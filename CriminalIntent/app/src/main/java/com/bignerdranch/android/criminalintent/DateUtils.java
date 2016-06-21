package com.bignerdranch.android.criminalintent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jboggess on 6/21/16.
 */
public class DateUtils {

	private final static DateFormat dateFormat = new SimpleDateFormat("EEEE, MMM d, yyyy H:mm a");

	public static String formatDate(Date date) {
		return dateFormat.format(date);
	}
}
