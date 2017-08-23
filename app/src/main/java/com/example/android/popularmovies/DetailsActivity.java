package com.example.android.popularmovies;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.popularmovies.model.Movie;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movie>> {

    private DetailsAdapter mDetailsAdapter;

    // Movie's ID
    private int mMovieId;

    @BindView(R.id.list) ListView mListView;
    @BindView(R.id.empty_text_view_details) TextView mEmptyTextView;

    // Loader id used to initialized DetailsLoader
    private static final int LOADER_ID = 5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get movie's id passed through intent from MoviesAdapter
        mMovieId = getIntent().getIntExtra(getString(R.string.id), 0);

        mDetailsAdapter = new DetailsAdapter(this, new ArrayList<Movie>());
        mListView.setAdapter(mDetailsAdapter);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            // Initialize the loader if there is internet connection
            getLoaderManager().initLoader(LOADER_ID, null, this);
        } else {
            // When there is no internet connection,
            // Remove the loading progress bar and display no internet connection
            View loadingProgress = findViewById(R.id.loading_progress_details);
            loadingProgress.setVisibility(View.GONE);
            mEmptyTextView.setVisibility(View.VISIBLE);
            mEmptyTextView.setText(R.string.no_intenet);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        Uri baseUri = Uri.parse(Constants.BASE_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        // Build uri, for example "https://api.themoviedb.org/3/movie/315635?api_key=[your api key here]"
        uriBuilder.appendPath(String.valueOf(mMovieId)).appendQueryParameter(Constants.API_KEY_PARAM, Constants.API_KEY).build();
        return new DetailsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> moviesList) {
        mDetailsAdapter = new DetailsAdapter(this, new ArrayList<Movie>());
        if (moviesList != null && !(moviesList.isEmpty())) {
            mDetailsAdapter = new DetailsAdapter(this, moviesList);
            mListView.setAdapter(mDetailsAdapter);
        } else {
            mEmptyTextView.setText(R.string.no_details);
        }
        View loadingProgress = findViewById(R.id.loading_progress_details);
        // Remove loading progress bar after making http request and updating ui
        loadingProgress.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        mDetailsAdapter = new DetailsAdapter(this, new ArrayList<Movie>());
    }
}
