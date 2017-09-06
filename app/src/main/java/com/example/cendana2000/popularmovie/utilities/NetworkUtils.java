package com.example.cendana2000.popularmovie.utilities;

import android.net.Uri;

/**
 * Created by Cendana2000 on 03-Jul-17.
 */

public class NetworkUtils {
    private static final String Movie_DB_BASE_URL =
            "https://api.themoviedb.org/3/";
    private static final String MOVIE_POSTER_URL =
            "http://image.tmdb.org/t/p";
    private static final String API_KEY =
            "Insert Your API KEY";
    private static final String PAGE_QUERY =
            "&page=";
    private static final String YOUTUBE_BASE_URL =
            "https://www.youtube.com/watch";
    private static final String YOUTUBE_PACKAGE =
            "com.google.android.youtube";

    private static final String size = "/w342/";
    public static String getMoviePosterUrl(String path) {
        return MOVIE_POSTER_URL + size + path;
    }
    public static String getMovieDBBaseUrl() {
        return Movie_DB_BASE_URL;
    }
    public static String getAPIKey() {
        return API_KEY;
    }
    public static Uri getYoutubeUrl(String key) {
        Uri uri = Uri.parse(YOUTUBE_BASE_URL);
        uri = uri.buildUpon().appendQueryParameter("v", key).build();
        return uri;
    }
    public static String getYoutubePackage() {
        return YOUTUBE_PACKAGE;
    }
}
