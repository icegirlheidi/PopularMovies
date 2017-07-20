package com.example.android.popularmovies;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movies>> {

    private static final String LOG_TAG = DetailsActivity.class.getName();

    private DetailsAdapter mDetailsAdapter;

    // Movie's ID
    private int mMovieId;

    private ListView mList;

    // Loader id used to initialized DetailsLoader
    private static final int LOADER_ID = 5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get movie's id passed through intent from MoviesAdapter
        mMovieId = getIntent().getIntExtra(getString(R.string.id), 0);

        mList = (ListView) findViewById(R.id.list);
        mDetailsAdapter = new DetailsAdapter(this, new ArrayList<Movies>());
        mList.setAdapter(mDetailsAdapter);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            // Initialize the loader if there is internet connection
            getLoaderManager().initLoader(LOADER_ID, null, this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<Movies>> onCreateLoader(int id, Bundle args) {
        Uri baseUri = Uri.parse(Constants.SINGLE_MOVIE_BASE_URL + mMovieId);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        // Build uri, for example "https://api.themoviedb.org/3/movie/315635?api_key=[your api key here]"
        uriBuilder.appendQueryParameter(Constants.API_KEY_PARAM, Constants.api_key).build();
        return new DetailsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Movies>> loader, List<Movies> moviesList) {
        mDetailsAdapter = new DetailsAdapter(this, new ArrayList<Movies>());
        if (moviesList != null && !(moviesList.isEmpty())) {
            mDetailsAdapter = new DetailsAdapter(this, moviesList);
            mList.setAdapter(mDetailsAdapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movies>> loader) {
        mDetailsAdapter = new DetailsAdapter(this, new ArrayList<Movies>());
    }
}
