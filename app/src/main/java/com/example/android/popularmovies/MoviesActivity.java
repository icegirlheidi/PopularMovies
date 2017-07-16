package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.app.LoaderManager;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

public class MoviesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movies>> {

    private static final String LOG_TAG = MoviesActivity.class.getName();

    private static final int LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

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
    public void onLoadFinished(Loader<List<Movies>> loader, List<Movies> data) {
        Movies movie0 = data.get(0);
        Log.i(LOG_TAG, movie0.getmOriginalTitle() + "_" + movie0.getmBackdropPath());
    }

    @Override
    public void onLoaderReset(Loader<List<Movies>> loader) {

    }
}
