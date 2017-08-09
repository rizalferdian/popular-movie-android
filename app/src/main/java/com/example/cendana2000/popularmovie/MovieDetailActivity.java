package com.example.cendana2000.popularmovie;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.cendana2000.popularmovie.data.MovieContract;
import com.example.cendana2000.popularmovie.utilities.MovieDBResult;
import com.example.cendana2000.popularmovie.utilities.MovieDBService;
import com.example.cendana2000.popularmovie.utilities.MovieDBVideosResponse;
import com.example.cendana2000.popularmovie.utilities.MovieDBVideosResult;
import com.example.cendana2000.popularmovie.utilities.NetworkUtils;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieDetailActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<MovieDBVideosResponse>{
    @BindView(R.id.tv_movie_title) TextView mMovieTitle;
    @BindView(R.id.tv_movie_rating) TextView mMovieRating;
    @BindView(R.id.tv_movie_release) TextView mMovieRelease;
    @BindView(R.id.tv_movie_overview) TextView mMovieOverview;

    @BindView(R.id.iv_movie_thumbnail) ImageView mMovieThumbnail;
    @BindView(R.id.b_movie_trailer) Button mMovieVideoTrailerButton;
    @BindView(R.id.tb_movie_favorite) ToggleButton mMovieFavoriteToggle;

    private MovieDBResult movieDBResult;
    private MovieDBVideosResult movieDBVideosResult;
    private byte[] posterByte;
    private static final int MOVIE_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        if(intent.hasExtra("movieDBResult")) {
            String dataSring = intent.getStringExtra("movieDBResult");

            Gson gson = new Gson();
            movieDBResult = gson.fromJson(dataSring, MovieDBResult.class);

            String path = movieDBResult.getPosterPath();
            String url  = NetworkUtils.getMoviePosterUrl(path);
            Picasso.with(this).load(url).into(mMovieThumbnail);

            mMovieTitle.setText(movieDBResult.getTitle());
            mMovieRelease.setText(movieDBResult.getReleaseDate());
            mMovieRating.setText(movieDBResult.getVoteAverage().toString());
            mMovieOverview.setText(movieDBResult.getOverview());
        }

        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
//        Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);
//        if(cursor.moveToFirst()) {
//            Log.e("data", cursor.toString());
//        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
    }

    public void openVideo(View view) {
        Intent intent = new  Intent(Intent.ACTION_VIEW);

        intent.setPackage("com.google.android.youtube");
        Uri uri = Uri.parse(NetworkUtils.getYoutubeBaseUrl());
        uri = uri.buildUpon().appendQueryParameter("v", movieDBVideosResult.getKey()).build();
        intent.setData(uri);

        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void toogleFavorite(View view) {
        Boolean checked = mMovieFavoriteToggle.isChecked();
        if(checked) {
            ContentValues values = new ContentValues();
            values.put(MovieContract.MovieEntry.COLUMN_TITLE, movieDBResult.getTitle());
            values.put(MovieContract.MovieEntry.COLUMN_RELEASE, movieDBResult.getReleaseDate());
            values.put(MovieContract.MovieEntry.COLUMN_RATING, movieDBResult.getVoteCount());
            values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movieDBResult.getOverview());
//            values.put(MovieContract.MovieEntry.COLUMN_POSTER, posterByte);
            Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);
            if(uri != null) {
                Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public Loader<MovieDBVideosResponse> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<MovieDBVideosResponse>(this) {
            MovieDBVideosResponse movieDBVideosResponse = null;

            @Override
            protected void onStartLoading() {
                if(movieDBVideosResponse != null) {
                    deliverResult(movieDBVideosResponse);
                } else {
                    forceLoad();
                }
            }

            @Override
            public MovieDBVideosResponse loadInBackground() {
                String baseUrl    = NetworkUtils.getMovieDBBaseUrl();
                String apiKey = NetworkUtils.getAPIKey();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                MovieDBService service = retrofit.create(MovieDBService.class);
                Call<MovieDBVideosResponse> call = service.getVideos(movieDBResult.getId(), apiKey);

                try {
                    return call.execute().body();
                } catch (IOException e ){
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(MovieDBVideosResponse data) {
                movieDBVideosResponse = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<MovieDBVideosResponse> loader, MovieDBVideosResponse data) {
        List<MovieDBVideosResult> movieDBVideosResultList = data.getResults();
        movieDBVideosResult = movieDBVideosResultList.get(0);
    }

    @Override
    public void onLoaderReset(Loader<MovieDBVideosResponse> loader) {
    }
}
