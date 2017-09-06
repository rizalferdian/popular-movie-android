package com.example.cendana2000.popularmovie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cendana2000.popularmovie.R;
import com.example.cendana2000.popularmovie.utilities.MovieDBReviewsResult;

import java.util.List;

/**
 * Created by Cendana2000 on 27-Aug-17.
 */

public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.MovieReviewViewHolder> {
    private List<MovieDBReviewsResult> movieReviewData;

    @Override
    public MovieReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int resourceId = R.layout.movie_review_item;

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(resourceId, parent, false);

        return new MovieReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieReviewViewHolder holder, int position) {
        MovieDBReviewsResult movieDBReviewsResult = movieReviewData.get(position);
        holder.mMovieTrailerContentTextView.setText(movieDBReviewsResult.getContent());
        holder.mMovieTrailerAuthorTextView.setText(movieDBReviewsResult.getAuthor());
    }

    @Override
    public int getItemCount() {
        if(movieReviewData == null) return 0;
        return movieReviewData.size();
    }

    public void setData(List<MovieDBReviewsResult> movieDBReviewsResults) {
        movieReviewData = movieDBReviewsResults;
        notifyDataSetChanged();
    }

    class MovieReviewViewHolder extends RecyclerView.ViewHolder {
        TextView mMovieTrailerAuthorTextView;
        TextView mMovieTrailerContentTextView;
        public MovieReviewViewHolder(View itemView) {
            super(itemView);
            mMovieTrailerAuthorTextView = (TextView) itemView.findViewById(R.id.tv_movie_review_author);
            mMovieTrailerContentTextView = (TextView) itemView.findViewById(R.id.tv_movie_review_content);
        }
    }
}
