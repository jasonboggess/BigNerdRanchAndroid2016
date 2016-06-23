package com.bignerdranch.android.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jboggess on 6/23/16.
 */
public class ThumbnailDownloader<T> extends HandlerThread {

	private static final String TAG = "ThumbnailDownloader";
	private static final int MESSAGE_DOWNLOAD = 0;

	private boolean hasQuit = false;
	private Handler requestHandler;
	private ConcurrentMap<T, String> requestMap = new ConcurrentHashMap<>();
	private ConcurrentMap<String, Bitmap> photoCache = new ConcurrentHashMap<>();

	private Handler responseHandler;
	private ThumbnailDownloadListener<T> thumbnailDownloadListener;

	private ExecutorService executorService;

	public interface ThumbnailDownloadListener<T> {
		void onThumbnailDownloaded(T target, Bitmap thumbnail);
	}

	public ThumbnailDownloader(Handler responseHandler) {
		super(TAG);
		this.responseHandler = responseHandler;
		executorService = Executors.newFixedThreadPool(10);
	}

	public void setThumbnailDownloadListener(ThumbnailDownloadListener<T> thumbnailDownloadListener) {
		this.thumbnailDownloadListener = thumbnailDownloadListener;
	}

	@Override
	protected void onLooperPrepared() {
		requestHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == MESSAGE_DOWNLOAD) {
					T target = (T) msg.obj;
					Log.i(TAG, "Processing message for url" + requestMap.get(target));
					handleRequest(target);
				}
			}
		};
	}

	public void clearQueue() {
		requestHandler.removeMessages(MESSAGE_DOWNLOAD);
	}

	@Override
	public boolean quit() {
		hasQuit = true;
		return super.quit();
	}


	public void queueThumbnail(T target, String url) {
		Log.i(TAG, "Got url " + url);

		if (url == null) {
			requestMap.remove(target);
		} else {
			requestMap.put(target, url);
			requestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget();
		}
	}

	public void handleRequest(final T target) {

		executorService.submit(new Runnable() {
			@Override
			public void run() {
				try {
					final String url = requestMap.get(target);

					if (url == null) {
						return;
					}


					if (photoCache.containsKey(url)) {
						sendBitmap(url, target, photoCache.get(url));
						return;
					}


					byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
					final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
					Log.i(TAG, "Bitmap Created");
					photoCache.put(url, bitmap);
					sendBitmap(url, target, bitmap);

				} catch (IOException ioe) {
					Log.e(TAG, "Error downloading image", ioe);
				}
			}
		});

	}

	private void sendBitmap(final String url, final T target, final Bitmap thumbnail) {
		responseHandler.post(new Runnable() {
			@Override
			public void run() {
				if (!url.equals(requestMap.get(target)) || hasQuit) {
					return;
				}

				requestMap.remove(target);
				thumbnailDownloadListener.onThumbnailDownloaded(target, thumbnail);
			}
		});
	}
}
