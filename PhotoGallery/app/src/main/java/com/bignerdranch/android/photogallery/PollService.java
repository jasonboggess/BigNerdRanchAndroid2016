package com.bignerdranch.android.photogallery;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.List;

/**
 * Created by jboggess on 6/23/16.
 */
public class PollService extends IntentService {

	private static final String TAG = PollService.class.getSimpleName();
	private static final int POLL_INTERVAL = 1000 * 60;

	public static Intent newIntent(Context context) {
		return new Intent(context, PollService.class);
	}

	public static void setServiceAlarm(Context context, boolean isOn) {
		Intent intent = PollService.newIntent(context);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
		if (isOn) {
			alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), POLL_INTERVAL, pendingIntent);
		} else {
			alarmManager.cancel(pendingIntent);
			pendingIntent.cancel();
		}
	}

	public static boolean isServiceAlarmOn(Context context) {
		Intent intent = PollService.newIntent(context);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE);

		return pendingIntent != null;
	}

	public PollService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (!isNetworkAvailableAndConnected()) {
			return;
		}

		String query = QueryPreferences.getStoredQuery(this);
		String lastResultId = QueryPreferences.getLastResultId(this);

		List<GalleryItem> items;

		if (query == null) {
			items = new FlickrFetchr().fetchRecentPhotos();
		} else {
			items = new FlickrFetchr().searchPhotos(query);
		}

		if (items.size() == 0) {
			return;
		}

		String resultId = items.get(0).getId();

		if (resultId.equals(lastResultId)) {
			Log.i(TAG, "Got an old result: " + resultId);
		} else {
			Log.i(TAG, "Got an new result: " + resultId);

			Resources resources = getResources();
			Intent newIntent = PhotoGalleryActivity.newIntent(this);
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, newIntent, 0);

			Notification notification = new NotificationCompat.Builder(this)
					.setTicker(resources.getString(R.string.new_pictures_title))
					.setSmallIcon(R.mipmap.ic_launcher)
					.setContentTitle(resources.getString(R.string.new_pictures_title))
					.setContentText(resources.getString(R.string.new_pictures_text))
					.setContentIntent(pendingIntent)
					.setAutoCancel(true)
					.build();

			NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
			notificationManager.notify(0, notification);
		}

		QueryPreferences.setLastResultId(this, resultId);
	}

	private boolean isNetworkAvailableAndConnected() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

		boolean isNetworkAvailable = connectivityManager.getActiveNetworkInfo() != null;

		return isNetworkAvailable && connectivityManager.getActiveNetworkInfo().isConnected();
	}
}
