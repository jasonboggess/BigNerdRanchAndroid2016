package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ShareCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.android.criminalintent.databinding.FragmentCrimeBinding;

import java.io.File;
import java.util.Date;
import java.util.UUID;

/**
 * Created by jboggess on 6/21/16.
 */
public class CrimeFragment extends Fragment implements PhotoButtonEventHandler {

	private Crime crime;
	private File photoFile;
	private static final String ARGUMENT_CRIME_ID = "crime_id";
	private static final String DIALOG_DATE = "dialog_date";
	private static final int REQUEST_DATE = 0;
	private static final int REQUEST_CONTACT = 1;
	private static final int REQUEST_PHOTO = 1;
	private FragmentCrimeBinding binding;

	public static CrimeFragment newInstance(UUID crimeId) {
		Bundle bundle = new Bundle();
		bundle.putSerializable(ARGUMENT_CRIME_ID, crimeId);

		CrimeFragment fragment = new CrimeFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		UUID crimeId = (UUID) getArguments().getSerializable(ARGUMENT_CRIME_ID);
		crime = CrimeLab.getInstance(getActivity()).getCrime(crimeId);
		photoFile = CrimeLab.getInstance(getActivity()).getPhotoFile(crime);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		binding = (FragmentCrimeBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_crime, container, false);
		View view = binding.getRoot();

		Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		PackageManager packageManager = getActivity().getPackageManager();
		if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
			binding.crimeSuspect.setEnabled(false);
		}


		Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		binding.crimeCameraAndTitle.crimeCamera.setEnabled(
				photoFile != null
						&& captureImage.resolveActivity(packageManager) != null
		);

		binding.setCrime(crime);
		binding.setEventHandler(this);
		binding.crimeCameraAndTitle.setCrime(crime);
		binding.crimeCameraAndTitle.setPhotoButtonEventHandler(this);

		updatePhotoView();
		return view;
	}

	public void onDateButtonClick(View view) {
		FragmentManager fragmentManager = getFragmentManager();
		DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(crime.getDate());
		datePickerFragment.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
		datePickerFragment.show(fragmentManager, DIALOG_DATE);
	}

	public void onDeleteButtonClicked(View view) {
		new AlertDialog.Builder(getActivity())
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("Delete")
				.setMessage("Are you sure you want to delete this crime?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						CrimeLab.getInstance(CrimeFragment.this.getActivity()).deleteCrime(crime);
						CrimeFragment.this.getActivity().finish();
					}

				})
				.setNegativeButton("No", null)
				.show();

	}

	public void onChooseSuspectClicked(View view) {
		Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		startActivityForResult(pickContact, REQUEST_CONTACT);
	}

	public void sendCrimeReportClicked(View view) {
		Intent intent = ShareCompat.IntentBuilder
				.from(getActivity())
				.setType("text/plain")
				.setChooserTitle(R.string.send_report)
				.setText(getCrimeReport())
				.setSubject(getString(R.string.crime_report_subject))
				.createChooserIntent();

		startActivity(intent);
	}

	public String getCrimeReport() {
		String crimeTitle = crime.getTitle() == null ? "Untitled" : crime.getTitle();
		String solvedString = getString(crime.isSolved() ? R.string.crime_report_solved : R.string.crime_report_unsolved);
		String dateString = DateUtils.formatDate(crime.getDate());
		String suspectString = crime.getSuspect() == null ? getString(R.string.crime_report_no_suspect) : getString(R.string.crime_report_suspect, crime.getSuspect());

		return getString(R.string.crime_report, crimeTitle, dateString, solvedString, suspectString);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			super.onActivityResult(requestCode, resultCode, data);
		}

		if (requestCode == REQUEST_DATE) {
			Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
			crime.setDate(date);
		} else if (requestCode == REQUEST_CONTACT && data != null) {
			Uri contactUri = data.getData();
			String[] queryFields = new String[]{
					ContactsContract.Contacts.DISPLAY_NAME
			};

			Cursor cursor = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);

			try {
				if (cursor == null || cursor.getCount() == 0) {
					return;
				}

				cursor.moveToFirst();
				String suspect = cursor.getString(0);
				crime.setSuspect(suspect);
			} finally {
				cursor.close();
			}
		} else if (requestCode == REQUEST_PHOTO) {
			updatePhotoView();
		}
	}

	@Override
	public void onPhotoButtonPressed(View view) {
		Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		Uri uri = Uri.fromFile(photoFile);
		captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

		startActivityForResult(captureImage, REQUEST_PHOTO);

	}

	private void updatePhotoView() {
		Bitmap bitmap = null;
		if (photoFile != null && photoFile.exists()) {
			bitmap = PictureUtils.getScaledBitmap(photoFile.getPath(), getActivity());
		}
		binding.crimeCameraAndTitle.crimePhoto.setImageBitmap(bitmap);
	}
}
