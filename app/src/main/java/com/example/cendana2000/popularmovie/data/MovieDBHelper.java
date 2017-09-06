package com.example.cendana2000.popularmovie.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.cendana2000.popularmovie.data.MovieContract.MovieEntry;

/**
 * Created by Cendana2000 on 07-Aug-17.
 */

public class MovieDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "movie.db";
    public static final int DATABASE_VERSION = 12;

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String query =
                "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieEntry.COLUMN_ID         + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_TITLE      + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE    + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RATING     + " FLOAT NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW   + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POSTER     + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_SORT_PREF  + " TEXT NOT NULL " +
                ");";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        final String query = "DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME;
        db.execSQL(query);
        onCreate(db);
    }
}
