package com.bignerdranch.android.criminalintent;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bignerdranch.android.criminalintent.CrimeDbSchema.CrimeTable;

import java.util.Date;
import java.util.UUID;

/**
 * Created by jboggess on 6/22/16.
 */
public class CrimeCursorWrapper extends CursorWrapper {
	public CrimeCursorWrapper(Cursor cursor) {
		super(cursor);
	}

	public Crime getCrime() {
		String uuid = getString(getColumnIndex(CrimeTable.Cols.UUID));
		String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
		long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
		int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));

		Crime crime = new Crime(UUID.fromString(uuid));
		crime.setTitle(title);
		crime.setDate(new Date(date));
		crime.setSolved(isSolved != 0);

		return crime;
	}
}
