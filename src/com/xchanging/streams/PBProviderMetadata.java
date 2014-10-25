package com.xchanging.streams;

import android.net.Uri;
import android.provider.BaseColumns;

public class PBProviderMetadata {
	public static final String AUTHORITY = "com.xchanging.PBProvider";
	public static final String DATABASE_NAME = "photobooth.db";
	public static final int DATABASE_VERSION = 1;
	private PBProviderMetadata() {}
	
	//inner class describing AccessRights Table
	public static final class AccessRightsTableMetaData implements BaseColumns
	{
		private AccessRightsTableMetaData() {}
		
		public static final String TABLE_NAME = "streams";
		//uri and MIME type definitions
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/streams");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.photobooth.streams";
		public static final String DEFAULT_SORT_ORDER = "";
		
		//Additional Columns start here.
		public static final String EVENT_ID = "eventid";
		public static final String EVENT_USER_NAME = "username";
		public static final String EVENT_USER_PROFILE_PIC = "profilepic";
		public static final String BODY_PICTURE = "bodypic";
		public static final String BODY_TEXT = "bodytext";
	}
	
}
