package com.example.android.popularmovies;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.FavoriteContract.FavoriteEntry;
import com.example.android.popularmovies.model.Detail;
import com.example.android.popularmovies.model.Review;
import com.example.android.popularmovies.model.ReviewList;
import com.example.android.popularmovies.model.Video;
import com.example.android.popularmovies.model.VideoList;
import com.example.android.popularmovies.services.MovieClient;
import com.example.android.popularmovies.services.MovieService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity {

    private static final String LOG_TAG = DetailsActivity.class.getName();

    private ReviewAdapter mReviewAdapter;

    // Movie's ID
    private int mMovieId;

    private String mVideoKey;

    @BindView(R.id.empty_text_view_details)
    TextView mEmptyTextView;

    @BindView(R.id.loading_progress_details)
    ProgressBar mLoadingProgress;

    @BindView(R.id.backdrop_image_view_details)
    ImageView mBackdropImageView;

    @BindView(R.id.backdropProgressBar)
    ProgressBar mBackdropProgressBar;

    @BindView(R.id.play_video_button)
    ImageView mPlayVideoButton;

    @BindView(R.id.add_and_remove_favorite_button)
    ImageView mAddAndRemoveFavoriteButton;

    @BindView(R.id.title_in_details)
    TextView mTitleInDetailsTextView;

    @BindView(R.id.genres_in_details)
    TextView mGenresInDetailsTextView;

    @BindView(R.id.vote_average_in_details)
    TextView mVoteAverageInDetailsTextView;

    @BindView(R.id.overview_in_details)
    TextView mOverviewTextView;

    @BindView(R.id.poster_image_view_details)
    ImageView mPosterInDetailsImageView;

    @BindView(R.id.reviews_label)
    TextView mReviewLabelTextView;

    @BindView(R.id.reviewList)
    ListView mReviewListView;

    // MovieService to fetch details in background using Retrofit
    private MovieService mMovieService;

    private String mPosterPath;

    private String mOriginalTitle;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get movie's id passed through intent from MoviesAdapter
        mMovieId = getIntent().getIntExtra(getString(R.string.id), 0);

        mReviewAdapter = new ReviewAdapter(DetailsActivity.this, new ArrayList<Review>());
        mReviewListView.setAdapter(mReviewAdapter);

        mMovieService = MovieClient.createService(MovieService.class);

        if (isOnline()) {
            fetchDetails();
            fetchReviews();
            fetchVideos();
        } else {
            // When there is no internet connection,
            // Remove the loading progress bar and display no internet connection
            mLoadingProgress.setVisibility(View.GONE);
            mEmptyTextView.setVisibility(View.VISIBLE);
            mEmptyTextView.setText(R.string.no_intenet);
        }

    }

    @OnClick(R.id.play_video_button)
    void playVideo(View view) {
        if (view.getId() == R.id.play_video_button) {
            Uri youtubeUri = Uri.parse(Constants.YOUTUBE + mVideoKey);
            Intent intent = new Intent(Intent.ACTION_VIEW, youtubeUri);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }

    // Click the add and remove favorite button to add and remove favorite movie
    @OnClick(R.id.add_and_remove_favorite_button)
    void addAndRemoveFavorite(View view) {
        if (isFavorite()) {
            // If the clicked movie is already added to my favorite list
            // then clicking it will delete it from my favorite list
            String selection = FavoriteEntry.COLUMN_MOVIE_ID + "=?";
            String[] selectionArgs = new String[]{String.valueOf(mMovieId)};
            int rowsAffected = getContentResolver().delete(FavoriteEntry.CONTENT_URI, selection, selectionArgs);
            // If this movie is removed from my favorite list successfully
            if (rowsAffected > 0) {
                Toast.makeText(
                        this,
                        mOriginalTitle + " " + getString(R.string.movie_removed_from_favorite),
                        Toast.LENGTH_LONG).show();
                mAddAndRemoveFavoriteButton.setImageResource(R.drawable.ic_favorite_border_48px);
                mAddAndRemoveFavoriteButton.setTag(R.drawable.ic_favorite_border_48px);
            }
        } else {
            // If the clicked movie is not in my favorite list
            // then clicking it will add it to my favorite list
            ContentValues contentValues = new ContentValues();
            contentValues.put(FavoriteEntry.COLUMN_MOVIE_ID, mMovieId);
            contentValues.put(FavoriteEntry.COLUMN_MOVIE_POSTER_PATH, mPosterPath);
            contentValues.put(FavoriteEntry.COLUMN_MOVIE_ORIGINAL_TITLE, mOriginalTitle);
            Uri createdRow = getContentResolver().insert(FavoriteEntry.CONTENT_URI, contentValues);
            if (createdRow != null) {
                // If the clicked movie is added successfully into my favorite list
                // then change the add and remove favorite button to a full pink heart
                mAddAndRemoveFavoriteButton.setImageResource(R.drawable.ic_favorite_48px);
                mAddAndRemoveFavoriteButton.setTag(R.drawable.ic_favorite_48px);
                Toast.makeText(
                        this,
                        mOriginalTitle + " " + getString(R.string.movie_added_to_favorite_successfully),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    // Check whether this movie is added as favorite
    private boolean isFavorite() {
        Integer resource = (Integer) mAddAndRemoveFavoriteButton.getTag();
        // If the image resource of the button is a full pink heart
        // it means this movie is already added to my favorite list
        if (resource == R.drawable.ic_favorite_48px) {
            return true;
        }
        return false;
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
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {

                String backdropPath = response.body().getBackdrop_path();
                String backdropPathUrl = Constants.IMAGE_BASE_URL
                        + Constants.IMAGE_SIZE_EXTRA_LARGE + backdropPath;
                //load the image url with a callback to a callback method/class
                Picasso.with(DetailsActivity.this)
                        .load(backdropPathUrl)
                        .into(mBackdropImageView, new ImageLoadedCallback(mLoadingProgress) {
                            @Override
                            public void onSuccess() {
                                if (this.progressBar != null) {
                                    // Once the backdrop is loaded successfully, hide the progress indicator
                                    this.progressBar.setVisibility(View.GONE);
                                }
                            }
                        });

                List<Detail.Genre> genresList = response.body().getGenres();
                if (genresList != null && !(genresList.isEmpty())) {
                    List<String> genreNames = new ArrayList<>();
                    for (Detail.Genre genre : genresList) {
                        // Get each genre name and add them into genreNames list
                        genreNames.add(genre.getName());
                    }
                    // Separate each item in genresNames with " | ", for example: Action | Adventure | Fantasy
                    String genresNameString = TextUtils.join(Constants.GENRES_SEPARATOR, genreNames);
                    mGenresInDetailsTextView.setText(genresNameString);
                }

                String overView = response.body().getOverview();
                mOverviewTextView.setText(overView);

                mPosterPath = response.body().getPoster_path();
                String posterUrl = Constants.IMAGE_BASE_URL
                        + Constants.IMAGE_SIZE_EXTRA_LARGE + mPosterPath;
                // Use picasso library to load poster
                Picasso.with(DetailsActivity.this).load(posterUrl).into(mPosterInDetailsImageView);

                mOriginalTitle = response.body().getOriginal_title();
                String releaseDate = response.body().getRelease_date();
                // If movies has release date
                if (releaseDate.contains(Constants.DATE_SEPARATOR)) {
                    // Then separate the release data by "-"
                    String[] parts = releaseDate.split(Constants.DATE_SEPARATOR);
                    // Assign the first part of the array as year
                    String year = parts[0];
                    // Set the title to be, for example: Beauty and the Beast (2017)
                    mTitleInDetailsTextView.setText(mOriginalTitle + " " + "(" + year + ")");
                } else {
                    // If the movies has no release date, then just set the original title to title
                    mTitleInDetailsTextView.setText(mOriginalTitle);
                }

                double voteAverage = response.body().getVote_average();
                mVoteAverageInDetailsTextView.setText(String.valueOf(voteAverage));

                // query sqlitedatabase to check whether this movie is added to my favorite list
                String selection = FavoriteEntry.COLUMN_MOVIE_ID + "=?";
                String[] selectionArgs = new String[]{String.valueOf(mMovieId)};
                Cursor cursor = getContentResolver().query(
                        FavoriteEntry.CONTENT_URI,
                        null,
                        selection,
                        selectionArgs,
                        null,
                        null);
                if (cursor.getCount() > 0) {
                    // if this movie is in my favorite list
                    // then set the image resource of add and remove favorite button to be a full pink heart
                    mAddAndRemoveFavoriteButton.setImageResource(R.drawable.ic_favorite_48px);
                    mAddAndRemoveFavoriteButton.setTag(R.drawable.ic_favorite_48px);
                } else {
                    // otherwise set a white border heart to be its image resource
                    mAddAndRemoveFavoriteButton.setImageResource(R.drawable.ic_favorite_border_48px);
                    mAddAndRemoveFavoriteButton.setTag(R.drawable.ic_favorite_border_48px);
                }

            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                Toast.makeText(DetailsActivity.this, getString(R.string.no_movies), Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Fetch videos of a single movie using Retrofit
     */
    public void fetchVideos() {
        Call<VideoList<Video>> videosCall = mMovieService.getVideos(String.valueOf(mMovieId));

        videosCall.enqueue(new Callback<VideoList<Video>>() {
            @Override
            public void onResponse(Call<VideoList<Video>> call, Response<VideoList<Video>> response) {
                List<Video> videosList = response.body().getResults();
                if (videosList.get(0).getSite().equals(DetailsActivity.this.getString(R.string.youtube))) {
                    mVideoKey = videosList.get(0).getKey();
                }
            }

            @Override
            public void onFailure(Call<VideoList<Video>> call, Throwable t) {
                Log.i(LOG_TAG, "Failure fetching videos");
            }
        });
    }

    public void fetchReviews() {
        Call<ReviewList<Review>> reviewsCall = mMovieService.getReviews(String.valueOf(mMovieId));
        reviewsCall.enqueue(new Callback<ReviewList<Review>>() {
            @Override
            public void onResponse(Call<ReviewList<Review>> call, Response<ReviewList<Review>> response) {
                List<Review> reviewList = response.body().getResults();
                if (reviewList != null && !(reviewList.isEmpty())) {
                    Log.i("TEST", "review list size: " + reviewList.size());
                    mReviewAdapter.clear();
                    mReviewAdapter = new ReviewAdapter(DetailsActivity.this, reviewList);
                    mReviewListView.setAdapter(mReviewAdapter);
                    mReviewLabelTextView.setVisibility(View.VISIBLE);

/*                    for (Review review : reviewList) {
                       mReviewAdapter.add(review);
                    }*/

                } else {
                    mReviewLabelTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ReviewList<Review>> call, Throwable t) {
                Log.i(LOG_TAG, "fetchReviews failure");
            }
        });
    }


    private class ImageLoadedCallback implements com.squareup.picasso.Callback {
        ProgressBar progressBar;

        public ImageLoadedCallback(ProgressBar progBar) {
            progressBar = progBar;
        }

        @Override
        public void onSuccess() {
        }

        @Override
        public void onError() {
        }
    }
}
