package com.example.android.popularmovies.utilities;


import android.net.Uri;
import android.util.Log;

import com.example.android.popularmovies.Movies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {

    private static final String TAG = QueryUtils.class.getName();

    private static final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";

    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185/";

    private static final String SORT_BY_PARAM = "sort_by";

    private static final String API_KEY_PARAM = "api_key";

    private static final String popularity = "popularity.desc";

    // Replace this with your own API key
    private static final String api_key = "";

    private static final String JSON_PARSE_RESULTS = "results";

    private static final String JSON_PARSE_ORIGINAL_TITLE = "original_title";

    private static final String JSON_PARSE_POSTER_PATH = "poster_path";

    public static List<Movies> fetchMoviesData() {
        URL url = buildUrl();
        String jsonResponse = "";
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Movies> listMovies = extractFeatureFromJSON(jsonResponse);
        return listMovies;
    }

    public static URL buildUrl() {
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(SORT_BY_PARAM, popularity)
                .appendQueryParameter(API_KEY_PARAM, api_key).build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "The built URL is: " + url);
        return url;
    }


    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if(inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<Movies> extractFeatureFromJSON(String moviesJSON) {
        List<Movies> moviesList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(moviesJSON);
            JSONArray results = jsonObject.getJSONArray(JSON_PARSE_RESULTS);
            for (int i = 0; i < results.length(); i++) {
                JSONObject currentMovie = results.getJSONObject(i);
                String originalTitle = currentMovie.getString(JSON_PARSE_ORIGINAL_TITLE);
                String posterPath = IMAGE_BASE_URL + currentMovie.getString(JSON_PARSE_POSTER_PATH);

                moviesList.add(new Movies(originalTitle, posterPath));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return moviesList;
    }
}
