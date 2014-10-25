package com.debasmita.clickshare;

import java.io.File;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;

public class FrogUtility {
	Context FrogContext;
	private static final String TAG = "MainFragment";
	// THE SHAREDPREFERENCE INSTANCE
	SharedPreferences sharedPrefs;
	Editor editor;
	// A CONSTANT STRING TO PROVIDE A NAME TO THE SHAREDPREFERENCE FILE
	private static final String PRIVATE_PREF = "frogasiauploader";
	private static final String KEY_PAGE_TOKEN_SET = "page_token_set";
	private static final String KEY_ALBUM_SELECTED = "album_selected";
	private static final String KEY_EVT_ALBUM_ID = "evt_album_id";
	private static final String KEY_EVT_ALBUM_NODE_LIST = "evt_album_node_list";
	
	private Object tokenLock = new Object();
    private Object albumLock = new Object();	
	public FrogUtility(Context context)
	{
		FrogContext = context;
	}
	
	public int getPageTokenProgress()
	{
		synchronized(tokenLock)
		{
			sharedPrefs = FrogContext.getSharedPreferences(PRIVATE_PREF, Context.MODE_PRIVATE);
			return sharedPrefs.getInt(KEY_PAGE_TOKEN_SET, 0);
		}
	}
	
	public int getAlbumUpdateProgress()
	{
		synchronized(albumLock)
		{
			sharedPrefs = FrogContext.getSharedPreferences(PRIVATE_PREF, Context.MODE_PRIVATE);
			return sharedPrefs.getInt(KEY_ALBUM_SELECTED, 0);
		}
	}
	
	public String getTodayEventAlbumId()
	{
		sharedPrefs = FrogContext.getSharedPreferences(PRIVATE_PREF, Context.MODE_PRIVATE);
		return sharedPrefs.getString(KEY_EVT_ALBUM_ID, "me");
	}
	
	public String getAlbumNodeListFromPref()
	{
		sharedPrefs = FrogContext.getSharedPreferences(PRIVATE_PREF, Context.MODE_PRIVATE);
		return sharedPrefs.getString(KEY_EVT_ALBUM_NODE_LIST, "");
	}
	
	public void setPageTokenPrefFlag(int progress)
	{
		synchronized(tokenLock)
		{
			sharedPrefs = FrogContext.getSharedPreferences(PRIVATE_PREF, Context.MODE_PRIVATE);
			// INSTANTIATE THE EDITOR INSTANCE
			editor = sharedPrefs.edit();
			// ADD VALUES TO THE PREFERENCES FILE
			editor.putInt(KEY_PAGE_TOKEN_SET, progress);
			editor.commit();
		}
	}
	
	public void setTodayEventAlbumId(String id)
	{
			sharedPrefs = FrogContext.getSharedPreferences(PRIVATE_PREF, Context.MODE_PRIVATE);
			// INSTANTIATE THE EDITOR INSTANCE
			editor = sharedPrefs.edit();
			// ADD VALUES TO THE PREFERENCES FILE
			editor.putString(KEY_EVT_ALBUM_ID, id);
			editor.commit();
	}
	
	public void setAlbumUpdateProgress(int flag)
	{
		synchronized(albumLock)
		{
			sharedPrefs = FrogContext.getSharedPreferences(PRIVATE_PREF, Context.MODE_PRIVATE);
			// INSTANTIATE THE EDITOR INSTANCE
			editor = sharedPrefs.edit();
			// ADD VALUES TO THE PREFERENCES FILE
			editor.putInt(KEY_ALBUM_SELECTED, flag);
			editor.commit();
		}
	}
	
	public void setAlbumNodeList(String listData)
	{
		sharedPrefs = FrogContext.getSharedPreferences(PRIVATE_PREF, Context.MODE_PRIVATE);
		// INSTANTIATE THE EDITOR INSTANCE
		editor = sharedPrefs.edit();
		// ADD VALUES TO THE PREFERENCES FILE
		editor.putString(KEY_EVT_ALBUM_NODE_LIST, listData);
		editor.commit();
	}

    public boolean isConnectingToInternet_()
    {
    	
    	return true;
    }
    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) FrogContext.getSystemService(Context.CONNECTIVITY_SERVICE);
          if (connectivity != null) 
          {
              NetworkInfo[] info = connectivity.getAllNetworkInfo();
              if (info != null) 
                  for (int i = 0; i < info.length; i++) 
                      if (info[i].getState() == NetworkInfo.State.CONNECTED)
                      {
                          return true;
                      }
 
          }
          return false;
    }
    
    public Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /**
     * Helper method to carry out upload operation to facebook
     */
    private void getAlbumIds(){
	    Session session = Session.getActiveSession();
	    if (session != null) {

		    Bundle params = new Bundle();
		    
		    Request.Callback callback= new Request.Callback() {
		    	
		        public void onCompleted(Response response) {
		        	Log.i(TAG, "get album details Complete");
		        	
		            FacebookRequestError error = response.getError();
		            if (error != null) {
		                Toast.makeText(FrogContext
		                     .getApplicationContext(),
		                     error.getErrorMessage(),
		                     Toast.LENGTH_SHORT).show();
		                Log.e(TAG, "" + error.getErrorMessage());
		                } else {
/*
    	    		            JSONObject graphResponse = response
    	    		                                       .getGraphObject()
    	    		                                       .getInnerJSONObject();*/
	    		            Log.d("Facebook-Example", "Response: " + response.toString());
	    		            }
		            
		        }
		    };
		    Log.i(TAG, "getting album details");

		    Request request = new Request(session, "frogasia123/albums", null, 
		                          HttpMethod.GET, callback);

		    RequestAsyncTask task = new RequestAsyncTask(request);
		    task.execute();
		} 
    }
    

	public String getPhotoBoothFilePath (Context appContext, String fileName)
	{
    	boolean sdcard = true;
		String reportPath = null;
		
		// checking available storage
		String state = Environment.getExternalStorageState();
		if (!Environment.MEDIA_MOUNTED.equals(state)) {
			sdcard = false;
		}
		else {
			File root = Environment.getExternalStorageDirectory();
			if (!root.canWrite()) {
				sdcard = false;
			}
		}
		
		if (sdcard)
		{
			reportPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName;
		}
		else
		{
			File f1 = appContext.getDir("files", Context.MODE_PRIVATE);
			String reportAbsPath = f1.getAbsolutePath();
			reportPath = reportAbsPath + "/" + fileName;
		}
		Log.i(TAG, "Report Path:" + reportPath);
		return reportPath;
	}

}
