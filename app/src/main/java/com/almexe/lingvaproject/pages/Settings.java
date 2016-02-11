package com.almexe.lingvaproject.pages;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.almexe.lingvaproject.R;

public class Settings extends PreferenceActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
	    super.onCreate(savedInstanceState);
	    addPreferencesFromResource(R.xml.background_settings);   
	}

}
