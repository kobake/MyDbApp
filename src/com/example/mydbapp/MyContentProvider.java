package com.example.mydbapp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MyContentProvider extends ContentProvider{
	
	private static final String AUTHORITY =
			"com.example.mydbapp.mycontentprovider";
	public static final Uri CONTENT_URI =
			Uri.parse("content://" + AUTHORITY + "/" + MyAppContract.Users.TABLE_NAME);
	private MyDbHelper mHelper;
	
	// UriMatcher
	/*
	処理 -> URI
	select -> table USERS
	insert -> table
	update -> row USER_ITEM
	delete -> row
	*/

	private static final int USERS = 1;
	private static final int USER_ITEM = 2;
	
	private static final UriMatcher uriMatcher;
	static{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, MyAppContract.Users.TABLE_NAME, USERS);
		uriMatcher.addURI(AUTHORITY, MyAppContract.Users.TABLE_NAME + "/#", USER_ITEM);
	}
	
	@Override
	public boolean onCreate() {
		mHelper = new MyDbHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		if(uriMatcher.match(uri) != USERS){
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		SQLiteDatabase db = mHelper.getReadableDatabase();
		Cursor cursor = db.query(
				MyAppContract.Users.TABLE_NAME,
				projection,
				selection,
				selectionArgs,
				null,
				null,
				sortOrder
		);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		if(uriMatcher.match(uri) != USERS){
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		SQLiteDatabase db = mHelper.getWritableDatabase();
		long newId = db.insert(MyAppContract.Users.TABLE_NAME, null, values);
		Uri newUri = ContentUris.withAppendedId(MyContentProvider.CONTENT_URI, newId);
		getContext().getContentResolver().notifyChange(uri, null);
		return newUri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		if(uriMatcher.match(uri) != USER_ITEM){
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		SQLiteDatabase db = mHelper.getWritableDatabase();
		int count = db.delete(MyAppContract.Users.TABLE_NAME, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		if(uriMatcher.match(uri) != USER_ITEM){
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		SQLiteDatabase db = mHelper.getWritableDatabase();
		int count = db.update(MyAppContract.Users.TABLE_NAME, values, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

}
