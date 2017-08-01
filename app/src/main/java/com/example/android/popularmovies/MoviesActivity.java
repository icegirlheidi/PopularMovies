package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.app.LoaderManager;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MoviesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movies>>, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int LOADER_ID = 1;

    private RecyclerView mRecyclerView;

    private MoviesAdapter mMoviesAdapter;

    private static boolean PREFERENCES_HAVE_BEEN_CHANGED = false;

    private TextView mEmptyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        // Number of columns to show movie posters
        int spanCount = calculateNoOfColumns(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, spanCount);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(new MoviesAdapter(this, new ArrayList<Movies>()));

        mEmptyTextView = (TextView) findViewById(R.id.empty_text_view);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            // Initialize the loader if there is internet connection
            getLoaderManager().initLoader(LOADER_ID, null, this);
        } else {
            // When there is no internet connection,
            // Remove the loading progress bar and display no internet connection
            View loadingProgress = findViewById(R.id.loading_progress);
            loadingProgress.setVisibility(View.GONE);
            mEmptyTextView.setVisibility(View.VISIBLE);
            mEmptyTextView.setText(R.string.no_intenet);
        }

        // Scroll to lastly visible movie if there is saved instance state
        if (savedInstanceState != null) {
            Parcelable state = savedInstanceState.getParcelable(getString(R.string.view_state));
            mRecyclerView.getLayoutManager().onRestoreInstanceState(state);
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
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is no internet, do not restart loader even if user changes preference in settings
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            // If the preferences for order by has changed, make another query and set the flag to false.
            if (PREFERENCES_HAVE_BEEN_CHANGED) {
                getLoaderManager().restartLoader(LOADER_ID, null, this);
                mEmptyTextView.setVisibility(View.INVISIBLE);
                PREFERENCES_HAVE_BEEN_CHANGED = false;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(
                getString(R.string.view_state),
                mRecyclerView.getLayoutManager().onSaveInstanceState());
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

        Uri baseUri = Uri.parse(Constants.BASE_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        // Build uri, for example:
        // "http://api.themoviedb.org/3/movie/popular?api_key=[your api key here]"
        Uri uri = uriBuilder.appendPath(orderBy).appendQueryParameter(Constants.API_KEY_PARAM, Constants.api_key).build();
        // Initialize MoviesLoader
        return new MoviesLoader(this, uri.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Movies>> loader, List<Movies> moviesList) {
        mMoviesAdapter = new MoviesAdapter(this, new ArrayList<Movies>());
        if (moviesList != null && !(moviesList.isEmpty())) {
            mMoviesAdapter = new MoviesAdapter(this, moviesList);
            mRecyclerView.setAdapter(mMoviesAdapter);
        } else {
            mEmptyTextView.setText(R.string.no_movies);
        }
        View loadingProgress = findViewById(R.id.loading_progress);
        // Remove loading progress bar after making http request and updating ui
        loadingProgress.setVisibility(View.GONE);
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
    private static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / 180);
    }
}
