package com.example.cendana2000.popularmovie;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cendana2000.popularmovie.adapter.MoviePosterAdapter;
import com.example.cendana2000.popularmovie.utilities.MovieDBResponse;
import com.example.cendana2000.popularmovie.utilities.MovieDBResult;
import com.example.cendana2000.popularmovie.utilities.MovieDBService;
import com.example.cendana2000.popularmovie.utilities.NetworkUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.cendana2000.popularmovie.data.MovieContract.MovieEntry;

public class MainActivity extends AppCompatActivity implements
        MoviePosterAdapter.ListItemClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    private MoviePosterAdapter mMoviePosterAdapter;
    @BindView(R.id.rv_movie_poster)
    RecyclerView mRecyclerView;
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;
    @BindView(R.id.tv_display_error) LinearLayout mDisplayError;
    @BindView(R.id.btn_refresh) Button mBtnRefresh;

    @BindString(R.string.pref_sort_by_key) String sortByKey;
    @BindString(R.string.pref_sort_by_default) String sortByDefault;
    @BindString(R.string.pref_sort_by_favorite) String sortByFavorite;

    private final String RECYCLER_VIEW_KEY = "rv_location";
    private static final int ID_FORECAST_LOADER = 91;

    int currentPage = 1;
    SharedPreferences sharedPreferences;
    GridLayoutManager layoutManager;
    Bundle mLayoutManagerSavedState = null;
    private LoaderManager mLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoader = LoaderManager.getInstance(this);

        // binding
        ButterKnife.bind(this);

        // setting recyleview
        mMoviePosterAdapter = new MoviePosterAdapter(this);
        layoutManager = new GridLayoutManager(this, calculateNoOfColumns(this));
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mMoviePosterAdapter);
        mRecyclerView.setHasFixedSize(true);

        // register SharedPreference
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        if(savedInstanceState != null) {
            mLayoutManagerSavedState = savedInstanceState;
        }
        setDataToRecylerView();

        mBtnRefresh.setOnClickListener(view -> {
            setDataToRecylerView();
        });
    }

    // method to calculate the columns
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 180;
        return (int) (dpWidth / scalingFactor);
    }

    private void loadMovieData(int page) {
        final String prefSortBy = sharedPreferences.getString(sortByKey, sortByDefault);
        String baseUrl    = NetworkUtils.getMovieDBBaseUrl();
        String apiKey     = NetworkUtils.getAPIKey();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieDBService service = retrofit.create(MovieDBService.class);

        mLoadingIndicator.setVisibility(View.VISIBLE);
        Call<MovieDBResponse> call = service.getMovies(prefSortBy, page, apiKey);
        call.enqueue(new Callback<MovieDBResponse>() {
            @Override
            public void onResponse(Call<MovieDBResponse> call, Response<MovieDBResponse> response) {
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                if(response != null) {
                    showMovieData();
                    MovieDBResponse movieDBResponse = response.body();
                    insertMovieData(movieDBResponse, prefSortBy);
                    setDataToRecylerView();
                } else {
                    showDisplayError();
                }
            }

            @Override
            public void onFailure(Call<MovieDBResponse> call, Throwable t) {
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                Toast.makeText(getBaseContext(), "Failed to fetch Movie Data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setDataToRecylerView() {
        String prefSortBy = sharedPreferences.getString(sortByKey, sortByDefault);
        Cursor cursor = getContentResolver().query(MovieEntry.CONTENT_URI, null, MovieEntry.COLUMN_SORT_PREF + "=?", new String[]{prefSortBy}, null);
        if(cursor.getCount() > 0) {
            mLoader.restartLoader(ID_FORECAST_LOADER, null, this);
        } else if(prefSortBy.equals(sortByFavorite)) {
            Toast.makeText(getBaseContext(), "You don't have any Favorite Movie yet.", Toast.LENGTH_SHORT).show();
        } else {
            loadMovieData(currentPage);
        }
    }
    
    public void insertMovieData(MovieDBResponse movieDBResponse, String prefSortBy) {
        List<ContentValues> contentValuesList = new ArrayList<>();
        List<MovieDBResult> movieDBResults = movieDBResponse.getResults();
        for (MovieDBResult movieDBResult: movieDBResults) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieEntry.COLUMN_ID, movieDBResult.getId());
            contentValues.put(MovieEntry.COLUMN_TITLE, movieDBResult.getTitle());
            contentValues.put(MovieEntry.COLUMN_RELEASE, movieDBResult.getReleaseDate());
            contentValues.put(MovieEntry.COLUMN_RATING, movieDBResult.getVoteAverage());
            contentValues.put(MovieEntry.COLUMN_OVERVIEW, movieDBResult.getOverview());
            contentValues.put(MovieEntry.COLUMN_POSTER, movieDBResult.getPosterPath());
            contentValues.put(MovieEntry.COLUMN_SORT_PREF, prefSortBy);
            contentValuesList.add(contentValues);
        }

        ContentValues[] contentValues = new ContentValues[contentValuesList.size()];
        contentValues = contentValuesList.toArray(contentValues);
        getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, contentValues);
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
        setDataToRecylerView();
    }

    // start intent to go to detail activity
    @Override
    public void onListItemClick(MovieDBResult movieDBResult) {
        Intent intent = new Intent(this, MovieDetailActivity.class);

        Gson gson = new Gson();
        intent.putExtra("movieDBResult", gson.toJson(movieDBResult));
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Parcelable parcelable = mRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(RECYCLER_VIEW_KEY, parcelable);
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

    private void restoreLayoutManagerPosition() {
        if(mLayoutManagerSavedState != null){
            Parcelable parcelable = mLayoutManagerSavedState.getParcelable(RECYCLER_VIEW_KEY);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(parcelable);
            mLayoutManagerSavedState = null;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch(id) {
            case ID_FORECAST_LOADER:
                Uri uri = MovieEntry.CONTENT_URI;
                String selection = MovieEntry.COLUMN_SORT_PREF + "=?";

                String prefSortBy = sharedPreferences.getString(sortByKey, sortByDefault);
                return new CursorLoader(this, uri, null, selection, new String[]{ prefSortBy }, null);
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMoviePosterAdapter.setData(data);
        restoreLayoutManagerPosition();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviePosterAdapter.setData(null);
    }
}

