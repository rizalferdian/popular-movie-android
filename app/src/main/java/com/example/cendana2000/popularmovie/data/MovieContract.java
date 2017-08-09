package com.example.cendana2000.popularmovie.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Cendana2000 on 07-Aug-17.
 */

public class MovieContract {
    public static final String CONTENT_AUTHORITY = "com.example.cendana2000.popularmovie";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIE = "movie";

    public static class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();
        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RELEASE = "release_date";
        public static final String COLUMN_RATING = "vote_average";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_POSTER = "poster";
    }
}
