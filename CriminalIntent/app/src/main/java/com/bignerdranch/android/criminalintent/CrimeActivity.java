package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

public class CrimeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime);

        loadAndBindFragments();
    }

    private void loadAndBindFragments(){

        FragmentManager fragmentManager = getFragmentManager();
        Fragment crimeFragment = fragmentManager.findFragmentById(R.id.fragment_container);

        if (crimeFragment==null){
            crimeFragment = new CrimeFragment();
            fragmentManager
                    .beginTransaction()
                        .add(R.id.fragment_container, crimeFragment)
                    .commit();
        }
    }
}
