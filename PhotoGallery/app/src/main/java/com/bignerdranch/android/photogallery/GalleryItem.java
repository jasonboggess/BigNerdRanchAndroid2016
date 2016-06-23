package com.bignerdranch.android.photogallery;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

/**
 * Created by jboggess on 6/23/16.
 */
public class GalleryItem extends BaseObservable {

	private String caption;
	private String id;
	private String url;

	@Bindable
	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
		notifyPropertyChanged(com.bignerdranch.android.photogallery.BR.caption);
	}

	@Bindable
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
		notifyPropertyChanged(com.bignerdranch.android.photogallery.BR.id);
	}

	@Bindable
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
		notifyPropertyChanged(com.bignerdranch.android.photogallery.BR.url);
	}

	@Override
	public String toString() {
		return caption;
	}
}
