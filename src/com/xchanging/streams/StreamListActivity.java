package com.xchanging.streams;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.xchanging.streams.PBProviderMetadata.AccessRightsTableMetaData;

public class StreamListActivity extends ListActivity implements OnItemClickListener{
	private final String TAG = "StreamListActivity";
	private Context context;
	PBProviderObserver pbObserver;
	
	Handler mProgressHandler;
	SimpleCursorAdapter mCursorAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		context = getApplicationContext();
		/*ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1,
				new String[]{"Frogasia","Roadshow", "Yes", "1Bestari", "MOE"});
		this.setListAdapter(adapter);*/
		
				
		context.startService(new Intent(context, PBService.class));
		
	    String[] proj = { AccessRightsTableMetaData._ID, AccessRightsTableMetaData.BODY_TEXT };
	    Cursor c = getContentResolver().query(AccessRightsTableMetaData.CONTENT_URI, proj, null, null, null);
	    
		String[] cols = new String[] {AccessRightsTableMetaData.BODY_TEXT};
		int[] views = new int[] {android.R.id.text1};
				
		mCursorAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_1, c, cols, views, 0);
		this.setListAdapter(mCursorAdapter);
		
		
		mProgressHandler = new Handler() {
		    @Override
		    public void handleMessage(Message msg) {
			    String[] proj = { AccessRightsTableMetaData._ID, AccessRightsTableMetaData.BODY_TEXT };
			    Cursor c = getContentResolver().query(AccessRightsTableMetaData.CONTENT_URI, proj, null, null, null);
			    
		    	mCursorAdapter.changeCursor(c);
		    	mCursorAdapter.notifyDataSetChanged();
		    	
		    	
			    /*String[] proj = { AccessRightsTableMetaData._ID, AccessRightsTableMetaData.BODY_TEXT };
			    Cursor c = getContentResolver().query(AccessRightsTableMetaData.CONTENT_URI, proj, null, null, null);
			    
				String[] cols = new String[] {AccessRightsTableMetaData.BODY_TEXT};
				int[] views = new int[] {android.R.id.text1};
				
				mCursorAdapter = new SimpleCursorAdapter(StreamListActivity.this,
						android.R.layout.simple_list_item_1, c, cols, views, 0);
		    	StreamListActivity.this.setListAdapter(mCursorAdapter);*/
		    	
		        super.handleMessage(msg);
		    }
		};
		pbObserver = new PBProviderObserver(mProgressHandler, mCursorAdapter);
	}
	
	@Override
	protected void onStop()
	{
		super.onStop();
		getContentResolver().unregisterContentObserver(pbObserver);
	}

	
	@Override
	protected void onStart()
	{
		super.onStart();
		getContentResolver().registerContentObserver(AccessRightsTableMetaData.CONTENT_URI, true, pbObserver);
	}

	@Override
	public void onItemClick(AdapterView<?> adView, View target, int position, long id) {
		Log.e("ListViewActivity", "in onItemClick with " + ((TextView) target).getText()
		+
		". Position = " + position + ". Id = " + id);
 
		//Intent intent = new Intent(Intent.ACTION_VIEW, selectedPerson);
		//startActivity(intent);
	}
}
