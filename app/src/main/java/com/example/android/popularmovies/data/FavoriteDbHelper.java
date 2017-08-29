package com.example.android.popularmovies.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmovies.data.FavoriteContract.FavoriteEntry;
public class FavoriteDbHelper extends SQLiteOpenHelper {

    private static final int DATA_VERSION = 1;
    public static final String DATABASE_NAME = "movies.db";

    public FavoriteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // CREATE TABLE favorites (_id INTEGER PRIMARY KEY AUTOINCREMENT, movie_id INTEGER NOT NULL,
        // poster_path TEXT NOT NULL, original_title TEXT NOT NULL);
        String SQL_CREATE_FAVORITES_TABLE =
                "CREATE TABLE " + FavoriteEntry.TABLE_NAME + "(" +
                        FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        FavoriteEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                        FavoriteEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL, " +
                        FavoriteEntry.COLUMN_MOVIE_ORIGINAL_TITLE + " TEXT NOT NULL);";
        db.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
