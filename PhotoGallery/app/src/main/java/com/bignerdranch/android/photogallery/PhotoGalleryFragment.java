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
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
	private MenuItem searchItem;
	private SearchView searchView;

	public static PhotoGalleryFragment newInstance() {
		return new PhotoGalleryFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
		updateItems();

		PollService.setServiceAlarm(getActivity(), true);

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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_photo_gallery, menu);

		searchItem = menu.findItem(R.id.menu_item_search);
		searchView = (SearchView) searchItem.getActionView();

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				QueryPreferences.setStoredQuery(getActivity(), query);
				searchView.clearFocus();
				updateItems();
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});
		searchView.setOnSearchClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				searchView.setQuery(QueryPreferences.getStoredQuery(getActivity()), false);
			}
		});

		if (QueryPreferences.getStoredQuery(getActivity()) != null) {
			searchView.setIconified(false);
			searchView.clearFocus();
		}


		MenuItem toggleItem = menu.findItem(R.id.menu_item_toggle_polling);
		if (PollService.isServiceAlarmOn(getActivity())) {
			toggleItem.setTitle(R.string.stop_polling);
		} else {
			toggleItem.setTitle(R.string.start_polling);
		}

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		thumbnailDownloader.clearQueue();
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


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_item_clear:
				QueryPreferences.setStoredQuery(getActivity(), null);
				searchView.setQuery(null, false);
				searchView.setIconified(true);
				updateItems();
				return true;
			case R.id.menu_item_toggle_polling:
				boolean shouldStartAlarm = !PollService.isServiceAlarmOn(getActivity());
				PollService.setServiceAlarm(getActivity(), shouldStartAlarm);
				getActivity().invalidateOptionsMenu();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void updateItems() {
		String query = QueryPreferences.getStoredQuery(getActivity());
		new FetchItemsTask(query).execute();
	}

	private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>> {

		private FlickrFetchr flickrFetchr = new FlickrFetchr();
		private String query;

		public FetchItemsTask(String query) {
			this.query = query;
		}

		@Override
		protected List<GalleryItem> doInBackground(Void... voids) {
			if (query == null) {
				return flickrFetchr.fetchRecentPhotos();
			}
			return flickrFetchr.searchPhotos(query);
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

	private static Drawable placeHolder;

	private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

		private List<GalleryItem> galleryItems;

		public PhotoAdapter(List<GalleryItem> galleryItems) {
			this.galleryItems = galleryItems;
			if (placeHolder == null) {
				placeHolder = getResources().getDrawable(R.drawable.loading);
			}
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
			holder.bindGalleryDrawable(null);
			thumbnailDownloader.queueThumbnail(holder, item.getUrl());
		}

		@Override
		public int getItemCount() {
			return galleryItems.size();
		}
	}
}
