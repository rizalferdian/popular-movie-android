package com.example.cendana2000.popularmovie;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cendana2000.popularmovie.adapter.MovieReviewAdapter;
import com.example.cendana2000.popularmovie.adapter.MovieTrailerAdapter;
import com.example.cendana2000.popularmovie.utilities.MovieDBResult;
import com.example.cendana2000.popularmovie.utilities.MovieDBReviewsResponse;
import com.example.cendana2000.popularmovie.utilities.MovieDBReviewsResult;
import com.example.cendana2000.popularmovie.utilities.MovieDBService;
import com.example.cendana2000.popularmovie.utilities.MovieDBTrailersResponse;
import com.example.cendana2000.popularmovie.utilities.MovieDBTrailersResult;
import com.example.cendana2000.popularmovie.utilities.NetworkUtils;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.cendana2000.popularmovie.data.MovieContract.MovieEntry;

public class MovieDetailActivity extends AppCompatActivity implements
    MovieTrailerAdapter.ListItemClickListener{
    @BindView(R.id.tv_movie_title) TextView mMovieTitle;
    @BindView(R.id.tv_movie_rating) TextView mMovieRating;
    @BindView(R.id.tv_movie_release) TextView mMovieRelease;
    @BindView(R.id.tv_movie_overview) TextView mMovieOverview;
    @BindView(R.id.tv_movie_review) TextView mMovieReview;
    @BindView(R.id.tv_movie_trailer) TextView mMovieTrailerLabel;
    @BindView(R.id.b_movie_favorite) TextView mMovieButtonFavorite;

    @BindView(R.id.iv_movie_thumbnail) ImageView mMovieThumbnail;

    @BindView(R.id.rv_movie_trailer) RecyclerView mMovieTrailerRecylerView;
    @BindView(R.id.rv_movie_review) RecyclerView mMovieReviewRecylerView;

    @BindView(R.id.sv_movie_detail) ScrollView mMovieDetailScrollView;

    @BindString(R.string.mark_as_favorite) String mMarkFavorite;
    @BindString(R.string.unmark_from_favorite) String mUnmarkFavorite;
    @BindString(R.string.pref_sort_by_favorite) String mSortByFavorite;

    private MovieDBResult movieDBResult;
    private MovieTrailerAdapter movieTrailerAdapter;
    private MovieReviewAdapter movieReviewAdapter;
    private final String SCROLL_POSITION = "sv_posisition";
    private Bundle scrollviewInstanceState = null;
    private int loadingCount = 0;

    Boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        ButterKnife.bind(this);

        movieTrailerAdapter = new MovieTrailerAdapter(this);
        LinearLayoutManager trailerLinearLayoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mMovieTrailerRecylerView.setAdapter(movieTrailerAdapter);
        mMovieTrailerRecylerView.setLayoutManager(trailerLinearLayoutManager);
        mMovieTrailerRecylerView.setHasFixedSize(false);

        LinearLayoutManager reviewLinearLayoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        movieReviewAdapter = new MovieReviewAdapter();
        mMovieReviewRecylerView.setAdapter(movieReviewAdapter);
        mMovieReviewRecylerView.setLayoutManager(reviewLinearLayoutManager);
        mMovieReviewRecylerView.setHasFixedSize(false);

        setDataToView();
        checkIsFavorite();
        setButtonFavoriteText();
        loadMovieTrailer();
        loadMovieReview();

        if(savedInstanceState != null) {
            scrollviewInstanceState = savedInstanceState;
        }
    }

    public void setDataToView() {
        Intent intent = getIntent();
        if(intent.hasExtra("movieDBResult")) {
            String dataSring = intent.getStringExtra("movieDBResult");

            Gson gson = new Gson();
            movieDBResult = gson.fromJson(dataSring, MovieDBResult.class);

            String path = movieDBResult.getPosterPath();
            String url  = NetworkUtils.getMoviePosterUrl(path);
            String urlGambar = url.replace("http:", "https:");
            Picasso.with(this).load(urlGambar).into(mMovieThumbnail);

            mMovieTitle.setText(movieDBResult.getTitle());
            mMovieRelease.setText(movieDBResult.getReleaseDate());
            mMovieRating.setText(movieDBResult.getVoteAverage().toString());
            mMovieOverview.setText(movieDBResult.getOverview());
        }
    }

    public void loadMovieTrailer() {
        String baseUrl    = NetworkUtils.getMovieDBBaseUrl();
        String apiKey     = NetworkUtils.getAPIKey();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieDBService service = retrofit.create(MovieDBService.class);
        Call<MovieDBTrailersResponse> call = service.getVideos(movieDBResult.getId(), apiKey);
        call.enqueue(new Callback<MovieDBTrailersResponse>() {
            @Override
            public void onResponse(Call<MovieDBTrailersResponse> call, Response<MovieDBTrailersResponse> response) {
                List<MovieDBTrailersResult> movieDBTrailersResults = response.body().getResults();
                if(movieDBTrailersResults.size() > 0) {
                    mMovieTrailerLabel.setVisibility(View.VISIBLE);
                    movieTrailerAdapter.setData(movieDBTrailersResults);
                }
                restoreScrollViewPosition();
            }

            @Override
            public void onFailure(Call<MovieDBTrailersResponse> call, Throwable t) {
                Toast.makeText(getBaseContext(), "Failed to fetch Movie Trailer Data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loadMovieReview() {
        String baseUrl    = NetworkUtils.getMovieDBBaseUrl();
        String apiKey     = NetworkUtils.getAPIKey();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieDBService service = retrofit.create(MovieDBService.class);
        Call<MovieDBReviewsResponse> call = service.getReviews(movieDBResult.getId(), apiKey);
        call.enqueue(new Callback<MovieDBReviewsResponse>() {
            @Override
            public void onResponse(Call<MovieDBReviewsResponse> call, Response<MovieDBReviewsResponse> response) {
                List<MovieDBReviewsResult> movieDBReviewsResults = response.body().getResults();
                if(movieDBReviewsResults.size() > 0) {
                    mMovieReview.setVisibility(View.VISIBLE);
                    movieReviewAdapter.setData(movieDBReviewsResults);
                }
                restoreScrollViewPosition();
            }

            @Override
            public void onFailure(Call<MovieDBReviewsResponse> call, Throwable t) {
                Toast.makeText(getBaseContext(), "Failed to fetch Movie Review Data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void openVideoTrailer(MovieDBTrailersResult movieDBTrailersResult) {
        String key = movieDBTrailersResult.getKey();

        Intent intent = new  Intent(Intent.ACTION_VIEW);
        intent.setPackage(NetworkUtils.getYoutubePackage());
        intent.setData(NetworkUtils.getYoutubeUrl(key));

        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @OnClick(R.id.b_movie_favorite)
    public void onClickFavorite() {
        if(isFavorite){
            unsetFromFavorite();
        } else {
            setToFavorite();
        }
    }

    public void checkIsFavorite() {
        Cursor cursor = getContentResolver().query(MovieEntry.CONTENT_URI, null, MovieEntry.COLUMN_ID + "=? and " + MovieEntry.COLUMN_SORT_PREF + "=?", new String[]{movieDBResult.getId().toString(), mSortByFavorite}, null);
        isFavorite = cursor.moveToFirst();
    }

    public void setToFavorite() {
        isFavorite = true;
        setButtonFavoriteText();

        ContentValues values = new ContentValues();
        values.put(MovieEntry.COLUMN_ID, movieDBResult.getId());
        values.put(MovieEntry.COLUMN_TITLE, movieDBResult.getTitle());
        values.put(MovieEntry.COLUMN_RELEASE, movieDBResult.getReleaseDate());
        values.put(MovieEntry.COLUMN_RATING, movieDBResult.getVoteAverage());
        values.put(MovieEntry.COLUMN_OVERVIEW, movieDBResult.getOverview());
        values.put(MovieEntry.COLUMN_POSTER, movieDBResult.getPosterPath());
        values.put(MovieEntry.COLUMN_SORT_PREF, mSortByFavorite);
        Uri uri = getContentResolver().insert(MovieEntry.CONTENT_URI, values);

        if(uri != null) {
            Toast.makeText(this, "Being mark as Favorite", Toast.LENGTH_SHORT).show();
        }
    }

    public void unsetFromFavorite() {
        isFavorite = false;
        setButtonFavoriteText();

        String id = movieDBResult.getId().toString();
        Uri uri = MovieEntry.CONTENT_URI.buildUpon().appendPath(id).build();
        Toast.makeText(this, "Being unmark from Favorite", Toast.LENGTH_SHORT).show();
        getContentResolver().delete(uri, null, null);
    }

    public void setButtonFavoriteText() {
        if(isFavorite) {
            mMovieButtonFavorite.setText(mUnmarkFavorite);
        } else {
            mMovieButtonFavorite.setText(mMarkFavorite);
        }
    }

    @Override
    public void onListItemClick(MovieDBTrailersResult movieDBTrailersResult) {
        openVideoTrailer(movieDBTrailersResult);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray(SCROLL_POSITION, new int[]{ mMovieDetailScrollView.getScrollX(), mMovieDetailScrollView.getScrollY() });
    }

    public void restoreScrollViewPosition() {
        loadingCount++;
        if(loadingCount == 2 && scrollviewInstanceState != null) {
            final int[] position = scrollviewInstanceState.getIntArray(SCROLL_POSITION);
            mMovieDetailScrollView.post(new Runnable() {
                @Override
                public void run() {
                    mMovieDetailScrollView.scrollTo(position[0], position[1]);
                }
            });
            loadingCount = 0;
            scrollviewInstanceState = null;
        }
    }
}
