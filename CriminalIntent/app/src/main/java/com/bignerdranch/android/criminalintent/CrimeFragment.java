package com.bignerdranch.android.criminalintent;

import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.android.criminalintent.databinding.FragmentCrimeBinding;

/**
 * Created by jboggess on 6/21/16.
 */
public class CrimeFragment extends Fragment {

	private Crime crime;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		crime = new Crime();
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
