package com.bignerdranch.android.criminalintent;

import android.app.Fragment;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.android.criminalintent.databinding.FragmentCrimeListBinding;
import com.bignerdranch.android.criminalintent.databinding.ListItemCrimeBinding;

import java.util.List;

/**
 * Created by jboggess on 6/21/16.
 */
public class CrimeListFragment extends Fragment {

	public class CrimeHolder extends RecyclerView.ViewHolder {
		private ListItemCrimeBinding listItemCrimeBinding;

		public CrimeHolder(ListItemCrimeBinding listItemCrimeBinding) {
			super(listItemCrimeBinding.getRoot());
			this.listItemCrimeBinding = listItemCrimeBinding;
			this.listItemCrimeBinding.setClickHandler(this);
		}

		public void setCrime(Crime crime) {
			listItemCrimeBinding.setCrime(crime);
		}

		public void onClick(View view) {
			Crime crime = listItemCrimeBinding.getCrime();
			Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
			startActivity(intent);
		}
	}

	private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
		private List<Crime> crimes;

		public CrimeAdapter(List<Crime> crimes) {
			this.crimes = crimes;
		}

		@Override
		public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
			ListItemCrimeBinding listItemCrimeBinding = ListItemCrimeBinding.inflate(layoutInflater, parent, false);

			return new CrimeHolder(listItemCrimeBinding);
		}

		@Override
		public void onBindViewHolder(CrimeHolder holder, int position) {
			Crime crime = crimes.get(position);
			holder.setCrime(crime);
		}

		@Override
		public int getItemCount() {
			return crimes.size();
		}
	}

	private CrimeAdapter crimeAdapter;
	private FragmentCrimeListBinding binding;

	private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

	private boolean subtitleVisible;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		binding = (FragmentCrimeListBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_crime_list, container, false);
		binding.crimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		if (savedInstanceState != null) {
			subtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
		}

		updateUI();
		return binding.getRoot();
	}

	@Override
	public void onResume() {
		super.onResume();
		binding.crimeRecyclerView.getAdapter().notifyDataSetChanged();
		updateSubtitle();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_crime_list, menu);

		MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
		if (subtitleVisible) {
			subtitleItem.setTitle(R.string.hide_subtitle);
		} else {
			subtitleItem.setTitle(R.string.show_subtitle);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case R.id.menu_item_new_crime:
				Crime crime = new Crime();
				CrimeLab.getInstance(getActivity()).addCrime(crime);
				Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
				startActivity(intent);
				return true;
			case R.id.menu_item_show_subtitle:
				subtitleVisible = !subtitleVisible;
				getActivity().invalidateOptionsMenu();
				updateSubtitle();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(SAVED_SUBTITLE_VISIBLE, subtitleVisible);
	}

	private void updateUI() {
		CrimeLab crimeLab = CrimeLab.getInstance(getActivity());
		crimeAdapter = new CrimeAdapter(crimeLab.getCrimes());
		binding.crimeRecyclerView.setAdapter(crimeAdapter);
	}

	private void updateSubtitle() {
		CrimeLab crimeLab = CrimeLab.getInstance(getActivity());
		int crimeCount = crimeLab.getCrimes().size();

		String subtitle = getString(R.string.subtitle_format, crimeCount);
		if (!subtitleVisible) {
			subtitle = null;
		}

		AppCompatActivity activity = (AppCompatActivity) getActivity();
		activity.getSupportActionBar().setSubtitle(subtitle);
	}
}
