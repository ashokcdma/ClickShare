package com.debasmita.clickshare;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.debasmita.dropbox.RemoteClient;
import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.android.Utility;
import com.facebook.widget.LoginButton;
import com.todotxt.todotxttouch.TodoApplication;
import com.todotxt.todotxttouch.util.Util;
import com.xchanging.settings.AlbumArrayList;
import com.xchanging.settings.AlbumNode;

public class MainFragment extends Fragment {
	
	private static final String TAG = "MainFragment";
	private ImageButton captureBtn;//Aggrigation
	ProgressDialog dialogProgress;//Aggrigation
	private UiLifecycleHelper uiHelper;//Aggrigation
	FrogUtility utilityHelper; //Aggrigation
	TextView albumSelected;
	
	private String PhotoBoothFileName;
	private String PHOTOBOOTHPATH;
	private Uri picUri;	//captured picture uri
	ArrayList<String> pageTokenList = null;
	AlbumArrayList albumNodeList = null;
	int operationPendingState = FrogConstants.STAGE_RESET;
	boolean pendingUpload = false;
	boolean pendingAccountRequest = false;
	boolean requestPublish = false;
	
	private TodoApplication m_app;
	private Button dropboxLoginBtn;
	private BroadcastReceiver m_broadcastReceiver;
	RemoteClient dropboxClient;
	
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(final Session session, final SessionState state, final Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    
	@Override
	public View onCreateView(LayoutInflater inflater, 
			ViewGroup container, 
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main, container, false);
		
		LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
		albumSelected = (TextView) view.findViewById(R.id.albumSelected);

		authButton.setReadPermissions(FrogConstants.PERMISSIONS_READ);
		//authButton.setPublishPermissions(FrogConstants.PERMISSIONS);
		authButton.setFragment(this);
		
		//////////////////
		dropboxLoginBtn = (Button) view.findViewById(R.id.dropboxLogin);
		dropboxLoginBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startLogin();
			}
		});
		m_app = (TodoApplication) getActivity().getApplication();	
		
		
		
		//////////////
		utilityHelper = new FrogUtility(getActivity());
		//albumNodeList = AlbumArrayList.getAlbumListFromPref(getActivity());
		//Log.e(TAG, albumNodeList.toString());
		//authButton.setReadPermissions(Arrays.asList("user_likes", "user_status"));

        //retrieve a reference to the UI button
        captureBtn = (ImageButton)view.findViewById(R.id.capture_btn);
        //handle button clicks
        captureBtn.setOnClickListener(new CameraButtonOnClickListener());
        ImageButton albumBtn = (ImageButton)view.findViewById(R.id.albumButton);
        albumBtn.setOnClickListener(new AlbumButtonOnClickListener());
        
        ImageButton help = (ImageButton)view.findViewById(R.id.help);
        help.setOnClickListener(new HelpButtonOnClickListener());
        
        String PhotoBoothFolderPath = utilityHelper.getPhotoBoothFilePath(getActivity(), "PhotoBooth");
        File f2 = new File(PhotoBoothFolderPath);
		if(!f2.exists())
		{
			f2.mkdir();
		}
		PHOTOBOOTHPATH = PhotoBoothFolderPath;
        
		if (savedInstanceState != null) {
			PhotoBoothFileName = savedInstanceState.getString(FrogConstants.MEDIAURL_BEFORE_EMAIL);
			String tempStr = savedInstanceState.getString(FrogConstants.MEDIAURL_BEFORE_EMAIL_ICS);
			if (tempStr != null)
			{
				picUri = Uri.parse(tempStr);
				
			}
			else if (PhotoBoothFileName != null)
			{
				picUri = Uri.parse(PhotoBoothFileName);
			}
			operationPendingState =
					savedInstanceState.getInt(FrogConstants.PENDING_STATE_KEY, 0);
			if (operationPendingState == FrogConstants.STAGE_EMAIL)
			{
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            	boolean allowedEmail = sharedPref.getBoolean(FrogConstants.KEY_EMAIL_CHECKBOX, true);
            	if(allowedEmail)
            	{
                    //send the email also.
            		String filePathForEmail = getFilePathFromContentUri(picUri,
            				getActivity().getContentResolver());
            		Log.i(TAG, "FilePath-----" + filePathForEmail);
            		operationPendingState = FrogConstants.STAGE_RESET;
                    chooseEmail("gmail", filePathForEmail);
            	}
			}
		}

		return view;
	}
	
	void startLogin() {
		dropboxClient = m_app.getRemoteClientManager()
				.getRemoteClient();

		if (!dropboxClient.isAvailable()) {
			Log.d(TAG, "Remote service " + dropboxClient.getClass().getSimpleName()
					+ " is not available; aborting login");

			Util.showToastLong(m_app, R.string.toast_login_notconnected);
		} else {
			dropboxClient.startDropBoxLogin(this.getActivity());
		}
	}
	
	private void requestPublishPermissions(Session session, List<String> permission) {
		Log.e(TAG, "test called" + requestPublish);
        if (session != null && requestPublish == false) {
        	requestPublish = true;
    		Log.e(TAG, "requestPublishPermissions called");
            Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this, permission)
                    // demonstrate how to set an audience for the publish permissions,
                    // if none are set, this defaults to FRIENDS
                    .setDefaultAudience(SessionDefaultAudience.FRIENDS)
                    .setRequestCode(FrogConstants.REAUTH_ACTIVITY_CODE);
            session.requestNewPublishPermissions(newPermissionsRequest);
        }
    }
	

    private final class CameraButtonOnClickListener implements OnClickListener {
        /*
         * Source Tag: login_tag
         */
        @Override
        public void onClick(View arg0) {
        	{
            	//Intent intent = new Intent(Intent.ACTION_MAIN);
            	//intent.setComponent(new ComponentName("com.facebook.pages.app","com.facebook.pages.app.activity.PagesManagerLoginActivity"));
            	//startActivity(intent);
            	//return;
        		handleCameraButtonClick();
        	}
        }
    }


    private final class AlbumButtonOnClickListener implements OnClickListener {
        /*
         * Source Tag: login_tag
         */
        @Override
        public void onClick(View arg0) {
        	{        
        	
            	//Intent intent = new Intent(Intent.ACTION_MAIN);
            	//intent.setComponent(new ComponentName("com.xchanging.clickshare","com.xchanging.settings.AlbumListPrefActivity"));
            	//startActivity(intent);
            	//return;
        		Session session = Session.getActiveSession();
                if (!utilityHelper.isConnectingToInternet())
                {
                	showAlertDialog(getActivity(), getString(R.string.no_internet_title), getString(R.string.no_internet), false);
                	return;
                }
                else if (session == null || !session.isOpened()) {
                	Log.e(TAG, "Not logged in yet...");
                	//captureBtn.setVisibility(View.INVISIBLE);
                	picUri = null;
                	showAlertDialog(getActivity(), getString(R.string.album_error_title), getString(R.string.no_log_in), false);
                	return; 
                }
                
        		albumNodeList = AlbumArrayList.getAlbumListFromPref(getActivity());
        		if (albumNodeList != null)
        		{
	        		final CharSequence[] items = albumNodeList.toArray();
	        		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        		builder.setTitle("Choose Your Album");
	        		builder.setItems(items, new DialogInterface.OnClickListener() {
	        		    public void onClick(DialogInterface dialog, int which) {

							utilityHelper.setTodayEventAlbumId(albumNodeList.get(which).getAlbumId());
							String temp = getString(R.string.settings_album_selected) + albumNodeList.get(which).getAlbumName();
							albumSelected.setText(temp);
							showAlertDialog(getActivity(), getString(R.string.settings_complete_title), getString(R.string.settings_complete_msg), false);
	        		    }
	        		});
	        		AlertDialog alert = builder.create();
	        		alert.show();
        		}
        	}
        }
    }


    private final class HelpButtonOnClickListener implements OnClickListener {
	        /*
	         * Source Tag: login_tag
	         */
	        @Override
	        public void onClick(View arg0) {
	    	{        
	    		String url = "https://sites.google.com/site/photoboothhelp/home";
	    		Intent i = new Intent(Intent.ACTION_VIEW);
	    		i.setData(Uri.parse(url));
	    		startActivity(i);	
	    	}
        }
    }

    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }
	
	private void displayOnScreen()
	{
		if ((getResources().getConfiguration().screenLayout & 
			    Configuration.SCREENLAYOUT_SIZE_MASK) == 
			        Configuration.SCREENLAYOUT_SIZE_NORMAL
			        
			        || (getResources().getConfiguration().screenLayout & 
						    Configuration.SCREENLAYOUT_SIZE_MASK) == 
					        Configuration.SCREENLAYOUT_SIZE_SMALL) {
			    return;

		}
		if (picUri != null)
		{
    		try {
    			//Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), picUri);
    			//retrieve a reference to the ImageView
    			
				byte[] bitmapdata = Utility.scaleToImageThumbnail(getActivity().getApplicationContext(), picUri);
				Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata , 0, bitmapdata .length);
				ImageView picView = (ImageView)getActivity().findViewById(R.id.picture);
    			//display the returned cropped image
    			picView.setImageBitmap(bitmap);
    			LinearLayout photoLayout =(LinearLayout)getActivity().findViewById(R.id.Photo_layout);
    			photoLayout.setVisibility(View.VISIBLE);
    		} 
    		catch (Exception e) {
    			// TODO: handle exception
    			e.printStackTrace();
			}
    		//TBD
			//operationPendingState = STAGE_RESET;
		}
	}

    @Override
    public void onResume() {
        super.onResume();
        
        // For scenarios where the main activity is launched and user
		// session is not null, the session state change notification
		// may not be triggered. Trigger it if it's open/closed.
		Session session = Session.getActiveSession();
		if (session != null &&
				(session.isOpened() || session.isClosed()) ) {
			onSessionStateChange(session, session.getState(), null);
		}

		if (operationPendingState == FrogConstants.STAGE_DONE)
		{
			displayOnScreen();
		}
		
        uiHelper.onResume();
        
        finishLogin();
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	Log.e(TAG, "ONACTIVITYRESULT requestCode:" + requestCode + "resultCode" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FrogConstants.REAUTH_ACTIVITY_CODE) {

            uiHelper.onActivityResult(requestCode, resultCode, data);
        }
        else if (resultCode == Activity.RESULT_OK) {
    		//user is returning from capturing an image using the camera
    		if(requestCode == FrogConstants.CAMERA_CAPTURE){
    			//get the Uri for the captured image
    			if (data != null)
    			{
    				picUri = data.getData();
    			}
    			else
    			{
    				//show a dialog that issue with camera
    				Log.i(TAG, "PhotoBoothFileName:" + PhotoBoothFileName);
    				if (PhotoBoothFileName == null)
    				{
	        		//display an error message
	        		String errorMessage = "Whoops - your device doesn't support this feature";
	        		Toast.makeText(getActivity()
		                     .getApplicationContext(),
		                     errorMessage,
		                     Toast.LENGTH_SHORT).show();
    				}
    				else
    				{
    					//create picture uri
    					picUri = Uri.parse(PhotoBoothFileName);
    					if(picUri == null)
    					{
    		        		String errorMessage = "Whoops - your device doesn't support this feature";
    		        		Toast.makeText(getActivity()
    			                     .getApplicationContext(),
    			                     errorMessage,
    			                     Toast.LENGTH_SHORT).show();
    					}
    					
    				}
    			}
    			Log.i(TAG, "Pic Uri=" + picUri);
    			//crop operation removed and upload to facebook added
    			//check if facebook upload allowed in settings
    			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                boolean allowed = sharedPref.getBoolean(FrogConstants.KEY_FB_CHECKBOX, true);
                
                if(allowed)
                {
                	new UpLoadDropboxPhotoASYNC().execute(picUri);
                	//uploadToDropBox(picUri);
                	//performUploadToFacebook(picUri);
                }
                else
                {
                	boolean allowedEmail = sharedPref.getBoolean(FrogConstants.KEY_EMAIL_CHECKBOX, true);
                	if(allowedEmail)
                	{

                    	operationPendingState = FrogConstants.STAGE_RESET;
                    	
	                    //send the email also.
	            		String filePathForEmail = getFilePathFromContentUri(picUri,
	            				getActivity().getContentResolver());
	            		Log.i(TAG, "FilePath-----" + filePathForEmail);

	                    chooseEmail("gmail", filePathForEmail);	
                	}
                	else
                	{
                		displayOnScreen();
                	}
                }
    			//operationPendingState = STAGE_DONE;
    			return;
    		}
    		//user is returning from cropping the image
    		/*else if(requestCode == PIC_CROP){
    			//get the returned data
    			Bundle extras = data.getExtras();
    			//get the cropped bitmap
    			Bitmap thePic = extras.getParcelable("data");
    			//retrieve a reference to the ImageView
    			ImageView picView = (ImageView)getActivity().findViewById(R.id.picture);
    			//display the returned cropped image
    			picView.setImageBitmap(thePic);
    			return;
    		}*/
    	}
    }
    
    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }
    
    private class UpLoadDropboxPhotoASYNC extends AsyncTask<Uri, Void, String> {
    	        @Override
    	        protected String doInBackground(Uri... urls) {
    	        	uploadToDropBox(urls[0]);
    	            return null;
    	        }
    	        @Override
    	        protected void onPostExecute(String result) {
    	 
    	        }
    	    }

    
    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(FrogConstants.PENDING_STATE_KEY, operationPendingState);//TODO:
        if (PhotoBoothFileName != null)
        {
        	outState.putString(FrogConstants.MEDIAURL_BEFORE_EMAIL, PhotoBoothFileName);
        }
        if (picUri != null)
        {
        	outState.putString(FrogConstants.MEDIAURL_BEFORE_EMAIL_ICS, picUri.toString());
        }
        dismissProgressDialog();
        uiHelper.onSaveInstanceState(outState);
    }
    
    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
    	
    	if ((session != null && session.isOpened())) {
            
            Log.i(TAG, "Logged in...");
            if (state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
            	Log.e(TAG, "TOKEN UPDATED");
            	if (utilityHelper.getPageTokenProgress() == 1)
        		{
        			getPageAccessToken();
        		}
                return;
            }
    		//check the access token to page access token.
    		if (utilityHelper.getPageTokenProgress() == 0)
    		{
    			getPageAccessToken();
    		}
    		else if (utilityHelper.getAlbumUpdateProgress() == 0)
    		{
    			//showAlertDialog(getActivity(), "Select the album also", "Select the album also", false);
    			if (utilityHelper.getPageTokenProgress() > 2)
    			{
    				getAlbumList();
    			}
    		}
        
        } else if (state.isClosed()) {
        	Log.i(TAG, "Logged out...");
        	//captureBtn.setVisibility(View.INVISIBLE);
        	picUri = null;
        	utilityHelper.setPageTokenPrefFlag(0);
        	requestPublish = false;
        	utilityHelper.setAlbumNodeList("");
        	albumSelected.setText("");
        	if (utilityHelper.getAlbumUpdateProgress() == 2)
        	{
        		showAlertDialog(getActivity(), getString(R.string.logout_title), getString(R.string.complete_logout_msg), false);
        	}
        	utilityHelper.setAlbumUpdateProgress(0);
        	//TODO: clear the album data in preference
        }
    	/*if (!utilityHelper.isConnectingToInternet())
        {
        	showAlertDialog(getActivity(), getString(R.string.no_internet_title), getString(R.string.no_internet), false);
        }*/
    }
    

    private void handleCameraButtonClick() {
    	pendingUpload = false;
		Session session = Session.getActiveSession();
        if (!utilityHelper.isConnectingToInternet())
        {
        	showAlertDialog(getActivity(), getString(R.string.no_internet_title), getString(R.string.no_internet), false);
        	return;
        }
        else if (session == null || !session.isOpened()) {
        	Log.e(TAG, "Not logged in yet...");
        	//captureBtn.setVisibility(View.INVISIBLE);
        	picUri = null;
        	showAlertDialog(getActivity(), getString(R.string.no_login_title), getString(R.string.no_log_in), false);
        	return; 
        }
        
    	try {
    		File tempFile = new File (PHOTOBOOTHPATH + FrogConstants.TEMP_FILE_NAME);
    		//tempFile = File.createTempFile("PhotoBooth", ".jpg");
    		
    		Uri uri = Uri.fromFile(tempFile);
    		PhotoBoothFileName = uri.toString();
        	//use standard intent to capture an image
        	Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        	//we will handle the returned data in onActivityResult
            startActivityForResult(captureIntent, FrogConstants.CAMERA_CAPTURE);
    	}
        catch(ActivityNotFoundException anfe){
    		//display an error message
    		String errorMessage = "Whoops - your device doesn't support capturing images!";
    		Toast.makeText(getActivity()
                     .getApplicationContext(),
                     errorMessage,
                     Toast.LENGTH_SHORT).show();
    	}
    }

    /**
     * Helper method to carry out upload operation to facebook
     */
    private void getAlbumList(){
    	utilityHelper.setAlbumUpdateProgress(1);
    	Log.e(TAG, "get Album List called");
	    Session session = Session.getActiveSession();
	    if (session != null) {
	    	Bundle params = new Bundle();
            
            String pageUrl = "me/Albums";
		    Request.Callback callback= new Request.Callback() {
		    	
		        public void onCompleted(Response response) {
		            FacebookRequestError error = response.getError();
		            if (error != null) {
		                Toast.makeText(getActivity()
		                     .getApplicationContext(),
		                     error.getErrorMessage(),
		                     Toast.LENGTH_SHORT).show();
		                } else {
		                	
	    		            JSONObject graphResponse = response
	    		                                       .getGraphObject()
	    		                                       .getInnerJSONObject();
	    		            Log.i(TAG, "graphResponse:" + graphResponse.toString());
	    		            AlertDialog myDialog;
							try {
								
								JSONArray accountArray = graphResponse.getJSONArray("data");
								albumNodeList = new AlbumArrayList();
								for(int i=0, j= accountArray.length(); i < j; i++)
								{
									JSONObject albumDetails = accountArray.getJSONObject(i);
									if (true == albumDetails.getBoolean("can_upload"))
									{
										//add to list
										//Log.i(TAG, "Album Name" + albumDetails.getString("name").toString());
										
										String albumName = albumDetails.getString("name");
										String albumId = albumDetails.getString("id");
										AlbumNode node = new AlbumNode(albumName, albumId);
										albumNodeList.add(node);
									}
								}
								
								if (albumNodeList.size() > 0)
								{
									CharSequence[] albumList = albumNodeList.toArray();

									AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
									builder.setTitle("Choose your Album to upload all photo for today's event");
									//builder.setSingleChoiceItems(albumList, -1, new DialogInterface.OnClickListener() {
									builder.setItems(albumList, new DialogInterface.OnClickListener() {
	
									@Override
									public void onClick(DialogInterface dialog, int which) {
										dismissProgressDialog();
										utilityHelper.setTodayEventAlbumId(albumNodeList.get(which).getAlbumId());
										utilityHelper.setAlbumUpdateProgress(2);
										utilityHelper.setPageTokenPrefFlag(3);
										
										String temp = getString(R.string.settings_album_selected) + albumNodeList.get(which).getAlbumName();
										albumSelected.setText(temp);
										
										//save the entire album list to preference list.
										albumNodeList.save(getActivity().getApplicationContext());
										showAlertDialog(getActivity(), getString(R.string.settings_complete_title), getString(R.string.settings_complete_msg), false);
									}
									});
									builder.setCancelable(false);
									myDialog = builder.create();
									myDialog.show();
								}
		    		            else
		    		            {
		    		            	showAlertDialog(getActivity(), getString(R.string.settings_complete_title), getString(R.string.settings_noAlbum_msg), false);
		    		            	utilityHelper.setTodayEventAlbumId("me");
		    		            	utilityHelper.setAlbumUpdateProgress(2);
									utilityHelper.setPageTokenPrefFlag(3);
									
		    		            	dismissProgressDialog();
		    		            }
								
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

		            }
		            
		        }
		    };
		    Request request = new Request(session, pageUrl, params, 
		                          HttpMethod.GET, callback);

		    RequestAsyncTask task = new RequestAsyncTask(request);
		    task.execute();
	    }
    }
    
    /**
     * Helper method to carry out upload operation to facebook
     */
    private void getPageAccessToken(){
    	utilityHelper.setPageTokenPrefFlag(1);
	    Session session = Session.getActiveSession();
        List<String> permissions = session.getPermissions();
        if (!permissions.containsAll(FrogConstants.PERMISSIONS)) {
            requestPublishPermissions(session, FrogConstants.PERMISSIONS);
            return;
        }
        utilityHelper.setPageTokenPrefFlag(2);

    	Log.e(TAG, "getPageAccessToken called");
	    dialogProgress = ProgressDialog.show(getActivity(), "",
                getString(R.string.settings_progress), true, false);

	    if (session != null) {
	    	Bundle params = new Bundle();
            String pageUrl = "me/Accounts";
		    Request.Callback callback= new Request.Callback() {
		    	
		        public void onCompleted(Response response) {
		        	Log.e(TAG, "Account request completed");
		            FacebookRequestError error = response.getError();
		            if (error != null) {
		                Toast.makeText(getActivity()
		                     .getApplicationContext(),
		                     error.getErrorMessage(),
		                     Toast.LENGTH_SHORT).show();
		                	utilityHelper.setPageTokenPrefFlag(0);
		                } else {

	    		            JSONObject graphResponse = response
	    		                                       .getGraphObject()
	    		                                       .getInnerJSONObject();
	    		            String newToken = null;
							try {
								JSONArray accountArray = graphResponse.getJSONArray("data");
								ArrayList<String> stringPageList = new ArrayList<String>();
								pageTokenList = new ArrayList<String>();
								
								for(int i=0, j= accountArray.length(); i < j; i++)
								{
									JSONObject temp = accountArray.getJSONObject(i);
									String pageName = temp.getString("name");
									stringPageList.add(pageName);
									String accessToken = temp.getString("access_token");
									pageTokenList.add(accessToken);
									
								}

		    		            if (stringPageList.size() > 0)
		    		            {

									stringPageList.add("Own Profile");
									pageTokenList.add("me");
									
		    		            	CharSequence[] pageList = 
		    		            			stringPageList.toArray(new CharSequence[stringPageList.size()]);

									AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
									builder.setTitle("Choose your Page");
									builder.setItems(pageList, new DialogInterface.OnClickListener() {
	
									@Override
									public void onClick(DialogInterface dialog, int which) {
										
										if (pageTokenList.get(which) != "me")
										{
					    		            Session session = Session.getActiveSession();
					    		            AccessToken at = AccessToken.createFromExistingAccessToken(pageTokenList.get(which), null, null, null, null);
					    		            
					    		            Session.StatusCallback callbackToken = new Session.StatusCallback() {
					    		                @Override
					    		                public void call(final Session session, final SessionState state, final Exception exception) {
					    		                	//Log.e(TAG, "page token set successfully");
	
					    		                	utilityHelper.setPageTokenPrefFlag(3);
					    		                }
					    		            };
					    		            session.openActiveSessionWithAccessToken(getActivity(), at, callbackToken);
					    		            Log.i(TAG, "Updated with new token");
										}
										else
										{
				    		            	utilityHelper.setTodayEventAlbumId("me");
				    		            	getAlbumList();
										}

									}
									});
									builder.setCancelable(false);
									AlertDialog myDialog = builder.create();
									myDialog.show();
		    		            	
		    		            }
		    		            else
		    		            {
		    		            	utilityHelper.setTodayEventAlbumId("me");
		    		            	getAlbumList();
		    		            	/*//alert
		    		            	dismissProgressDialog();
		    		            	showAlertDialog(getActivity(), getString(R.string.settings_error_title), getString(R.string.settings_error_msg), false);*/
		    		            }
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

		            }
		            
		        }
		    };
		    Request request = new Request(session, pageUrl, params, 
		                          HttpMethod.GET, callback);

		    RequestAsyncTask task = new RequestAsyncTask(request);
		    task.execute();
	    }
    }
    
    private void uploadToDropBox(Uri photoPath)
    {
    	if (dropboxClient != null)
    	{
    		dropboxClient.pushTodo(photoPath);
    	}
    }
    
    
	private void finishLogin() {
		dropboxClient = m_app.getRemoteClientManager()
				.getRemoteClient();

		if (dropboxClient != null && dropboxClient.finishLogin() && dropboxClient.isAuthenticated()) {
			Log.i(TAG, "LoginScreen: login complete: about to start the app.");

			/*Intent broadcastLoginIntent = new Intent(
					"com.todotxt.todotxttouch.ACTION_LOGIN");
			sendBroadcast(broadcastLoginIntent);*/
			//change the button text
			dropboxLoginBtn.setText(R.string.logout_screen_button);
		} else {
			dropboxLoginBtn.setText(R.string.login_screen_button);
			Log.i(TAG, "LoginScreen: not logged in. Showing login screen.");
		}
	}
    //////////helper methods below
    /**
     * Helper method to carry out upload operation to facebook
     */
    private void performUploadToFacebook(Uri photoUri){
	    Session session = Session.getActiveSession();
	    if (session != null) {

		    // Check for publish permissions    
		 /*   List<String> permissions = session.getPermissions();
		        if (!isSubsetOf(PERMISSIONS, permissions)) {
		            Session.NewPermissionsRequest newPermissionsRequest = new Session
		                    .NewPermissionsRequest(this, PERMISSIONS);
		            session.requestNewPublishPermissions(newPermissionsRequest);
		            return;
		    }*/
		        

	        if (photoUri != null) {
	    	    if (session != null) {

	    		    // Check for publish permissions    
	    		    /*List<String> permissions = session.getPermissions();
			        if (!isSubsetOf(PERMISSIONS, permissions)) {
			            Session.NewPermissionsRequest newPermissionsRequest = new Session
			                    .NewPermissionsRequest(this, PERMISSIONS);
			            session.requestNewPublishPermissions(newPermissionsRequest);
			            return;
			       }*/

	    		    Bundle params = new Bundle();
	                try {
	                    params.putByteArray("source",
	                            Utility.scaleImage(getActivity().getApplicationContext(), photoUri));
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
	                String captions = sharedPref.getString(FrogConstants.KEY_CAPTIONS, "");
	                //get the caption from preference settings.
	                if (captions.trim() == "")
	                {
	                	captions = getString(R.string.upload_caption);
	                }
	                Log.e(TAG, "Captions:" + captions);
	                params.putString("message", captions);
	                
	                String pageUrl = utilityHelper.getTodayEventAlbumId(); 
	                		//sharedPref.getString(SettingsActivity.KEY_PAGE, "");
	                //get the caption from preference settings.
	                pageUrl = pageUrl + "/photos";
	                Log.e(TAG, "pageUrl:" + pageUrl);
	                //params.putString("caption", captions);
	                params.putBoolean("fileUpload", true);
	                //params.putString("album_id", "450096581739776");
	    		    
	    		    
	    		    Request.Callback callback= new Request.Callback() {
	    		    	
	    		        public void onCompleted(Response response) {
	    		        	Log.i(TAG, "Upload Complete");
	    		        	if (getActivity() == null){
	    		        		// may be screen mode changed, so activity destroyed
	    		        		return;
	    		        	}
	    		        	operationPendingState = FrogConstants.STAGE_RESET;
	    		        	dismissProgressDialog();
	    		        	
	    		            FacebookRequestError error = response.getError();
	    		            if (error != null) {
	    		                Toast.makeText(getActivity()
	    		                     .getApplicationContext(),
	    		                     error.getErrorMessage(),
	    		                     Toast.LENGTH_SHORT).show();
	    		                } else {

	    	    		            JSONObject graphResponse = response
	    	    		                                       .getGraphObject()
	    	    		                                       .getInnerJSONObject();
	    	    		            String postId = null;
	    	    		            try {
	    	    		                postId = graphResponse.getString("id");
	    	    		            } catch (JSONException e) {
	    	    		                Log.i(TAG,
	    	    		                    "JSON error "+ e.getMessage());
	    	    		            }
	    	    		            
	    		                    Toast.makeText(getActivity()
	    			                         .getApplicationContext(), 
	    			                         "UPLOAD TO FACEBOOK SUCCESSFUL WITH POST ID: " + postId,
	    			                         Toast.LENGTH_LONG).show();
	    		                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
	    		                	boolean allowedEmail = sharedPref.getBoolean(FrogConstants.KEY_EMAIL_CHECKBOX, true);
	    		                	if(allowedEmail)
	    		                	{
	    			                    //send the email also.
	    		                		String filePathForEmail = null;
	    		                		try{
	    		                			filePathForEmail = getFilePathFromContentUri(picUri,
	    			            				getActivity().getContentResolver());
	    		                		}
	    		                		catch(Exception e)
	    		                		{
	    		                			filePathForEmail = picUri.toString();
	    		                		}
	    			            		Log.i(TAG, "FilePath-----" + filePathForEmail);

	    			                    chooseEmail("gmail", filePathForEmail);
	    		                	}
	    		                	else
	    		                	{
	    		                		displayOnScreen();
	    		                	}
	    		            }
	    		            
	    		        }
	    		    };
	    		    Log.i(TAG, "Started uploading");
	    		    //never cancel the progress till upload is not complete. set last parameter as false.
	    		    dialogProgress = ProgressDialog.show(getActivity(), "",
	                        getString(R.string.upload_progress), true, false);

	    		    Request request = new Request(session, pageUrl, params, 
	    		                          HttpMethod.POST, callback);

	    		    RequestAsyncTask task = new RequestAsyncTask(request);
	    		    operationPendingState = FrogConstants.STAGE_EMAIL;
	    		    task.execute();
	    		}

	        }
	    }
            
    }


    /**
     * Gets the corresponding path to a file from the given content:// URI
     * @param selectedVideoUri The content:// URI to find the file path from
     * @param contentResolver The content resolver to use to perform the query.
     * @return the file path as a string
     */
    private String getFilePathFromContentUri(Uri selectedVideoUri,
            ContentResolver contentResolver) {
        String filePath = "file://";
        try{
	        String[] filePathColumn = {MediaColumns.DATA};
	
	        Cursor cursor = contentResolver.query(selectedVideoUri, filePathColumn, null, null, null);
	        cursor.moveToFirst();
	
	        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	        filePath += cursor.getString(columnIndex);
	        cursor.close();
        }
        catch(Exception e)
        {
        	filePath = selectedVideoUri.toString();
        }
        return filePath;
    }
    
    /**
     * Helper method to carry out crop operation
     */
    private void performCrop(){
    	//take care of exceptions
    	try {
    		//call the standard crop action intent (the user device may not support it)
	    	Intent cropIntent = new Intent("com.android.camera.action.CROP"); 
	    	//indicate image type and Uri
	    	cropIntent.setDataAndType(picUri, "image/*");
	    	//set crop properties
	    	cropIntent.putExtra("crop", "true");
	    	//indicate aspect of desired crop
	    	cropIntent.putExtra("aspectX", 1);
	    	cropIntent.putExtra("aspectY", 1);
	    	//indicate output X and Y
	    	cropIntent.putExtra("outputX", 256);
	    	cropIntent.putExtra("outputY", 256);
	    	//retrieve data on return
	    	cropIntent.putExtra("return-data", true);
	    	//start the activity - we handle returning in onActivityResult
	        startActivityForResult(cropIntent, FrogConstants.PIC_CROP);  
    	}
    	//respond to users whose devices do not support the crop action
    	catch(ActivityNotFoundException anfe){
    		//display an error message
    		String errorMessage = "Whoops - your device doesn't support the crop action!";
    		Toast toast = Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT);
    		toast.show();
    	}
    }


	private void publishStory() {
	    Session session = Session.getActiveSession();
	    if (session != null) {

		    // Check for publish permissions    
		    List<String> permissions = session.getPermissions();
		        if (!isSubsetOf(FrogConstants.PERMISSIONS, permissions)) {
		            Session.NewPermissionsRequest newPermissionsRequest = new Session
		                    .NewPermissionsRequest(this, FrogConstants.PERMISSIONS);
		            session.requestNewPublishPermissions(newPermissionsRequest);
		            return;
		       }

		    Bundle postParams = new Bundle();
		    postParams.putString("name", "Facebook SDK for Android");
		    postParams.putString("caption", "Build great social apps and get more installs.");
		    postParams.putString("description", "The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
		    postParams.putString("link", "https://developers.facebook.com/android");
		    postParams.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");

		    Request.Callback callback= new Request.Callback() {
		        public void onCompleted(Response response) {
		            JSONObject graphResponse = response
		                                       .getGraphObject()
		                                       .getInnerJSONObject();
		            String postId = null;
		            try {
		                postId = graphResponse.getString("id");
		            } catch (JSONException e) {
		                Log.i(TAG,
		                    "JSON error "+ e.getMessage());
		            }
		            FacebookRequestError error = response.getError();
		            if (error != null) {
		                Toast.makeText(getActivity()
		                     .getApplicationContext(),
		                     error.getErrorMessage(),
		                     Toast.LENGTH_SHORT).show();
		                } else {
		                    Toast.makeText(getActivity()
		                         .getApplicationContext(), 
		                         postId,
		                         Toast.LENGTH_LONG).show();
		            }
		        }
		    };

		    Request request = new Request(session, "me/feed", postParams, 
		                          HttpMethod.POST, callback);

		    RequestAsyncTask task = new RequestAsyncTask(request);
		    task.execute();
		}
	}
	
	private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
	    for (String string : subset) {
	        if (!superset.contains(string)) {
	            return false;
	        }
	    }
	    return true;
	}


    private void chooseEmail(String type, String filePath)
    {
    	//finish current activity and launch the email app
    	//
    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String to = sharedPref.getString(FrogConstants.KEY_DEFAULT_EMAIL, "");
        //get the caption from preference settings.
        
    	//String to = "snstest481@gmail.com";
		String subject = "Photobooth Upload";
		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		String message = getString(R.string.email_body) + mydate;
		  
		Intent email = new Intent(Intent.ACTION_SEND);
		email.putExtra(Intent.EXTRA_EMAIL, new String[]{ to});
		//email.putExtra(Intent.EXTRA_CC, new String[]{ to});
		//email.putExtra(Intent.EXTRA_BCC, new String[]{to});
		email.putExtra(Intent.EXTRA_SUBJECT, subject);
		email.putExtra(Intent.EXTRA_TEXT, message);
		//we get "/storage/sdcard0/DCIM/Camera/20130422_161512.jpg"
		email .putExtra(Intent.EXTRA_STREAM, Uri.parse(filePath));

		//need this to prompts email client only
		email.setType("message/rfc822");

    	List<ResolveInfo> resInfo = getActivity().getPackageManager().queryIntentActivities(email, 0);

    	if (!resInfo.isEmpty())
    	{
    	    for (ResolveInfo info : resInfo) 
    	    {
	    	    if (info.activityInfo.packageName.toLowerCase().contains(type) || info.activityInfo.name.toLowerCase().contains(type)) 
	    	    {
	    	    	email.setPackage(info.activityInfo.packageName);
	    	    	operationPendingState = FrogConstants.STAGE_DONE;
	    	    	getActivity().startActivity(Intent.createChooser(email, "Choose an Email client :"));
	    	    	//getActivity().startActivityForResult(Intent.createChooser(email, "Choose an Email client :"), EMAIL_SEND);
	    	    	
    	        }
    	    }
    	}
    }
    
    /**
     * Function to display simple Alert Dialog
     * @param context - application context
     * @param title - alert dialog title
     * @param message - alert message
     * @param status - success/failure (used to set icon)
     * */
    @SuppressWarnings("deprecation")
	public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
 
        // Setting Dialog Title
        alertDialog.setTitle(title);
 
        // Setting Dialog Message
        alertDialog.setMessage(message);
         
        // Setting alert dialog icon
        //alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);
 
        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
 
        // Showing Alert Message
        alertDialog.show();
    }
    
    private void dismissProgressDialog()
    {
    	
    	if (dialogProgress != null && dialogProgress.isShowing())
    	{
    		dialogProgress.dismiss();
    	}
    }
}

    