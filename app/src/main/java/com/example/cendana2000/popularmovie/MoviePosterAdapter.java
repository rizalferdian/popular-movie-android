package com.example.cendana2000.popularmovie;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.cendana2000.popularmovie.utilities.MovieDBResult;
import com.example.cendana2000.popularmovie.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Cendana2000 on 30-Jun-17.
 */

public class MoviePosterAdapter extends RecyclerView.Adapter<MoviePosterAdapter.MoviePosterViewHolder> {
    private List<MovieDBResult> mMovieData;

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
        String path = mMovieData.get(position).getPosterPath();
        String url  = NetworkUtils.getMoviePosterUrl(path);
        Picasso.with(context).load(url).into(holder.mMoviePosterImageView);
    }

    @Override
    public int getItemCount() {
        if (null == mMovieData) return 0;
        return mMovieData.size();
    }

    public void setData(List<MovieDBResult> movieDBResults) {
        mMovieData = movieDBResults;
        notifyDataSetChanged();
    }

    public void addData(List<MovieDBResult> movieDBResults) {
        mMovieData.addAll(movieDBResults);
        int lastIndex = mMovieData.size() - 1;
        notifyItemRangeInserted(lastIndex, movieDBResults.size());
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
            MovieDBResult movieDBResult = mMovieData.get(position);
            mOnClickListener.onListItemClick(movieDBResult);
        }
    }
}
