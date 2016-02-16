package com.almexe.lingvaproject.pages;

import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import com.almexe.lingvaproject.R;
import com.almexe.lingvaproject.utils.TimePreference;

public class Settings extends PreferenceFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.background_settings);
	}

}