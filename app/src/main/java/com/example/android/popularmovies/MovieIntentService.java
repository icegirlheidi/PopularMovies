package com.example.android.popularmovies;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.preference.PreferenceManager;
import android.widget.Toast;

import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.utilities.QueryUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MovieIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_FETCH_MOVIES = "com.example.android.popularmovies.ACTION_FETCH_MOVIES";
    public static final String ACTION_FETCH_DETAILS = "com.example.android.popularmovies.ACTION_FETCH_DETAILS";
    public static final String ACTION_FETCH_VIDEOS = "com.example.android.popularmovies.ACTION_FETCH_VIDEOS";
    public static final String ACTION_FETCH_REVIEWS = "com.example.android.popularmovies.ACTION_FETCH_REVIEWS";

    // TODO: Rename parameters
    public static final String EXTRA_MOVIES = "extra_movies";
    public static final String EXTRA_DETAILS= "extra_details";
    public static final String EXTRA_VIDEOS= "extra_videos";
    public static final String EXTRA_REVIEWS= "extra_reviews";

    public static final String EXTRA_PARAM1 = "PARAM1";
    public static final String EXTRA_PARAM2 = "PARAM2";

    public MovieIntentService() {
        super("MovieIntentService");
    }

    public static void fetchMovies(Context context) {
        Intent intent = new Intent(context, MovieIntentService.class);
        intent.setAction(ACTION_FETCH_MOVIES);
        context.startService(intent);
    }

/*    *//**//**//**//**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     *//**//**//**//*
    // TODO: Customize helper method
    public static void fetchDetails(Context context, String param1, String param2) {
        Intent intent = new Intent(context, MovieIntentService.class);
        intent.setAction(ACTION_FETCH_DETAILS);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    *//**//**//**//**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     *//**//**//**//*
    // TODO: Customize helper method
    public static void fetchVideos(Context context, String param1, String param2) {
        Intent intent = new Intent(context, MovieIntentService.class);
        intent.setAction(ACTION_FETCH_VIDEOS);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public static void fetchReviews(Context context, String param1, String param2) {
        Intent intent = new Intent(context, MovieIntentService.class);
        intent.setAction(ACTION_FETCH_REVIEWS);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }*/

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }
        if (intent != null) {
            final String action = intent.getAction();
            Intent broadcastIntent = new Intent();

            if (ACTION_FETCH_MOVIES.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_MOVIES);

                Uri uri = createMovieUri();
                String jsonResponse = QueryUtils.fetchMovieJson(uri.toString());
                ArrayList<Movie> moviesList = extractFeatureFromJSON(jsonResponse);
                broadcastIntent.setAction(MovieIntentService.ACTION_FETCH_MOVIES);
                if(moviesList != null) {
                    broadcastIntent.putParcelableArrayListExtra(EXTRA_MOVIES, moviesList);
                }

            } else if (ACTION_FETCH_DETAILS.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);

            } else if (ACTION_FETCH_VIDEOS.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
            } else if (ACTION_FETCH_REVIEWS.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
            }
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
        }
    }

    private Uri createMovieUri() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String orderBy = sharedPreferences.getString(getString(R.string.pref_order_by_key), getString(R.string.pref_order_by_popularity_value));

        Uri baseUri = Uri.parse(Constants.BASE_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        // Build uri, for example:
        // "http://api.themoviedb.org/3/movie/popular?api_key=[your api key here]"
        Uri uri = uriBuilder.appendPath(orderBy).appendQueryParameter(Constants.API_KEY_PARAM, Constants.API_KEY).build();
        // Initialize MoviesLoader
        return uri;
    }


    /**
     * Extract usable info from json file and set them into views.
     *
     * @param moviesJSON is the String of json
     * @return a List with class Movies
     */
    private static ArrayList<Movie> extractFeatureFromJSON(String moviesJSON) {
        ArrayList<Movie> moviesList = new ArrayList<>();
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
                Movie movie = new Movie(originalTitle, posterPath);
                movie.setId(id);
                moviesList.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return moviesList;
    }

    private void handleActionJaa(String param1) {
        // TODO: Handle action Jaa
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void handleActionHoo(String param1, String param2) {
        // TODO: Handle action Hoo
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
