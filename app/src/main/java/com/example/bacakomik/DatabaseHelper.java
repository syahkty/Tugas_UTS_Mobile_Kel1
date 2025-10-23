package com.example.bacakomik;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "register.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "registeruser";
    public static final String COL_ID = "ID";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";
    public static final String COL_EMAIL = "email";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_USERNAME + " TEXT, " +
                        COL_EMAIL + " TEXT NOT NULL UNIQUE, " +
                        COL_PASSWORD + " TEXT NOT NULL" +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public boolean addUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_USERNAME, username);
        cv.put(COL_EMAIL, email);
        cv.put(COL_PASSWORD, password);

        long res = db.insert(TABLE_NAME, null, cv);
        db.close();
        return res != -1;
    }

    public boolean checkUserByEmail(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] cols = { COL_ID };
        String sel = COL_EMAIL + " = ? AND " + COL_PASSWORD + " = ?";
        String[] args = { email, password };

        Cursor c = db.query(TABLE_NAME, cols, sel, args, null, null, null, "1");
        boolean ok = (c != null && c.getCount() > 0);
        if (c != null) c.close();
        db.close();
        return ok;
    }

    public boolean isEmailRegistered(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] cols = {COL_ID};
        Cursor c = db.query(TABLE_NAME, cols, COL_EMAIL + " = ?", new String[]{email}, null, null, null, "1");
        boolean exists = (c != null && c.getCount() > 0);
        if (c != null) c.close();
        db.close();
        return exists;
    }

    @Nullable
    public String getUsernameByEmail(String email) {
        if (email == null) return null;

        SQLiteDatabase db = this.getReadableDatabase();
        String username = null;

        Cursor c = db.query(
                TABLE_NAME,
                new String[]{ COL_USERNAME },
                COL_EMAIL + " = ? COLLATE NOCASE",
                new String[]{ email.trim() },
                null, null, null,
                "1"
        );

        if (c != null) {
            if (c.moveToFirst()) {
                username = c.getString(c.getColumnIndexOrThrow(COL_USERNAME));
            }
            c.close();
        }

        return username;
    }

}
