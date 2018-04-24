package com.example.desve.attendancegps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Samuel McGhee on 4/18/2018.
 */

public class DatabaseManager {

    private DBOpenHelper dbOpenHelper;
    private SQLiteDatabase sqLiteDatabase;

    public DatabaseManager(Context context) {
        dbOpenHelper = new DBOpenHelper(context);
    }

    public void open() {
        sqLiteDatabase = dbOpenHelper.getWritableDatabase();
    }

    public void close() {
        sqLiteDatabase.close();
    }

    public void insertUserInfo(String idnum, String username, String password, String name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBOpenHelper.COLUMN_USERID, idnum);
        contentValues.put(DBOpenHelper.COLUMN_USERNAME, username);
        contentValues.put(DBOpenHelper.COLUMN_PASSWORD, password);
        contentValues.put(DBOpenHelper.COLUMN_NAME, name);
        sqLiteDatabase.insert(DBOpenHelper.TABLE_NAME, null, contentValues);
    }

    public UserInfo getUserFromDB() {
        Cursor cursor = sqLiteDatabase.query(DBOpenHelper.TABLE_NAME,
                new String[]{DBOpenHelper.COLUMN_ID, DBOpenHelper.COLUMN_USERID,
                        DBOpenHelper.COLUMN_USERNAME, DBOpenHelper.COLUMN_PASSWORD, DBOpenHelper.COLUMN_NAME},
                null, null, null, null, null);
        cursor.moveToFirst();
        try {
            UserInfo userInfo = new UserInfo(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
            return userInfo;
        } catch (RuntimeException e) {
            return new UserInfo("", "", "", "");
        }
    }

    public void deleteAll() {
        if (sqLiteDatabase.isOpen()) {
            sqLiteDatabase.execSQL("DELETE FROM " + DBOpenHelper.TABLE_NAME);
        }
    }

}
