package com.bignerdranch.android.criminalintent;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.util.Date;
import java.util.UUID;

/**
 * Created by jboggess on 6/21/16.
 */
public class Crime extends BaseObservable {

	private UUID id;
	private String title;
	private Date date;
	private boolean solved;

	public Crime() {
		this(UUID.randomUUID());
	}

	public Crime(UUID id) {

		this.id = id;
		date = new Date();
	}

	@Bindable
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
		notifyPropertyChanged(com.bignerdranch.android.criminalintent.BR.id);
		notifyPropertyChanged(com.bignerdranch.android.criminalintent.BR.stringValue);
	}

	@Bindable
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		notifyPropertyChanged(com.bignerdranch.android.criminalintent.BR.title);
		notifyPropertyChanged(com.bignerdranch.android.criminalintent.BR.stringValue);
	}

	@Bindable
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
		notifyPropertyChanged(com.bignerdranch.android.criminalintent.BR.date);
		notifyPropertyChanged(com.bignerdranch.android.criminalintent.BR.stringValue);
	}

	@Bindable
	public boolean isSolved() {
		return solved;
	}

	public void setSolved(boolean solved) {
		this.solved = solved;
		notifyPropertyChanged(com.bignerdranch.android.criminalintent.BR.solved);
		notifyPropertyChanged(com.bignerdranch.android.criminalintent.BR.stringValue);
	}

	@Bindable
	public String getStringValue() {
		return this.toString();
	}


	@Override
	public String toString() {
		return "Crime{" +
				"id=" + id +
				", title='" + title + '\'' +
				", date=" + date +
				", solved=" + solved +
				'}';
	}
}
