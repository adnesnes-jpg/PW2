package com.example.pw2;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class CampusContentProvider extends ContentProvider {

    public static final String AUTHORITY = "com.example.pw2.provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/registrations");

    private static final int REGISTRATIONS = 1;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, "registrations", REGISTRATIONS);
    }

    private DatabaseHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;
        if (uriMatcher.match(uri) == REGISTRATIONS) {
            cursor = db.query("registrations", projection, selection, selectionArgs, null, null, sortOrder);
        } else {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        if (getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = db.insert("registrations", null, values);
        if (id > 0) {
            if (getContext() != null) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsUpdated;
        if (uriMatcher.match(uri) == REGISTRATIONS) {
            rowsUpdated = db.update("registrations", values, selection, selectionArgs);
        } else {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        if (rowsUpdated > 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted;
        if (uriMatcher.match(uri) == REGISTRATIONS) {
            rowsDeleted = db.delete("registrations", selection, selectionArgs);
        } else {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        if (rowsDeleted > 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        return "vnd.android.cursor.dir/vnd.com.example.pw2.registrations";
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "CampusEvent.db";
        private static final int DATABASE_VERSION = 1;

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE registrations (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT, reg_no TEXT, email TEXT, phone TEXT, programme TEXT)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS registrations");
            onCreate(db);
        }
    }
}