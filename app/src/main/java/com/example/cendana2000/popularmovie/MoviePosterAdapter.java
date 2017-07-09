package com.example.cendana2000.popularmovie;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.cendana2000.popularmovie.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by Cendana2000 on 30-Jun-17.
 */

public class MoviePosterAdapter extends RecyclerView.Adapter<MoviePosterAdapter.MoviePosterViewHolder> {
    private String[] mMoviePosterPath;
    private String[] mMovieData;

    private Context context;
    private final ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void onListItemClick(String url, String data);
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
        String path = mMoviePosterPath[position];
        String url  = NetworkUtils.getMoviePosterUrl(path);
        Picasso.with(context).load(url).into(holder.mMoviePosterImageView);
    }

    @Override
    public int getItemCount() {
        if (null == mMoviePosterPath) return 0;
        return mMoviePosterPath.length;
    }

    public void setData(String[] jsonString, String[] data) {
        mMoviePosterPath = jsonString;
        mMovieData = data;
        notifyDataSetChanged();
    }

    class MoviePosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mMoviePosterImageView;

        public MoviePosterViewHolder(View itemView) {
            super(itemView);
            mMoviePosterImageView = (ImageView) itemView.findViewById(R.id.iv_movie_poster);
            mMoviePosterImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            String path = mMoviePosterPath[position];
            String url  = NetworkUtils.getMoviePosterUrl(path);

            String mMovieDataString = mMovieData[position];

            mOnClickListener.onListItemClick(url, mMovieDataString);
        }
    }
}
