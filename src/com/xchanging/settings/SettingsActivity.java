package com.xchanging.settings;

import com.debasmita.clickshare.R;

import android.app.Activity;
import android.os.Bundle;

public class SettingsActivity extends Activity{

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        {
            this.setTitle(this.getResources().getString(R.string.app_name_blank));
            getActionBar().setIcon(R.drawable.photobooth_img_1);
        }
        
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
