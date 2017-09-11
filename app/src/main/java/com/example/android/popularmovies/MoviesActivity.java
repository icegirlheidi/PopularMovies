package com.example.android.popularmovies;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.FavoriteContract.FavoriteEntry;
import com.example.android.popularmovies.model.ListResponse;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.services.MovieClient;
import com.example.android.popularmovies.services.MovieService;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MoviesActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private RecyclerView mRecyclerView;

    private MoviesAdapter mMoviesAdapter;

    private MovieService mMovieService;

    private static boolean PREFERENCES_HAVE_BEEN_CHANGED = false;

    private FavoriteCursorAdapter mFavoriteCursorAdapter;

    // Boolean value of whether there is movie deleted from my favorite done in details
    private static boolean MOVIE_IS_DELETED_FROM_FAVORITE = false;

//    private static SharedPreferences sharedPreferences;


    @BindView(R.id.empty_text_view)
    TextView mEmptyTextView;

    @BindView(R.id.loading_progress)
    View mLoadingProgress;

    @BindView(R.id.recycler_view)
    RecyclerView movieRecyclerView;

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

        mMovieService = MovieClient.createService(MovieService.class);

        if (isOnline()) {
            fetchMovies();
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
//        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        // If there is no internet, do not restart loader even if user changes preference in settings
        if (isOnline()) {
            // If the preferences for order by has changed, make another query and set the flag to false.
            if (PREFERENCES_HAVE_BEEN_CHANGED) {
                fetchMovies();
                PREFERENCES_HAVE_BEEN_CHANGED = false;
            }
        } else {
            mEmptyTextView.setVisibility(View.VISIBLE);
            mEmptyTextView.setText(getString(R.string.no_intenet));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String orderBy = sharedPreferences.getString(
                getString(R.string.pref_order_by_key),
                getString(R.string.pref_order_by_popularity_value));
        // Get the boolean value of MOVIE_IS_DELETED_FROM_FAVORITE
        // the default value is false
        MOVIE_IS_DELETED_FROM_FAVORITE = sharedPreferences.getBoolean(
                getString(R.string.pref_movie_deleted_from_favorite), false);

        // When coming back from DetailsActivity to MoviesActivity, fetch favorites if there is
        // movie deleted from my favorite in DetailsActivity
        // Otherwise return back to the position that is previously scrolled to.
        if (isOnline() && orderBy.equals(getString(R.string.pref_order_by_my_favorite_value)) && MOVIE_IS_DELETED_FROM_FAVORITE == true) {
            fetchFavorites();
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
    protected void onPause() {
        super.onPause();
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
     * @return whether internet connection is available
     */
    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            // When there is no internet connection,
            // Remove the loading progress bar and display no internet connection
            mLoadingProgress.setVisibility(View.GONE);
            mEmptyTextView.setVisibility(View.VISIBLE);
            mEmptyTextView.setText(R.string.no_intenet);
            return false;
        }
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

    /**
     * Fetch a list of movies using Retrofit open resource
     */
    public void fetchMovies() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String orderBy = sharedPreferences.getString(getString(R.string.pref_order_by_key), getString(R.string.pref_order_by_popularity_value));
        movieRecyclerView.setVisibility(View.VISIBLE);

        if (!(orderBy.equals(getString(R.string.pref_order_by_my_favorite_value)))) {
            // Use Retrofit to fetch movies if user selects order by popularity / top rated
            Call<ListResponse<Movie>> moviesCall = mMovieService.getMovies(orderBy);

            moviesCall.enqueue(new Callback<ListResponse<Movie>>() {
                @Override
                public void onResponse(Call<ListResponse<Movie>> call, Response<ListResponse<Movie>> response) {
                    List<Movie> movieList = response.body().getResults();
                    if (movieList != null && !(movieList.isEmpty())) {
                        mMoviesAdapter.swapData(movieList);
                        mRecyclerView.setAdapter(mMoviesAdapter);
                        mLoadingProgress.setVisibility(View.GONE);
                        mEmptyTextView.setVisibility(View.INVISIBLE);
                    } else {
                        mEmptyTextView.setVisibility(View.VISIBLE);
                        mEmptyTextView.setText(getString(R.string.no_movies));
                    }
                }

                @Override
                public void onFailure(Call<ListResponse<Movie>> call, Throwable t) {
                    Toast.makeText(MoviesActivity.this, getString(R.string.no_movies), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Fetch favorite movies when user selects order by my favorite
            fetchFavorites();
        }
    }

    public void fetchFavorites() {
        // Select columns _ID, poster path, original title and movie id.
        String[] projection = {
                FavoriteEntry._ID,
                FavoriteEntry.COLUMN_MOVIE_POSTER_PATH,
                FavoriteEntry.COLUMN_MOVIE_ORIGINAL_TITLE,
                FavoriteEntry.COLUMN_MOVIE_ID
        };
        Cursor cursor = getContentResolver().query(FavoriteEntry.CONTENT_URI, projection, null, null, null);
        if (cursor.getCount() > 0) {
            mFavoriteCursorAdapter = new FavoriteCursorAdapter(this, cursor);
            mRecyclerView.setAdapter(mFavoriteCursorAdapter);
            mLoadingProgress.setVisibility(View.GONE);
            mEmptyTextView.setVisibility(View.INVISIBLE);
        } else {
            mEmptyTextView.setVisibility(View.VISIBLE);
            mEmptyTextView.setText(getString(R.string.no_favorite_movies));
            mLoadingProgress.setVisibility(View.GONE);
            movieRecyclerView.setVisibility(View.INVISIBLE);
        }

        // Set deletedFromFavorite back to false every time after fetching favorites
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putBoolean(getString(R.string.pref_movie_deleted_from_favorite), false).commit();
    }
}
