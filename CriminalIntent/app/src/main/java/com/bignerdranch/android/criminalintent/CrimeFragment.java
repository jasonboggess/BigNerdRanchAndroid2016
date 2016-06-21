package com.bignerdranch.android.criminalintent;

import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.android.criminalintent.databinding.FragmentCrimeBinding;

import java.util.UUID;

/**
 * Created by jboggess on 6/21/16.
 */
public class CrimeFragment extends Fragment {

	private Crime crime;
	private static final String ARGUMENT_CRIME_ID = "crime_id";

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
		return view;
	}
}
