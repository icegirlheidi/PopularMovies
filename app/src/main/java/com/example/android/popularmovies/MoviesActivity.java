package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.app.LoaderManager;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MoviesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movies>> {

    private static final String LOG_TAG = MoviesActivity.class.getName();

    private static final int LOADER_ID = 1;

    private RecyclerView mRecyclerView;

    private MoviesAdapter mMoviesAdapter;

    // Number of columns used in GridLayoutManager
//    private final int SPAN_COUNT = 2;

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
        return new MoviesLoader(this);
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

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }
}
