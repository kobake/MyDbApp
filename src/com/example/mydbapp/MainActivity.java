package com.example.mydbapp;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainActivity extends Activity implements LoaderCallbacks<Cursor>{

	private SimpleCursorAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// ListView <> CursorLoader <> ContentsProvider <> DB
		
		
		// ListView
		// adapter設定
		String[] from = {
				MyAppContract.Users.COLUMN_NAME,
				MyAppContract.Users.COLUMN_SCORE
		};
		int[] to = {
				android.R.id.text1,
				android.R.id.text2
		};
		adapter = new SimpleCursorAdapter(
				this,
				android.R.layout.simple_list_item_2,
				null, // cursor
				from,
				to,
				0 // flag
		);
		
		// ListView取得
		ListView list = (ListView)findViewById(R.id.listView1);
		
		// ListViewにadapter設置
		list.setAdapter(adapter);
		
		// click
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if(false){
					ContentValues values = new ContentValues();
					values.put(MyAppContract.Users.COLUMN_NAME, "tanaka");
					values.put(MyAppContract.Users.COLUMN_SCORE, 50);
					getContentResolver().insert(MyContentProvider.CONTENT_URI, values);
				}
				if(true){
					Uri uri = ContentUris.withAppendedId(MyContentProvider.CONTENT_URI, id);
					String selection = MyAppContract.Users.COLUMN_ID + " = ?";
					String[] selectionArgs = new String[]{ Long.toString(id) };
					getContentResolver().delete(uri, selection, selectionArgs);

				}
				
			}
		});
		
		// (1)Loader初期化
		getLoaderManager().initLoader(0, null, this);
	}
	
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// (2) 非同期処理を投げる
		String[] projection = {
				MyAppContract.Users.COLUMN_ID,	
				MyAppContract.Users.COLUMN_NAME,	
				MyAppContract.Users.COLUMN_SCORE	
		};
		return new CursorLoader(
				this,
				MyContentProvider.CONTENT_URI,
				projection,
				null,
				null,
				null
		);
	}
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// (3) 結果をadapterに反映
		adapter.swapCursor(data);
		
	}
	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);		
	}

	void old(){
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
			// ※もっと複雑なクエリを組みたい場合は SQLiteQueryBuilder が良いらしい。
			cursor = db.query(
					MyAppContract.Users.TABLE_NAME, // table
					new String[]{"_id", "name", "score"}, // fields
					null, //"name like ?", //"score > ?", // where
					null, // new String[]{"%t%"}, // new String[]{"60"}, // where args
					null, // group by
					null, // having
					null //"score desc", // order by
					// "2" // limit
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
		
		// update
		if(false){
			ContentValues values = new ContentValues();
			values.put(MyAppContract.Users.COLUMN_SCORE, 100);
			int updatedCount = db.update(
					MyAppContract.Users.TABLE_NAME,
					values,
					"score > ?",
					new String[]{"80"}
			);
			Log.v("DB_TEST", "Updated: " + updatedCount);
		}
		
		// delete
		if(false){
			int deletedCount = db.delete(
					MyAppContract.Users.TABLE_NAME,
					"score < ?",
					new String[]{"50"}
			);
			Log.v("DB_TEST", "Deleted: " + deletedCount);
		}
		
		// transaction
		if(false){
			try{
				db.beginTransaction();
				
				db.execSQL("update users set score = score - 10 where _id = 1");
				db.execSQL("update users set score = score + 10 where _id = 2");
				
				db.setTransactionSuccessful();
			}
			finally{
				db.endTransaction();
			}
		}
	}
	
}
