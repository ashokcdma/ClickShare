package com.debasmita.clickshare;

import java.util.Arrays;
import java.util.List;

public class FrogConstants {
	
	static final String PENDING_STATE_KEY = "operationPendingState";
	
	static final String MEDIAURL_BEFORE_EMAIL_ICS = "lastMediaUrl";
	static final String MEDIAURL_BEFORE_EMAIL = "PhotoBoothFileName";
	static final String TEMP_FILE_NAME = "/temp.jpg";
	

	static final int STAGE_RESET = 0;
	static final int STAGE_EMAIL = 1;
	static final int STAGE_DONE = 2;
	
	//keep track of camera capture intent
	static final int CAMERA_CAPTURE = 10;
	//keep track of cropping intent
	static final int PIC_CROP = 20;
	
	static final int EMAIL_SEND = 30;

    public static final int REAUTH_ACTIVITY_CODE = 100;
	
	public static final List<String> PERMISSIONS = Arrays.asList("publish_stream", "manage_pages");
	public static final List<String> PERMISSIONS_READ = Arrays.asList("basic_info", "user_photos");
	
	
	public static final String KEY_FB_CHECKBOX = "fb_checkbox_preference";
	public static final String KEY_CAPTIONS = "pref_caption";
	public static final String KEY_EMAIL_CHECKBOX = "checkbox_preference";
	public static final String KEY_DEFAULT_EMAIL = "pref_email_id";
	
	static final int NFC_NOT_SUPPORTED = 0;
	static final int NFC_SUPPORTED = 1;
	static final int NFC_NOT_ENABLED = 2;

	static final int MENU_SETTINGS = 0;
	static final int MENU_PROMOTE_VIA_NFC = 1;
	static final int MENU_STREAMS = 2;
}
