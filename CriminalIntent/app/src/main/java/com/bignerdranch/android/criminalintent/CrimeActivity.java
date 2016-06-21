package com.bignerdranch.android.criminalintent;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;

import java.util.UUID;

public class CrimeActivity extends SingleFragmentActivity {

	private static final String EXTRA_CRIME_ID = "om.bignerdranch.android.criminalintent.CrimeActivity.crime_id";

	public static Intent newIntent(Context packageContext, UUID crimeId) {
		Intent intent = new Intent(packageContext, CrimeActivity.class);
		intent.putExtra(EXTRA_CRIME_ID, crimeId);
		return intent;
	}

	@Override
	protected Fragment createFragment() {
		UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
		return CrimeFragment.newInstance(crimeId);
	}

}
