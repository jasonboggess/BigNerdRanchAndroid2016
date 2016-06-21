package com.bignerdranch.android.criminalintent;

import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
			Toast.makeText(getActivity(), crime.getTitle() + " clicked!", Toast.LENGTH_SHORT).show();
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

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		binding = (FragmentCrimeListBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_crime_list, container, false);
		binding.crimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

		updateUI();
		return binding.getRoot();
	}

	private void updateUI() {
		CrimeLab crimeLab = CrimeLab.getInstance(getActivity());
		crimeAdapter = new CrimeAdapter(crimeLab.getCrimes());
		binding.crimeRecyclerView.setAdapter(crimeAdapter);
	}
}
