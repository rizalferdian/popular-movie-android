package com.example.cendana2000.popularmovie;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        TextView mMovieTitle = (TextView) findViewById(R.id.tv_movie_title);
        TextView mMovieRating = (TextView) findViewById(R.id.tv_movie_rating);
        TextView mMovieRelease = (TextView) findViewById(R.id.tv_movie_release);
        TextView mMovieOverview = (TextView) findViewById(R.id.tv_movie_overview);

        ImageView mMovieThumbnail = (ImageView) findViewById(R.id.iv_movie_thumbnail);

        Intent intent = getIntent();
        if(intent.hasExtra("url")) {
            String url = intent.getStringExtra("url");
            Picasso.with(this).load(url).into(mMovieThumbnail);
        }

        if(intent.hasExtra("data")) {
            String data = intent.getStringExtra("data");
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String title = null;
            try {
                assert jsonObject != null;
                title = jsonObject.getString("title");
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            mMovieTitle.setText(title);

            String rating = null;
            try {
                rating = jsonObject.getString("vote_average");
                rating = rating + getString(R.string.rating_max_value);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            mMovieRating.setText(rating);

            String release = null;
            try {
                release = jsonObject.getString("release_date");
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            mMovieRelease.setText(release);

            String overview = null;
            try {
                overview = jsonObject.getString("overview");
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            mMovieOverview.setText(overview);
        }
    }
}
