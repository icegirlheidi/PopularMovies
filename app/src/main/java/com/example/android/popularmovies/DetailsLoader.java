package com.example.android.popularmovies;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.utilities.QueryUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetailsLoader extends AsyncTaskLoader<List<Movie>> {

    private final String mUrl;

    public DetailsLoader(Context context, String url) {
        super(context);
        this.mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Movie> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        String jsonResponse = QueryUtils.fetchMovieJson(mUrl);
        return extractFeatureFromJSON(jsonResponse);
    }

    private static List<Movie> extractFeatureFromJSON(String moviesJSON) {
        List<Movie> moviesList = new ArrayList<>();
        List<String> genresList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(moviesJSON);
            String originalTitle = jsonObject.getString(Constants.JSON_PARSE_ORIGINAL_TITLE);
            String backdropPath = Constants.IMAGE_BASE_URL
                    + Constants.IMAGE_SIZE_EXTRA_LARGE
                    + jsonObject.getString(Constants.JSON_PARSE_BACKDROP_PATH);

            String overView = jsonObject.getString(Constants.JSON_PARSE_OVERVIEW_PATH);
            String posterPath = Constants.IMAGE_BASE_URL
                    + Constants.IMAGE_SIZE_EXTRA_LARGE
                    + jsonObject.getString(Constants.JSON_PARSE_POSTER_PATH);
            String releaseDate = jsonObject.getString(Constants.JSON_PARSE_RELEASE_DATE_PATH);
            JSONArray genresArray = jsonObject.getJSONArray(Constants.JSON_PARSE_GENRES);
            for (int i = 0; i < genresArray.length(); i++) {
                JSONObject currentGenres = genresArray.getJSONObject(i);
                String genresName = currentGenres.getString(Constants.JSON_PARSE_NAME);
                genresList.add(genresName);
            }
            double voteAverage = jsonObject.getDouble(Constants.JSON_PARSE_VOTE_AVERAGE);

            Movie movie = new Movie(originalTitle, posterPath);
            movie.setBackdropPath(backdropPath);
            movie.setOverview(overView);
            movie.setReleaseDate(releaseDate);
            movie.setGenres(genresList);
            movie.setVoteRate(voteAverage);
            moviesList.add(movie);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return moviesList;
    }
}
