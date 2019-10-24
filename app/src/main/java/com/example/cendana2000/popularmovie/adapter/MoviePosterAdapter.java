package com.example.cendana2000.popularmovie.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.cendana2000.popularmovie.R;
import com.example.cendana2000.popularmovie.utilities.MovieDBResult;
import com.example.cendana2000.popularmovie.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import static com.example.cendana2000.popularmovie.data.MovieContract.MovieEntry;

/**
 * Created by Cendana2000 on 30-Jun-17.
 */

public class MoviePosterAdapter extends RecyclerView.Adapter<MoviePosterAdapter.MoviePosterViewHolder> {
    private Cursor mMovieData;

    private Context context;
    private final ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void onListItemClick(MovieDBResult movieDBResult);
    }

    public MoviePosterAdapter(ListItemClickListener listener) {
        mOnClickListener = listener;
    }

    @Override
    public MoviePosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        Integer resourceId = R.layout.movie_poster_item;

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(resourceId, parent, false);

        return new MoviePosterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviePosterViewHolder holder, int position) {
        mMovieData.moveToPosition(position);
        int posterIndex = mMovieData.getColumnIndex(MovieEntry.COLUMN_POSTER);
        String path = mMovieData.getString(posterIndex);
        String url  = NetworkUtils.getMoviePosterUrl(path);
        String urlGambar = url.replace("http:", "https:");
        Picasso.with(context).load(urlGambar).into(holder.mMoviePosterImageView);
    }

    @Override
    public int getItemCount() {
        if (null == mMovieData) return 0;
        return mMovieData.getCount();
    }

    public void setData(Cursor movieDBResults) {
        mMovieData = movieDBResults;
        notifyDataSetChanged();
    }

    class MoviePosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mMoviePosterImageView;

        public MoviePosterViewHolder(View itemView) {
            super(itemView);
            mMoviePosterImageView = (ImageView) itemView.findViewById(R.id.iv_movie_poster);
            mMoviePosterImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            mMovieData.moveToPosition(position);
            int idIndex = mMovieData.getColumnIndex(MovieEntry.COLUMN_ID);
            int titleIndex = mMovieData.getColumnIndex(MovieEntry.COLUMN_TITLE);
            int releaseIndex = mMovieData.getColumnIndex(MovieEntry.COLUMN_RELEASE);
            int ratingIndex = mMovieData.getColumnIndex(MovieEntry.COLUMN_RATING);
            int overviewIndex = mMovieData.getColumnIndex(MovieEntry.COLUMN_OVERVIEW);
            int posterIndex = mMovieData.getColumnIndex(MovieEntry.COLUMN_POSTER);
            int sortPrefIndex = mMovieData.getColumnIndex(MovieEntry.COLUMN_SORT_PREF);

            int id = mMovieData.getInt(idIndex);
            String title = mMovieData.getString(titleIndex);
            String release = mMovieData.getString(releaseIndex);
            int rating = mMovieData.getInt(ratingIndex);
            String overview = mMovieData.getString(overviewIndex);
            String poster = mMovieData.getString(posterIndex);
            String sortPref = mMovieData.getString(sortPrefIndex);

            MovieDBResult movieDBResult = new MovieDBResult();
            movieDBResult.setId(id);
            movieDBResult.setTitle(title);
            movieDBResult.setReleaseDate(release);
            movieDBResult.setVoteAverage(Double.valueOf(String.valueOf(rating)));
            movieDBResult.setOverview(overview);
            movieDBResult.setPosterPath(poster);

            mOnClickListener.onListItemClick(movieDBResult);
        }
    }
}
