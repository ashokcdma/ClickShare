package com.xchanging.streams;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.SimpleCursorAdapter;

public class PBProviderObserver extends ContentObserver {
	private static final String TAG = "PBProviderObserver";
	SimpleCursorAdapter PBAdapter;
	Handler handler;
	public PBProviderObserver(Handler handler, SimpleCursorAdapter adapter)
	{
		super(handler);
		PBAdapter = adapter;
		this.handler = handler;
	}
	
	@Override
	public void onChange(boolean selfChange) {
		this.onChange(selfChange, null);
		Log.e(TAG, "GEtting the response in first");
	}
	
	@Override
	public void onChange(boolean selfChange, Uri uri) {
		super.onChange(selfChange);
		handler.post(null);
		Log.e(TAG, "GEtting the response in second");
	}
}
