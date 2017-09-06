package com.example.cendana2000.popularmovie.utilities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Cendana2000 on 31-Jul-17.
 */

public class MovieDBTrailersResponse {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private List<MovieDBTrailersResult> results = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<MovieDBTrailersResult> getResults() {
        return results;
    }

    public void setResults(List<MovieDBTrailersResult> results) {
        this.results = results;
    }
}
