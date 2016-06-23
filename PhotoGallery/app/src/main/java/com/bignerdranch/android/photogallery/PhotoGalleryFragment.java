package com.bignerdranch.android.photogallery;

import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bignerdranch.android.photogallery.databinding.FragmentPhotoGalleryBinding;
import com.bignerdranch.android.photogallery.databinding.GalleryItemBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jboggess on 6/23/16.
 */
public class PhotoGalleryFragment extends Fragment implements ThumbnailDownloader.ThumbnailDownloadListener<PhotoGalleryFragment.PhotoHolder> {

	private FragmentPhotoGalleryBinding binding;
	private static final String TAG = PhotoGalleryFragment.class.getSimpleName();
	private List<GalleryItem> items = new ArrayList<>();
	private ThumbnailDownloader<PhotoHolder> thumbnailDownloader;

	public static PhotoGalleryFragment newInstance() {
		return new PhotoGalleryFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

		new FetchItemsTask().execute();

		Handler responseHandler = new Handler();

		thumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
		thumbnailDownloader.setThumbnailDownloadListener(this);
		thumbnailDownloader.start();
		thumbnailDownloader.getLooper();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_photo_gallery, container, false);

		binding.fragmentPhotoGalleryRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

		setupAdapter();
		return binding.getRoot();
	}

	private void setupAdapter() {
		if (isAdded()) {
			binding.fragmentPhotoGalleryRecyclerView.setAdapter(new PhotoAdapter(items));
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		thumbnailDownloader.quit();
	}

	@Override
	public void onThumbnailDownloaded(PhotoHolder target, Bitmap thumbnail) {
		Drawable drawable = new BitmapDrawable(thumbnail);
		target.bindGalleryDrawable(drawable);
	}

	private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>> {
		@Override
		protected List<GalleryItem> doInBackground(Void... voids) {
			return new FlickrFetchr().fetchItems();
		}

		@Override
		protected void onPostExecute(List<GalleryItem> items) {
			PhotoGalleryFragment.this.items = items;
			setupAdapter();
		}
	}

	public class PhotoHolder extends RecyclerView.ViewHolder {

		private ImageView itemImageView;

		public PhotoHolder(GalleryItemBinding galleryItemBinding) {
			super(galleryItemBinding.getRoot());

			this.itemImageView = galleryItemBinding.fragmentPhotoGalleryImageView;
		}

		public void bindGalleryDrawable(Drawable drawable) {
			itemImageView.setImageDrawable(drawable);
		}
	}

	private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

		private List<GalleryItem> galleryItems;

		public PhotoAdapter(List<GalleryItem> galleryItems) {
			this.galleryItems = galleryItems;
		}

		@Override
		public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			LayoutInflater inflater = LayoutInflater.from(getActivity());
			GalleryItemBinding galleryItemBinding = DataBindingUtil.inflate(inflater, R.layout.gallery_item, parent, false);
			return new PhotoHolder(galleryItemBinding);
		}

		@Override
		public void onBindViewHolder(PhotoHolder holder, int position) {
			GalleryItem item = galleryItems.get(position);
			Drawable placeHolder = getResources().getDrawable(R.drawable.bill_up_close);
			holder.bindGalleryDrawable(placeHolder);
			thumbnailDownloader.queueThumbnail(holder, item.getUrl());
		}

		@Override
		public int getItemCount() {
			return galleryItems.size();
		}
	}
}
