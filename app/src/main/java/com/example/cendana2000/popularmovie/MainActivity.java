package com.example.cendana2000.popularmovie;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.cendana2000.popularmovie.utilities.MovieJsonUtils;
import com.example.cendana2000.popularmovie.utilities.NetworkUtils;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements MoviePosterAdapter.ListItemClickListener {
    private MoviePosterAdapter mMoviePosterAdapter;
    private RecyclerView mRecyclerView;
    private ProgressBar mLoadingIndicator;
    private LinearLayout mDisplayError;
    private String sortBy = "popular";

    private MenuItem mSortByPopular;
    private MenuItem mSortByRated;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mMoviePosterAdapter = new MoviePosterAdapter(this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_poster);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mMoviePosterAdapter);
        mRecyclerView.setHasFixedSize(true);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mDisplayError = (LinearLayout) findViewById(R.id.tv_display_error);


        loadMovieData();

        intent = new Intent(this, MovieDetailActivity.class);
    }

    private void loadMovieData() {
        String url = NetworkUtils.getMovieDataUrl(sortBy);
        new TheMovieDBQueryTask().execute(url);
    }

    @Override
    public void onListItemClick(String url, String data) {
        intent.putExtra("url", url);
        intent.putExtra("data", data);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        mSortByPopular = menu.findItem(R.id.action_sort_by_most_popular);
        mSortByRated = menu.findItem(R.id.action_sort_by_highest_rated);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_sort_by_most_popular) {
            showItemSortByRated();

            loadMovieData();
            return true;
        }

        if(id == R.id.action_sort_by_highest_rated) {
            showItemSortByPopular();

            loadMovieData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showItemSortByPopular() {
        mSortByRated.setVisible(false);
        mSortByPopular.setVisible(true);
        sortBy = "top_rated";
    }

    private void showItemSortByRated() {
        mSortByPopular.setVisible(false);
        mSortByRated.setVisible(true);
        sortBy = "popular";
    }

    private void showMovieData() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mDisplayError.setVisibility(View.INVISIBLE);
    }

    private void showDisplayError() {
        mDisplayError.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    public void onClickBtn(android.view.View view) {
        loadMovieData();
    }

    private class TheMovieDBQueryTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mMoviePosterAdapter.setData(null, null);
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String url = params[0];

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                .url(url)
                .build();

            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (NullPointerException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String jsonString) {
            super.onPostExecute(jsonString);
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            if(jsonString != null) {
                showMovieData();
                String[] moviePath = MovieJsonUtils.getMoviePosterPath(jsonString);
                String[] movieData = MovieJsonUtils.getMovieData(jsonString);
                mMoviePosterAdapter.setData(moviePath, movieData);
            } else {
                showDisplayError();
            }

        }
    }
}

