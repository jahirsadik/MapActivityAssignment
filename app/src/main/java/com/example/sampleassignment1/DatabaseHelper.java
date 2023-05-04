package com.example.sampleassignment1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.TreeSet;


public class DatabaseHelper extends SQLiteOpenHelper {
    final static String DATABASE_URL = "https://assignment2-9cf0e-default-rtdb.asia-southeast1.firebasedatabase.app/";
    public final static DatabaseReference fDatabase = FirebaseDatabase.getInstance(DATABASE_URL).getReference();
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

    public static void updateLocations (TreeSet<UserLocEntry> locations, DataSnapshot snapshot) {
        UserLocEntry temp = new UserLocEntry(
                Objects.requireNonNull(snapshot.child("address").getValue()).toString(),
                Instant.ofEpochSecond(Long.parseLong(Objects.requireNonNull(snapshot.child("epoch").getValue()).toString())).atOffset(ZoneOffset.UTC).toLocalDateTime(),
                Double.parseDouble(Objects.requireNonNull(snapshot.child("latitude").getValue()).toString()),
                Double.parseDouble(Objects.requireNonNull(snapshot.child("longitude").getValue()).toString())
        );
        locations.add(temp);
    }
}
