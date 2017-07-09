package com.example.cendana2000.popularmovie.utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Cendana2000 on 03-Jul-17.
 */

public class MovieJsonUtils {
    public static String[] getMoviePosterPath(String jsonString) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray itemsArray = null;
        try {
            itemsArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String[] results = new String[itemsArray.length()];

        for(int i = 0; i<itemsArray.length(); i++){
            JSONObject moviePosterData = null;
            try {
                moviePosterData = itemsArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                results[i] = moviePosterData.getString("poster_path");
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        return results;
    }

    public static String[] getMovieData(String jsonString) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray itemsArray = null;
        try {
            itemsArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String[] results = new String[itemsArray.length()];

        for(int i = 0; i<itemsArray.length(); i++){
            try {
                results[i] = itemsArray.getJSONObject(i).toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return results;
    }
}
