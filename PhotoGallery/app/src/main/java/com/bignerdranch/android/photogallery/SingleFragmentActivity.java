package com.bignerdranch.android.photogallery;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by jboggess on 6/23/16.
 */
public abstract class SingleFragmentActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment);

		loadAndBindFragments();
	}

	protected abstract Fragment createFragment();

	private void loadAndBindFragments() {

		FragmentManager fragmentManager = getFragmentManager();
		Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);

		if (fragment == null) {
			fragment = createFragment();
			fragmentManager
					.beginTransaction()
					.add(R.id.fragment_container, fragment)
					.commit();
		}
	}
}

