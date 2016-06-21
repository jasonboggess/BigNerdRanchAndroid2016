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

		for (int i = 0; i < 100; i++) {
			Crime crime = new Crime();
			crime.setTitle(String.format("Crime #%d", i));
			crime.setSolved(i % 3 == 0);
			crimes.add(crime);
		}
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
}
