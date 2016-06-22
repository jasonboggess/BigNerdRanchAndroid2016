package com.bignerdranch.android.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.Observable;

import com.bignerdranch.android.criminalintent.CrimeDbSchema.CrimeTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by jboggess on 6/21/16.
 */
public class CrimeLab extends Observable.OnPropertyChangedCallback {

	private static CrimeLab instance;

	private Context context;
	private SQLiteDatabase database;

	public CrimeLab(Context context) {
		this.context = context.getApplicationContext();
		database = new CrimeBaseHelper(this.context).getWritableDatabase();
	}


	public static CrimeLab getInstance(Context context) {
		if (instance == null) {
			instance = new CrimeLab(context);
		}
		return instance;
	}

	private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
		Cursor cursor = database.query(
				CrimeTable.NAME,
				null,
				whereClause,
				whereArgs,
				null,
				null,
				null
		);

		return new CrimeCursorWrapper(cursor);
	}

	public List<Crime> getCrimes() {
		List<Crime> crimes = new ArrayList<>();

		CrimeCursorWrapper cursor = queryCrimes(null, null);

		try {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Crime crime = cursor.getCrime();
				crimes.add(crime);
				watchCrime(crime);
				cursor.moveToNext();
			}
		} finally {
			cursor.close();
		}

		return crimes;
	}

	public Crime getCrime(UUID id) {
		CrimeCursorWrapper cursor = queryCrimes(
				CrimeTable.Cols.UUID + " = ?",
				new String[]{id.toString()});

		try {
			if (cursor.getCount() == 0) {
				return null;
			}
			cursor.moveToFirst();
			Crime crime = cursor.getCrime();
			watchCrime(crime);
			return crime;
		} finally {
			cursor.close();
		}
	}

	public void addCrime(Crime crime) {
		ContentValues contentValues = getContentValues(crime);
		database.insert(CrimeTable.NAME, null, contentValues);
	}

	private void updateCrime(Crime crime) {
		String uuidString = crime.getId().toString();
		ContentValues contentValues = getContentValues(crime);

		database.update(CrimeTable.NAME, contentValues, CrimeTable.Cols.UUID + "= ? ",
				new String[]{uuidString});
	}

	public void deleteCrime(Crime crime) {
		String uuidString = crime.getId().toString();
		crime.removeOnPropertyChangedCallback(this);
		database.delete(CrimeTable.NAME, CrimeTable.Cols.UUID + "= ? ", new String[]{uuidString});
	}

	private void watchCrime(Crime crime) {
		crime.addOnPropertyChangedCallback(this);
	}

	@Override
	public void onPropertyChanged(Observable observable, int i) {
		if (!(observable instanceof Crime)) {
			return;
		}

		//TODO: Update in batches as this might be very commit heavy
		updateCrime((Crime)observable);
	}


	private static ContentValues getContentValues(Crime crime) {
		ContentValues values = new ContentValues();
		values.put(CrimeTable.Cols.UUID, crime.getId().toString());
		values.put(CrimeTable.Cols.TITLE, crime.getTitle());
		values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
		values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);

		return values;
	}


}
