package com.example.cendana2000.popularmovie;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.cendana2000.popularmovie.utilities.EndlessRecyclerViewScrollListener;
import com.example.cendana2000.popularmovie.utilities.MovieDBResponse;
import com.example.cendana2000.popularmovie.utilities.MovieDBResult;
import com.example.cendana2000.popularmovie.utilities.MovieDBService;
import com.example.cendana2000.popularmovie.utilities.NetworkUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements
        MoviePosterAdapter.ListItemClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener{

    private MoviePosterAdapter mMoviePosterAdapter;
    @BindView(R.id.rv_movie_poster) RecyclerView mRecyclerView;
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;
    @BindView(R.id.tv_display_error) LinearLayout mDisplayError;

    @BindString(R.string.pref_sort_by_key) String sortByKey;
    @BindString(R.string.pref_sort_by_default) String sortByDefault;

    private EndlessRecyclerViewScrollListener scrollListener;
    private final String RECYCLER_VIEW_KEY = "rv_location";

    int currentPage = 1;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // binding
        ButterKnife.bind(this);

        // setting recyleview
        mMoviePosterAdapter = new MoviePosterAdapter(this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, calculateNoOfColumns(this));
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mMoviePosterAdapter);
        mRecyclerView.setHasFixedSize(true);
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                currentPage = page;
                loadMovieData(currentPage);
            }
        };
        mRecyclerView.addOnScrollListener(scrollListener);

        // register SharedPreference
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        // loading the first data
        loadMovieData(currentPage);

        if(savedInstanceState != null){
            Parcelable parcelable = savedInstanceState.getParcelable(RECYCLER_VIEW_KEY);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(parcelable);
        }
    }

    // method to calculate the columns
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 180;
        return (int) (dpWidth / scalingFactor);
    }

    private void loadMovieData(int page) {
        String prefSortBy = sharedPreferences.getString(sortByKey, sortByDefault);
        String baseUrl = NetworkUtils.getMovieDBBaseUrl();
        String apiKey = NetworkUtils.getAPIKey();
        new TheMovieDBQueryTask().execute(baseUrl, prefSortBy, String.valueOf(page), apiKey);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_open_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // trigered when sortby preference being change
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        currentPage = 1;
        loadMovieData(currentPage);
    }

    // start intent to go to detail activity
    @Override
    public void onListItemClick(MovieDBResult movieDBResult) {
        Intent intent = new Intent(this, MovieDetailActivity.class);

        Gson gson = new Gson();
        intent.putExtra("movieDBResult", gson.toJson(movieDBResult));
        startActivity(intent);
    }

    private class TheMovieDBQueryTask extends AsyncTask<String, Void, Response<MovieDBResponse>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected Response<MovieDBResponse> doInBackground(String... params) {
            String baseUrl    = params[0];
            String prefSortBy = params[1];
            int page = Integer.valueOf(params[2]);
            String apiKey = params[3];

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            MovieDBService service = retrofit.create(MovieDBService.class);
            Call<MovieDBResponse> call = service.getMovies(prefSortBy, page, apiKey);

            try {
                return call.execute();
            } catch (IOException e ){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Response<MovieDBResponse> response) {
            super.onPostExecute(response);
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            if(response != null) {
                showMovieData();
                MovieDBResponse movieDBResponse = response.body();
                List<MovieDBResult> movieDBResults = movieDBResponse.getResults();
                if(currentPage == 1) {
                    mMoviePosterAdapter.setData(movieDBResults);
                } else {
                    mMoviePosterAdapter.addData(movieDBResults);
                }
            } else {
                showDisplayError();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Parcelable parcerlable = mRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(RECYCLER_VIEW_KEY, parcerlable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    // method to show the data
    private void showMovieData() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mDisplayError.setVisibility(View.INVISIBLE);
    }

    // method to show the error
    private void showDisplayError() {
        mDisplayError.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }
}

