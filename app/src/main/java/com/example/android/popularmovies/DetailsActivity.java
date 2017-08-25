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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.model.Detail;
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

public class DetailsActivity extends AppCompatActivity {

    private DetailsAdapter mDetailsAdapter;

    // Movie's ID
    private int mMovieId;

    // Using ButterKnife to bind Views and ids
    @BindView(R.id.list) ListView mListView;
    @BindView(R.id.empty_text_view_details) TextView mEmptyTextView;
    @BindView(R.id.loading_progress_details) ProgressBar mLoadingProgress;

    // MovieService to fetch details in background using Retrofit
    private MovieService mMovieService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get movie's id passed through intent from MoviesAdapter
        mMovieId = getIntent().getIntExtra(getString(R.string.id), 0);

        mDetailsAdapter = new DetailsAdapter(this, new ArrayList<Detail>());
        mListView.setAdapter(mDetailsAdapter);

        mMovieService = MovieClient.createService(MovieService.class);

        if (isOnline()) {
            fetchDetails();
            //
        } else {
            // When there is no internet connection,
            // Remove the loading progress bar and display no internet connection
            mLoadingProgress.setVisibility(View.GONE);
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

    /**
     *
     * @return whether internet connection is available
     */
    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    /**
     * Fetch details of a single movie using Retrofit
     */
    public void fetchDetails() {
        Call<Detail> detailsCall = mMovieService.getDetails(String.valueOf(mMovieId));

        detailsCall.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                String backdropPath = response.body().getBackdrop_path();
                List<Detail.Genre> genres = response.body().getGenres();
                String originalTitle = response.body().getOriginal_title();
                String overView = response.body().getOverview();
                String posterPath = response.body().getPoster_path();
                String releaseDate = response.body().getRelease_date();
                double voteAverage = response.body().getVote_average();
                Detail detail = new Detail(originalTitle, posterPath);
                detail.setBackdrop_path(backdropPath);
                detail.setGenres(genres);
                detail.setOverview(overView);
                detail.setRelease_date(releaseDate);
                detail.setVote_average(voteAverage);

                List<Detail> detailList = new ArrayList<Detail>();
                detailList.add(0, detail);
                if(detailList != null && !(detailList.isEmpty())) {
                    mDetailsAdapter = new DetailsAdapter(DetailsActivity.this, detailList);
                    mListView.setAdapter(mDetailsAdapter);
                    mLoadingProgress.setVisibility(View.GONE);
                    mEmptyTextView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                Toast.makeText(DetailsActivity.this, getString(R.string.no_movies), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
