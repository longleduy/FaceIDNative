package com.faceidnative.faceidapplication.gui;

import com.faceidnative.faceidapplication.utils.BaseActivity;

import android.os.Bundle;

public class SettingsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}