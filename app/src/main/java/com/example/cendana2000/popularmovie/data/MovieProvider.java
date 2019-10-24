package com.example.cendana2000.popularmovie.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cendana2000.popularmovie.data.MovieContract.MovieEntry;

/**
 * Created by Cendana2000 on 07-Aug-17.
 */

public class MovieProvider extends ContentProvider {
    public static final int CODE_MOVIE = 100;
    public static final int CODE_MOVIE_WITH_ID = 101;

    private MovieDBHelper movieDBHelper;
    private UriMatcher uriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;
        final String path = MovieContract.PATH_MOVIE;

        matcher.addURI(authority, path, CODE_MOVIE);
        matcher.addURI(authority, path + "/#", CODE_MOVIE_WITH_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        movieDBHelper = new MovieDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = movieDBHelper.getWritableDatabase();
        Cursor cursor;

        switch (uriMatcher.match(uri)) {
            case CODE_MOVIE:
                cursor = db.query(MovieEntry.TABLE_NAME,
                        null, selection, selectionArgs, null, null, null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = movieDBHelper.getWritableDatabase();
        Uri returnUri;

        switch (uriMatcher.match(uri)) {
            case CODE_MOVIE:
                long id = db.insert(MovieEntry.TABLE_NAME, null, values);
                returnUri = ContentUris.withAppendedId(uri, id);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return returnUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        SQLiteDatabase db = movieDBHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case CODE_MOVIE:
                int rowInserted = 0;
                db.beginTransaction();

                try{
                    for(ContentValues value: values) {
                        long id = db.insert(MovieEntry.TABLE_NAME, null, value);
                        if(id != -1) rowInserted++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if(rowInserted > 0)
                    getContext().getContentResolver().notifyChange(uri, null);

                return rowInserted;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = movieDBHelper.getWritableDatabase();
        int totalDeleted = 0;

        switch (uriMatcher.match(uri)) {
            case CODE_MOVIE_WITH_ID:
                String id = uri.getLastPathSegment();
                totalDeleted = db.delete(MovieEntry.TABLE_NAME, MovieEntry.COLUMN_ID + "=? and " + MovieEntry.COLUMN_SORT_PREF + "=?", new String[]{id, "favorite"});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(totalDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return totalDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
