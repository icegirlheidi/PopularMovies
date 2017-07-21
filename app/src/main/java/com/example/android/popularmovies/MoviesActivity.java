package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.app.LoaderManager;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MoviesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movies>>, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String LOG_TAG = MoviesActivity.class.getName();

    private static final int LOADER_ID = 1;

    private RecyclerView mRecyclerView;

    private MoviesAdapter mMoviesAdapter;

    private static boolean PREFERENCES_HAVE_BEEN_CHANGED = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
        int spanCount = calculateNoOfColumns(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));
        mRecyclerView.setAdapter(new MoviesAdapter(this, new ArrayList<Movies>()));

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            // Initialize the loader if there is internet connection
            getLoaderManager().initLoader(LOADER_ID, null, this);
        }

        /*
         * Register MoviesActivity as an OnPreferenceChangedListener to receive a callback when a
         * SharedPreference has changed.
         */
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // If the preferences for order by has changed, make another query and set the flag to false.
        getLoaderManager().restartLoader(LOADER_ID, null, this);
        PREFERENCES_HAVE_BEEN_CHANGED = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /* Unregister MoviesActivity as an OnPreferenceChangedListener to avoid any memory leaks. */
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_settings:
                Intent intent = new Intent(MoviesActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<Movies>> onCreateLoader(int id, Bundle args) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String orderBy = sharedPreferences.getString(getString(R.string.pref_order_by_key), getString(R.string.pref_order_by_popularity_value));

        Uri baseUri = Uri.parse(Constants.BASE_URL + orderBy);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        // Build uri, for example:
        // "http://api.themoviedb.org/3/movie/popular?api_key=[your api key here]"
        uriBuilder.appendQueryParameter(Constants.API_KEY_PARAM, Constants.api_key).build();
        // Initialize MoviesLoader
        return new MoviesLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Movies>> loader, List<Movies> moviesList) {
        mMoviesAdapter = new MoviesAdapter(this, new ArrayList<Movies>());
        if (moviesList != null && !(moviesList.isEmpty())) {
            mMoviesAdapter = new MoviesAdapter(this, moviesList);
            mRecyclerView.setAdapter(mMoviesAdapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movies>> loader) {
        mMoviesAdapter = new MoviesAdapter(this, new ArrayList<Movies>());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        PREFERENCES_HAVE_BEEN_CHANGED = true;
    }

    /**
     * Adjust number of columns of posters based on screen width
     *
     * @param context the context
     * @return the number of columns
     */
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }
}
