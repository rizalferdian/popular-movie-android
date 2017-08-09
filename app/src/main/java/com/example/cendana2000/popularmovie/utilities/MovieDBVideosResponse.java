package com.example.cendana2000.popularmovie.utilities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Cendana2000 on 31-Jul-17.
 */

public class MovieDBVideosResponse {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private List<MovieDBVideosResult> results = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<MovieDBVideosResult> getResults() {
        return results;
    }

    public void setResults(List<MovieDBVideosResult> results) {
        this.results = results;
    }
}
