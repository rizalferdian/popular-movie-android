package com.example.cendana2000.popularmovie.utilities;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Cendana2000 on 30-Jul-17.
 */

public interface MovieDBService {
    @GET("movie/{sortBy}")
    Call<MovieDBResponse> getMovies(@Path("sortBy") String sortBy, @Query("page") int page, @Query("api_key") String apiKey);

    @GET("movie/{id}/videos")
    Call<MovieDBTrailersResponse> getVideos(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("movie/{id}/reviews")
    Call<MovieDBReviewsResponse> getReviews(@Path("id") int id, @Query("api_key") String apiKey);
}
