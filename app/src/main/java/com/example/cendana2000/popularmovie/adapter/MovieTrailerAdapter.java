package com.example.cendana2000.popularmovie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.cendana2000.popularmovie.R;
import com.example.cendana2000.popularmovie.utilities.MovieDBTrailersResult;

import java.util.List;

/**
 * Created by Cendana2000 on 27-Aug-17.
 */

public class MovieTrailerAdapter extends RecyclerView.Adapter<MovieTrailerAdapter.MoviePosterViewHolder> {
    private List<MovieDBTrailersResult> mMovieTrailerData;
    private final ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void onListItemClick(MovieDBTrailersResult movieDBTrailersResult);
    }


    public MovieTrailerAdapter(ListItemClickListener listener) {
        mOnClickListener = listener;
    }

    @Override
    public MoviePosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        Integer resourceId = R.layout.movie_trailer_item;

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(resourceId, parent, false);

        return new MoviePosterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviePosterViewHolder holder, int position) {
        MovieDBTrailersResult data = mMovieTrailerData.get(position);
        String trailerTitle = data.getName();
        holder.mMovieTrailerButton.setText(trailerTitle);
    }

    @Override
    public int getItemCount() {
        if(mMovieTrailerData == null) return 0;
        return mMovieTrailerData.size();
    }

    public void setData(List<MovieDBTrailersResult> movieDBTrailersResults) {
        mMovieTrailerData = movieDBTrailersResults;
        notifyDataSetChanged();
    }

    class MoviePosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Button mMovieTrailerButton;

        public MoviePosterViewHolder(View itemView) {
            super(itemView);
            mMovieTrailerButton = (Button) itemView.findViewById(R.id.b_movie_trailer);
            mMovieTrailerButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            MovieDBTrailersResult movieDBTrailersResult = mMovieTrailerData.get(position);
            mOnClickListener.onListItemClick(movieDBTrailersResult);
        }
    }
}
