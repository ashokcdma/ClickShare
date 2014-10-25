package com.xchanging.streams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.xchanging.streams.PBProviderMetadata.AccessRightsTableMetaData;

public class PBService extends Service{
	private static final String TAG = "PBService";
	private Context context;
	@Override
	public void onCreate() {
		super.onCreate();
		context = getBaseContext();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flag, int startId)
	{
		//Call the IntentService "onstart"
		super.onStart(intent, startId);
		getStreamList();
		return Service.START_NOT_STICKY;
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
    /**
     * Helper method to carry out upload operation to facebook
     */
    private void getStreamList(){
    	Log.e(TAG, "get Stream List called");
	    Session session = Session.getActiveSession();
	    if (session != null) {
	    	Bundle params = new Bundle();
            
            String pageUrl = "frogasia123/feed";
		    Request.Callback callback= new Request.Callback() {
		    	
		        public void onCompleted(Response response) {
		            FacebookRequestError error = response.getError();
		            if (error != null) {
		            	Log.e(TAG, "ERROR:" + error.getErrorMessage());
		                Toast.makeText(context,
	                     error.getErrorMessage(),
	                     Toast.LENGTH_SHORT).show();
	                } else {
	                	Log.e(TAG, "SUCCESS:");
    		            JSONObject graphResponse = response
    		                                       .getGraphObject()
    		                                       .getInnerJSONObject();
    		            Log.i(TAG, "graphResponse:" + graphResponse.toString());
    		            try {
							
							JSONArray eventArray = graphResponse.getJSONArray("data");
							for(int i=0, j= eventArray.length(); i < j; i++)
							{
								JSONObject evtObj = eventArray.getJSONObject(i);
								String eventid = evtObj.getString("id");
								String bodyPic = evtObj.optString("picture");
								String bodyText = evtObj.optString("message");
								
								JSONObject profileObj = evtObj.getJSONObject("from");
								String username = profileObj.optString("name");
								String profileId = evtObj.getString("id");
								//TODO: currently profile id is filed in profile picture
								insertFacebookFeeds(context, eventid, username, profileId, bodyPic, bodyText);
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

	                }
		            stopSelf();
		        }
		    };
		    Request request = new Request(session, pageUrl, params, 
		                          HttpMethod.GET, callback);

		    RequestAsyncTask task = new RequestAsyncTask(request);
		    task.execute();
	    }
    }
    
    public void insertFacebookFeeds(Context context, String eventid, String username, 
    									String profilePic, String bodyPic, String bodyText)
    {
	    Log.d(TAG,"inserting feeds to DB");
	    ContentValues cv = new ContentValues();
	    cv.put(AccessRightsTableMetaData.EVENT_ID, eventid);
	    cv.put(AccessRightsTableMetaData.BODY_PICTURE, bodyPic);
	    cv.put(AccessRightsTableMetaData.BODY_TEXT, bodyText);
	    cv.put(AccessRightsTableMetaData.EVENT_USER_NAME, username);
	    cv.put(AccessRightsTableMetaData.EVENT_USER_PROFILE_PIC, profilePic);
	    ContentResolver cr = context.getContentResolver();
	    Uri uri = AccessRightsTableMetaData.CONTENT_URI;
	    Uri insertedUri = cr.insert(uri, cv);
	    Log.d(TAG,"inserted uri:" + insertedUri);
    }

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
