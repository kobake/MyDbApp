package com.example.mydbapp;

import android.R.integer;
import android.R.string;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDbHelper extends SQLiteOpenHelper{

	public static final String DB_NAME = "myapp.db";
	public static final int DB_VERSION = 2; // version for migration
	
	public MyDbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// create table
		db.execSQL(MyAppContract.Users.CREATE_TABLE);
		// init table		
		db.execSQL(MyAppContract.Users.INIT_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// delete old table
		db.execSQL(MyAppContract.Users.DROP_TABLE);
		// onCreate		
		onCreate(db);
	}
	

}
