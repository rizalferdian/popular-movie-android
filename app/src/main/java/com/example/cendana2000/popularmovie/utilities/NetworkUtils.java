package com.example.cendana2000.popularmovie.utilities;

/**
 * Created by Cendana2000 on 03-Jul-17.
 */

public class NetworkUtils {
    private static final String MOVIE_POSTER_URL =
            "http://image.tmdb.org/t/p";
    private static final String MOVIE_DATA_URL =
            "https://api.themoviedb.org/3/movie" ;
    private static final String API_KEY_QUERY =
            "?api_key=";
    private static final String API_KEY =
            "Insert Your API KEY";

    private static final String size = "/w342/";

    public static String getMovieDataUrl(String sortBy) {
        return MOVIE_DATA_URL + "/" + sortBy + API_KEY_QUERY +  API_KEY;
    }

    public static String getMoviePosterUrl(String path) {
        return MOVIE_POSTER_URL + size + path;
    }
}
