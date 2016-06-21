package com.bignerdranch.android.criminalintent;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.util.UUID;

/**
 * Created by jboggess on 6/21/16.
 */
public class Crime extends BaseObservable {

	private UUID id;
	private String title;

	@Bindable
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
		notifyPropertyChanged(com.bignerdranch.android.criminalintent.BR.id);
	}

	@Bindable
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		notifyPropertyChanged(com.bignerdranch.android.criminalintent.BR.title);
	}
}
