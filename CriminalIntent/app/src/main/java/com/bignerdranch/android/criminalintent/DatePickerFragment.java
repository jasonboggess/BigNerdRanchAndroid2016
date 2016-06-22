package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.bignerdranch.android.criminalintent.databinding.DialogDateBinding;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by jboggess on 6/22/16.
 */
public class DatePickerFragment extends DialogFragment implements DialogInterface.OnClickListener {

	private static final String ARG_DATE = "date";
	public static final String EXTRA_DATE = "com.bignerdranch.android.criminalintent.DatePickerFragment.date";

	private DialogDateBinding binding;

	public static DatePickerFragment newInstance(Date date) {
		Bundle args = new Bundle();
		args.putSerializable(ARG_DATE, date);

		DatePickerFragment datePickerFragment = new DatePickerFragment();
		datePickerFragment.setArguments(args);
		return datePickerFragment;
	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		binding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.dialog_date, null, false);

		Date date = (Date) getArguments().getSerializable(ARG_DATE);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);

		binding.dialogDateDatePicker.init(year, month, day, null);

		return new AlertDialog.Builder(getActivity())
				.setView(binding.getRoot())
				.setTitle(R.string.date_picker_title)
				.setPositiveButton(android.R.string.ok, this)
				.create();

	}

	private void sendResult(int resultCode, Date date) {
		if (getTargetFragment() == null) {
			return;
		}

		Intent intent = new Intent();
		intent.putExtra(EXTRA_DATE, date);


		getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
	}

	@Override
	public void onClick(DialogInterface dialogInterface, int i) {
		int year = binding.dialogDateDatePicker.getYear();
		int month = binding.dialogDateDatePicker.getMonth();
		int day = binding.dialogDateDatePicker.getDayOfMonth();

		Date date = new GregorianCalendar(year, month, day).getTime();
		sendResult(Activity.RESULT_OK, date);

	}
}
