package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.android.criminalintent.databinding.FragmentCrimeBinding;

import java.util.Date;
import java.util.UUID;

/**
 * Created by jboggess on 6/21/16.
 */
public class CrimeFragment extends Fragment {

	private Crime crime;
	private static final String ARGUMENT_CRIME_ID = "crime_id";
	private static final String DIALOG_DATE = "dialog_date";
	private static final int REQUEST_DATE = 0;

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
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		FragmentCrimeBinding binding = (FragmentCrimeBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_crime, container, false);
		View view = binding.getRoot();

		binding.setCrime(crime);
		binding.setEventHandler(this);
		return view;
	}

	public void onDateButtonClick(View view) {
		FragmentManager fragmentManager = getFragmentManager();
		DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(crime.getDate());
		datePickerFragment.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
		datePickerFragment.show(fragmentManager, DIALOG_DATE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			super.onActivityResult(requestCode, resultCode, data);
		}

		if (requestCode == REQUEST_DATE) {
			Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
			crime.setDate(date);
		}
	}
}
