package com.bignerdranch.android.criminalintent;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;

import com.bignerdranch.android.criminalintent.databinding.ActivityCrimePagerBinding;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity {

	private static final String EXTRA_CRIME_ID = "om.bignerdranch.android.criminalintent.CrimePagerActivity.crime_id";

	public static Intent newIntent(Context packageContext, UUID crimeId) {
		Intent intent = new Intent(packageContext, CrimePagerActivity.class);
		intent.putExtra(EXTRA_CRIME_ID, crimeId);
		return intent;
	}

	private static class CrimeFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

		private List<Crime> crimes;
		public CrimeFragmentStatePagerAdapter(List<Crime> crimes, FragmentManager fm) {
			super(fm);
			this.crimes = crimes;
		}

		@Override
		public Fragment getItem(int position) {
			Crime crime = crimes.get(position);
			return CrimeFragment.newInstance(crime.getId());
		}

		@Override
		public int getCount() {
			return crimes.size();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityCrimePagerBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_crime_pager);

		List<Crime> crimes = CrimeLab.getInstance(this).getCrimes();
		FragmentManager fragmentManager = getFragmentManager();
		binding.activityCrimePagerViewPager.setAdapter(new CrimeFragmentStatePagerAdapter(crimes, fragmentManager));

		UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);

		for (int i = 0; i < crimes.size(); i++) {
			if (crimes.get(i).getId().equals(crimeId)) {
				binding.activityCrimePagerViewPager.setCurrentItem(i);
				break;
			}
		}
	}
}
