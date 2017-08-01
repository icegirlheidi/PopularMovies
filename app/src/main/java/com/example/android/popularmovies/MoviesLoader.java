package com.example.android.popularmovies;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.popularmovies.utilities.QueryUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MoviesLoader extends AsyncTaskLoader<List<Movies>> {

    private final String mUrl;

    private List<Movies> mList;

    public MoviesLoader(Context context, String url) {
        super(context);
        this.mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        if (mList != null) {
            deliverResult(mList);
        } else {
            forceLoad();
        }
    }

    @Override
    public List<Movies> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        // Fetch the json file
        String jsonResponse = QueryUtils.fetchMovieJson(mUrl);
        // Extract usable info from json file
        return extractFeatureFromJSON(jsonResponse);
    }

    @Override
    public void deliverResult(List<Movies> data) {
        mList = data;
        super.deliverResult(data);
    }

    /**
     * Extract usable info from json file and set them into views.
     *
     * @param moviesJSON is the String of json
     * @return a List with class Movies
     */
    private static List<Movies> extractFeatureFromJSON(String moviesJSON) {
        List<Movies> moviesList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(moviesJSON);
            JSONArray results = jsonObject.getJSONArray(Constants.JSON_PARSE_RESULTS);
            for (int i = 0; i < results.length(); i++) {
                JSONObject currentMovie = results.getJSONObject(i);
                // Get the original title as String, for example "Spider-Man: Homecoming"
                String originalTitle = currentMovie.getString(Constants.JSON_PARSE_ORIGINAL_TITLE);
                // Get the posterPath, for example "http://image.tmdb.org/t/p/w185//ApYhuwBWzl29Oxe9JJsgL7qILbD.jpg"
                String posterPath = Constants.IMAGE_BASE_URL + Constants.IMAGE_SIZE_LARGE + currentMovie.getString(Constants.JSON_PARSE_POSTER_PATH);
                // Get the id, for example 315635;
                int id = currentMovie.getInt(Constants.JSON_PARSE_ID);
                Movies movie = new Movies(originalTitle, posterPath);
                movie.setId(id);
                moviesList.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return moviesList;
    }
}
