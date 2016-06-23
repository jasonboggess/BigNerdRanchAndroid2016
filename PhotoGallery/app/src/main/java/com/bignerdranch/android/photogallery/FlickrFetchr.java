package com.bignerdranch.android.photogallery;

import android.net.Uri;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jboggess on 6/23/16.
 */
public class FlickrFetchr {

	private static final String TAG = FlickrFetchr.class.getSimpleName();
	private static final String API_KEY = "9b1c25cfa71f1a983da76fed7ddc33c8";
	private static final String FETCH_RECENTS_METHOD = "flickr.photos.getRecent";
	private static final String SEARCH_METHOD = "flickr.photos.search";

	private static final Uri endpoint
			= Uri.parse("https://api.flickr.com/services/rest/")
			.buildUpon()
			.appendQueryParameter("api_key", API_KEY)
			.appendQueryParameter("format", "json")
			.appendQueryParameter("nojsoncallback", "1")
			.appendQueryParameter("extras", "url_s")
			.build();

	public byte[] getUrlBytes(String urlSpec) throws IOException {
		URL url = new URL(urlSpec);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			InputStream in = connection.getInputStream();

			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
			}

			IOUtils.copy(in, out);

			return out.toByteArray();
		} finally {
			connection.disconnect();
		}
	}

	public String getUrlString(String urlSpec) throws IOException {
		return new String(getUrlBytes(urlSpec));
	}

	private String buildUrl(String method, String query) {
		Uri.Builder uriBuilder =
				endpoint.buildUpon()
						.appendQueryParameter("method", method);

		if (method.equals(SEARCH_METHOD)) {
			uriBuilder.appendQueryParameter("text", query);
		}

		return uriBuilder.build().toString();
	}

	private List<GalleryItem> downloadGalleryItems(String url) {
		List<GalleryItem> items = new ArrayList<>();

		try {
			String jsonString = getUrlString(url);
			Log.i(TAG, "Received JSON: " + jsonString);

			JSONObject jsonBody = new JSONObject(jsonString);
			parseItems(items, jsonBody);
		} catch (IOException e) {
			Log.e(TAG, "Failed to fetch items", e);
		} catch (JSONException e) {
			Log.e(TAG, "Error parsing json", e);
		}

		return items;
	}

	public List<GalleryItem> fetchRecentPhotos() {
		return downloadGalleryItems(buildUrl(FETCH_RECENTS_METHOD, null));
	}

	public List<GalleryItem> searchPhotos(String query) {
		return downloadGalleryItems(buildUrl(SEARCH_METHOD, query));
	}


	private void parseItems(List<GalleryItem> items, JSONObject jsonBody) throws IOException, JSONException {
		JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
		JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");

		for (int i = 0; i < photoJsonArray.length(); i++) {
			JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);

			if (!photoJsonObject.has("url_s")) {
				continue;
			}

			GalleryItem item = new GalleryItem();
			item.setId(photoJsonObject.getString("id"));
			item.setCaption(photoJsonObject.getString("title"));
			item.setUrl(photoJsonObject.getString("url_s"));
			items.add(item);
		}
	}
}
