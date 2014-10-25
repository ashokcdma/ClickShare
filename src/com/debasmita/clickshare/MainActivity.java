package com.debasmita.clickshare;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.xchanging.nfc.SharePhotoBooth;
import com.xchanging.settings.LowerVersionSettingsActivity;
import com.xchanging.settings.SettingsActivity;
import com.xchanging.streams.StreamListActivity;

public class MainActivity extends FragmentActivity {
	
	//private static final String TAG = "FROGASIA MAIN";
	private MainFragment mainFragment;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        {
	        this.setTitle(this.getResources().getString(R.string.app_name_blank));
	        getActionBar().setIcon(R.drawable.photobooth_img_1);
        }
        
        if (savedInstanceState == null) {
        	// Add the fragment on initial activity setup
        	mainFragment = new MainFragment();
            getSupportFragmentManager()
            .beginTransaction()
            .add(android.R.id.content, mainFragment)
            .commit();
        } else {
        	// Or set the fragment from restored state info
        	mainFragment = (MainFragment) getSupportFragmentManager()
        	.findFragmentById(android.R.id.content);
        }
        
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,FrogConstants.MENU_SETTINGS,0,this.getResources().getString(R.string.menu_settings));
        menu.add(0,FrogConstants.MENU_STREAMS,1,this.getResources().getString(R.string.menu_streams));
        
        int state = getNFCReadyState();
        if(state == FrogConstants.NFC_SUPPORTED || state == FrogConstants.NFC_NOT_ENABLED)
        {
        	menu.add(0,FrogConstants.MENU_PROMOTE_VIA_NFC,2,this.getResources().getString(R.string.menu_promote));
        }
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
	        case FrogConstants.MENU_PROMOTE_VIA_NFC:
	        	if(getNFCReadyState() == FrogConstants.NFC_SUPPORTED)
	        	{
	        		startActivity(new Intent(this, SharePhotoBooth.class));
	        	}
	        	else if(getNFCReadyState() == FrogConstants.NFC_NOT_ENABLED)
	        	{
					startActivityForResult(new Intent(
							android.provider.Settings.ACTION_WIRELESS_SETTINGS), 0);
	        	}
	        	else
	        	{
	        		//hide option
	        	}
	        	
	        return true;

	        case FrogConstants.MENU_STREAMS:
	        	
	        	startActivity(new Intent(this, StreamListActivity.class));
	        return true;
	        case FrogConstants.MENU_SETTINGS:
	        	
	        	if (Build.VERSION.SDK_INT < 11) {
	        	    startActivity(new Intent(this, LowerVersionSettingsActivity.class));
	        	} else {
	        		startActivity(new Intent(this, SettingsActivity.class));
	        	}
	        return true;
	        default:
	        	return super.onOptionsItemSelected(item);
    	}
    }
    

	private int getNFCReadyState()
	{
		int isNFC = FrogConstants.NFC_NOT_SUPPORTED;
		NfcManager manager = (NfcManager)getApplicationContext().getSystemService(Context.NFC_SERVICE);
		NfcAdapter adapter = manager.getDefaultAdapter();
		if (adapter != null)
		{
			if (!adapter.isEnabled())
			{
				isNFC = FrogConstants.NFC_NOT_ENABLED;
			}
			else
			{
				isNFC = FrogConstants.NFC_SUPPORTED;
			}
		}
		return isNFC;
	}
}
