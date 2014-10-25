package com.xchanging.streams;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.xchanging.streams.PBProviderMetadata.AccessRightsTableMetaData;

public class PBProvider extends ContentProvider {

	//Logging helper tag. No significance to providers.
	private static final String TAG = "PBProvider";
	//Setup projection Map
	//Projection maps are similar to "as" (column alias) construct
	//in an sql statement where by you can rename the
	//columns.

	private static HashMap<String, String> sAccessRightsProjectionMap;
	
	static
	{
		sAccessRightsProjectionMap = new HashMap<String, String>();
		
		//map for AccessRightsTable
		sAccessRightsProjectionMap.put(AccessRightsTableMetaData._ID, AccessRightsTableMetaData._ID);
		sAccessRightsProjectionMap.put(AccessRightsTableMetaData.BODY_TEXT, AccessRightsTableMetaData.BODY_TEXT);
		//sAccessRightsProjectionMap.put(AccessRightsTableMetaData.FEATURE_NAME, AccessRightsTableMetaData.FEATURE_NAME);
		//sAccessRightsProjectionMap.put(AccessRightsTableMetaData.CODE, AccessRightsTableMetaData.CODE);
	}
	
	
	//Setup URIs
	//Provide a mechanism to identify
	//all the incoming uri patterns.
	private static final UriMatcher sUriMatcher;
	private static final int ACCESSRIGHTS_URI_INDICATOR = 1;
	private static final int BANK_URI_INDICATOR = 2; //..
	private static final int ACCESSRIGHTS_STREAMS = 3;
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(PBProviderMetadata.AUTHORITY, "streams", ACCESSRIGHTS_STREAMS);
	}
	
	/**
	* Setup/Create Database
	* This class helps open, create, and upgrade the database file.
	*/
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, PBProviderMetadata.DATABASE_NAME, null, PBProviderMetadata.DATABASE_VERSION);
		}
		@Override
		public void onCreate(SQLiteDatabase db)
		{
			Log.d(TAG,"inner oncreate called");
			db.execSQL("CREATE TABLE " + AccessRightsTableMetaData.TABLE_NAME + " ("
				+ AccessRightsTableMetaData._ID + " INTEGER PRIMARY KEY,"
				+ AccessRightsTableMetaData.EVENT_ID + " TEXT UNIQUE NOT NULL,"
				+ AccessRightsTableMetaData.BODY_PICTURE + " TEXT,"
				+ AccessRightsTableMetaData.BODY_TEXT + " TEXT,"
				+ AccessRightsTableMetaData.EVENT_USER_NAME + " TEXT,"
				+ AccessRightsTableMetaData.EVENT_USER_PROFILE_PIC + " TEXT"
				+ ");");
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			Log.d(TAG,"inner onupgrade called");
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
			    + newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + AccessRightsTableMetaData.TABLE_NAME);
			onCreate(db);
		}
	}
	
	private DatabaseHelper mOpenHelper;
	
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
			case ACCESSRIGHTS_URI_INDICATOR:
				count = db.delete(AccessRightsTableMetaData.TABLE_NAME,	where, whereArgs);
			break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
			case ACCESSRIGHTS_URI_INDICATOR:
				return AccessRightsTableMetaData.CONTENT_TYPE;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		// Validate the requested uri
		if (sUriMatcher.match(uri) == ACCESSRIGHTS_URI_INDICATOR)
		{
			
		}
		else if (sUriMatcher.match(uri) == BANK_URI_INDICATOR)
		{
			
		}
		else if (sUriMatcher.match(uri) == ACCESSRIGHTS_STREAMS)
		{
			
		}
		else
		{
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		ContentValues values;
		if (initialValues != null) 
		{
			values = new ContentValues(initialValues);
		}
		else
		{
			values = new ContentValues();
		}

		/*if (values.containsKey(AccessRightsTableMetaData.BODY_PICTURE) == false)
		{
			throw new SQLException(
			"Failed to insert row because feature id needed " + uri);
		}*/
		if (values.containsKey(AccessRightsTableMetaData.EVENT_ID) == false)
		{
			throw new SQLException(
					"Failed to insert row because feature name needed " + uri);
		}
		if (values.containsKey(AccessRightsTableMetaData.EVENT_USER_NAME) == false) {
			values.put(AccessRightsTableMetaData.EVENT_USER_PROFILE_PIC, "Unknown");
		}
		
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		long rowId = db.insertWithOnConflict(AccessRightsTableMetaData.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
		//long rowId = db.insert(AccessRightsTableMetaData.TABLE_NAME, null, values);
		if (rowId > 0) {
			Uri insertedBookUri = ContentUris.withAppendedId(AccessRightsTableMetaData.CONTENT_URI, rowId);
			getContext().getContentResolver().notifyChange(insertedBookUri, null);
			return insertedBookUri;
		}
		{
			return null;
		}
	}

	@Override
	public boolean onCreate() {
		Log.d(TAG,"main onCreate called");
		mOpenHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		
		switch (sUriMatcher.match(uri)) {
			case ACCESSRIGHTS_URI_INDICATOR:
				qb.setTables(AccessRightsTableMetaData.TABLE_NAME);
				qb.setProjectionMap(sAccessRightsProjectionMap);
			break;
			case ACCESSRIGHTS_STREAMS:
				qb.setTables(AccessRightsTableMetaData.TABLE_NAME);
				qb.setProjectionMap(sAccessRightsProjectionMap);
				
				break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		// If no sort order is specified use the default
		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = AccessRightsTableMetaData.DEFAULT_SORT_ORDER;
		} else {
			orderBy = sortOrder;
		}
		
		// Get the database and run the query
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
		
		// Tell the cursor what uri to watch,
		// so it knows when its source data changes
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
			case ACCESSRIGHTS_URI_INDICATOR:
				count = db.update(AccessRightsTableMetaData.TABLE_NAME,	values, where, whereArgs);
			break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

}
