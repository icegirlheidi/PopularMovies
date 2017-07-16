package com.example.android.popularmovies;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.popularmovies.utilities.QueryUtils;

import java.util.List;

public class MoviesLoader extends AsyncTaskLoader<List<Movies>> {

    public MoviesLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Movies> loadInBackground() {
        List<Movies> movies = QueryUtils.fetchMoviesData();
        return movies;
    }
}
