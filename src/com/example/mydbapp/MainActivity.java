package com.example.mydbapp;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// DB設定
		/*
		Database
		- myapp.db
		
		Table
		- users
			- _id
			- name
			- score
		
		ヘルパ: MyDbHelper.java
		定数: MyAppContract.java
		 */
		// DBのOpen
		MyDbHelper helper = new MyDbHelper(this);
		SQLiteDatabase db = helper.getWritableDatabase();
		
		// DBのClose：気にしない
		
		// 各種処理
		// insert
		if(false){
			ContentValues values = new ContentValues();
			values.put(MyAppContract.Users.COLUMN_NAME, "tanaka");
			values.put(MyAppContract.Users.COLUMN_SCORE, 74);
			long newId = db.insert(MyAppContract.Users.TABLE_NAME, null, values);
			Log.v("DB_TEST", "Inserted ID: " + newId);
		}
		
		// select
		Cursor cursor = null;
		try{
			cursor = db.query(
					MyAppContract.Users.TABLE_NAME, // table
					new String[]{"_id", "name", "score"}, // fields
					"score > ?", // where
					new String[]{"60"}, // where args
					null, // group by
					null, // having
					null // order by
			);
			Log.v("DB_TEST", "Count: " + cursor.getCount());
			while(cursor.moveToNext()){
				String name = cursor.getString(cursor.getColumnIndex(MyAppContract.Users.COLUMN_NAME));
				int score = cursor.getInt(cursor.getColumnIndex(MyAppContract.Users.COLUMN_SCORE));
				Log.v("DB_TEST", "name: " + name + " score:" + score);
			}
		}
		finally{
			if(cursor != null){
				cursor.close();
			}
		}
	}

}
