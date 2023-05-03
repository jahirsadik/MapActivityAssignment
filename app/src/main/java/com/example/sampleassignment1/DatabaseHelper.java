package com.example.sampleassignment1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "usernames.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "usernames_table";
    public static final String COLUMN_ID = "id";

    private static final String SQL_CREATE_USERNAMES_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY,"
            + "username1 TEXT,"
            + "username2 TEXT,"
            + "username3 TEXT)";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    public static final String[] projection = {
            "username1",
            "username2",
            "username3"
    };

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_USERNAMES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    void dropTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    public void rebuild(SQLiteDatabase db) {
        dropTable(db);
        onCreate(db);
    }

    public void saveUsernames(String[] usernames) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        dropTable(db);
        values.put("username1", usernames[0]);
        values.put("username2", usernames[1]);
        values.put("username3", usernames[2]);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public String[] getUsernames() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] usernames = new String[3];
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        if ((cursor != null) && (cursor.getCount() > 0)) {
            cursor.moveToNext();
            usernames[0] = cursor.getString(cursor.getColumnIndexOrThrow("username1"));
            usernames[1] = cursor.getString(cursor.getColumnIndexOrThrow("username2"));
            usernames[2] = cursor.getString(cursor.getColumnIndexOrThrow("username3"));
            cursor.close();
        }
        db.close();
        return usernames;
    }
}
