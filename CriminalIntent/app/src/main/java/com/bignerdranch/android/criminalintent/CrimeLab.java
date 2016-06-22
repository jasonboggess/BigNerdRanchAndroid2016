package com.bignerdranch.android.criminalintent;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by jboggess on 6/21/16.
 */
public class CrimeLab {

	private static CrimeLab instance;
	private List<Crime> crimes;

	private Context context;

	public CrimeLab(Context context) {
		this.context = context;
		crimes = new ArrayList<>();
	}


	public static CrimeLab getInstance(Context context) {
		if (instance == null) {
			instance = new CrimeLab(context);
		}
		return instance;
	}

	public List<Crime> getCrimes() {
		return crimes;
	}

	public Crime getCrime(UUID id) {
		for (Crime crime : crimes) {
			if (crime.getId().equals(id)) {
				return crime;
			}
		}
		return null;
	}

	public void addCrime(Crime crime) {
		crimes.add(crime);
	}

	public void deleteCrime(Crime crime) {
		this.getCrimes().remove(crime);
	}
}
