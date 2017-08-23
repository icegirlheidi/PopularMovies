package com.example.android.popularmovies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.LoaderManager;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
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

import com.example.android.popularmovies.model.Movie;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int LOADER_ID = 1;

    private RecyclerView mRecyclerView;

    private MoviesAdapter mMoviesAdapter;

    private static boolean PREFERENCES_HAVE_BEEN_CHANGED = false;

    MoviesBroadcastReceiver mMoviesBroadcastReceiver = new MoviesBroadcastReceiver();

    private NetworkInfo mNetworkInfo;

    @BindView(R.id.empty_text_view)
    TextView mEmptyTextView;
    @BindView(R.id.loading_progress)
    View mLoadingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
        ButterKnife.bind(this);

        // Number of columns to show movie posters
        int spanCount = numberOfColumns();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, spanCount);
        mRecyclerView.setLayoutManager(mLayoutManager);
        List<Movie> mMoviesList = new ArrayList<>();
        mMoviesAdapter = new MoviesAdapter(this, mMoviesList);
        mRecyclerView.setAdapter(mMoviesAdapter);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mNetworkInfo = connMgr.getActiveNetworkInfo();

        if (mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting()) {
            // Initialize the loader if there is internet connection
            //getLoaderManager().initLoader(LOADER_ID, null, this);
            MovieIntentService.fetchMovies(this);
        } else {
            // When there is no internet connection,
            // Remove the loading progress bar and display no internet connection
            mLoadingProgress.setVisibility(View.GONE);
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
                //getLoaderManager().restartLoader(LOADER_ID, null, this);
                MovieIntentService.fetchMovies(this);
                mEmptyTextView.setVisibility(View.INVISIBLE);
                PREFERENCES_HAVE_BEEN_CHANGED = false;
            }
        } else {
            mEmptyTextView.setVisibility(View.VISIBLE);
            mEmptyTextView.setText(getString(R.string.no_intenet));
            mRecyclerView.setAdapter(null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMoviesBroadcastReceiver != null) {
            LocalBroadcastManager.getInstance(this)
                    .registerReceiver(mMoviesBroadcastReceiver, new IntentFilter(MovieIntentService.ACTION_FETCH_MOVIES));
        }
        if (mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting()) {
            MovieIntentService.fetchMovies(this);
        } /*else {
            mLoadingProgress.setVisibility(View.GONE);
            mEmptyTextView.setVisibility(View.VISIBLE);
            mEmptyTextView.setText(R.string.no_intenet);
        }*/

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(
                getString(R.string.view_state),
                mRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMoviesBroadcastReceiver != null) {
            LocalBroadcastManager.getInstance(this)
                    .unregisterReceiver(mMoviesBroadcastReceiver);
        }
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        PREFERENCES_HAVE_BEEN_CHANGED = true;
    }

    /**
     * Adjust number of columns of posters based on screen width
     *
     * @return the number of columns
     */
    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // You can change this divider to adjust the size of the poster
        int widthDivider = 350;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
    }

    public class MoviesBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getAction() == null) {
                return;
            }
            String action = intent.getAction();
            if (action.equals(MovieIntentService.ACTION_FETCH_MOVIES) && intent.hasExtra(MovieIntentService.EXTRA_MOVIES)) {
                List<Movie> moviesList = intent.getParcelableArrayListExtra(MovieIntentService.EXTRA_MOVIES);
                if (moviesList != null && !(moviesList.isEmpty())) {
                    mMoviesAdapter = new MoviesAdapter(context, moviesList);
                    mRecyclerView.setAdapter(mMoviesAdapter);
                } else {
                    mEmptyTextView.setText(getString(R.string.no_movies));
                }

                // Remove loading progress bar after making http request and updating ui
                mLoadingProgress.setVisibility(View.GONE);

            }
        }
    }

}
