package com.xchanging.settings;

import android.content.Context;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

import com.debasmita.clickshare.R;

public class AlbumListPrefActivity extends PreferenceActivity implements OnPreferenceChangeListener{
    String pref;
    
    Context mContext;
 
     
    private static final String PREF_ALBUM_NAME = "pref_album_name";
    ListPreference mAlbumListPref;
	/** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getApplicationContext();
        addPreferencesFromResource(R.xml.album_pref);
        PreferenceScreen prefs = getPreferenceScreen();
 
    
        mAlbumListPref = (ListPreference) prefs.findPreference(PREF_ALBUM_NAME);  
        mAlbumListPref.setOnPreferenceChangeListener(this);          
 
    }
	@Override
	public boolean onPreferenceChange(Preference arg0, Object arg1) {
		// TODO Auto-generated method stub
		return false;
	}

}
