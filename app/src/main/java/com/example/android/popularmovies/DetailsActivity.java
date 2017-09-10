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
import butterknife.OnItemClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity {

    private static final String LOG_TAG = DetailsActivity.class.getName();

    // Movie's ID
    private int mMovieId;

    private String mVideoKey;

    // A list of keys get from videos
    private List<String> mVideoKeyList;

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

    @BindView(R.id.runtime_in_details)
    TextView mRuntimeInDetailsTextView;

    @BindView(R.id.overview_in_details)
    TextView mOverviewTextView;

    @BindView(R.id.poster_image_view_details)
    ImageView mPosterInDetailsImageView;

    @BindView(R.id.reviews_label)
    TextView mReviewLabelTextView;

    @BindView(R.id.videos_label)
    TextView mVideoLabelTextView;

    @BindView(R.id.divider_before_review)
    View dividerBeforeReview;

    @BindView(R.id.divider_after_review)
    View dividerAfterReview;

    @BindView(R.id.show_and_hide_reviews)
    ImageView showAndHideReviewImageView;

    @BindView(R.id.show_and_hide_videos)
    ImageView showAndHideVideoImageView;

    @BindView(R.id.language_in_details)
    TextView languageTextView;

    @BindView(R.id.divider_before_video)
    View dividerBeforeVideoView;

    @BindView(R.id.divider_after_video)
    View dividerAfterVideoView;

    @BindView(R.id.divider_last)
    View dividerLast;

    @BindView(R.id.review_list_view)
    NonScrollListView reviewListView;

    @BindView(R.id.video_list_view)
    NonScrollListView videoListView;

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

    // Click the up or down arrow to hide or show reviews
    @OnClick(R.id.show_and_hide_reviews)
    void showAndHideReviews(View view) {
        if (hasReview()) {
            reviewListView.setVisibility(View.GONE);
            dividerAfterReview.setVisibility(View.VISIBLE);
            showAndHideReviewImageView.setImageResource(R.drawable.ic_keyboard_arrow_down_24px);
            showAndHideReviewImageView.setTag(R.drawable.ic_keyboard_arrow_down_24px);
            dividerBeforeVideoView.setVisibility(View.GONE);
        } else {
            reviewListView.setVisibility(View.VISIBLE);
            dividerAfterReview.setVisibility(View.INVISIBLE);
            showAndHideReviewImageView.setImageResource(R.drawable.ic_keyboard_arrow_up_24px);
            showAndHideReviewImageView.setTag(R.drawable.ic_keyboard_arrow_up_24px);
            dividerBeforeVideoView.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.show_and_hide_videos)
    void showAndHideVideos(View view) {
        if (hasVideo()) {
            videoListView.setVisibility(View.GONE);
            dividerAfterVideoView.setVisibility(View.VISIBLE);
            showAndHideVideoImageView.setImageResource(R.drawable.ic_keyboard_arrow_down_24px);
            showAndHideVideoImageView.setTag(R.drawable.ic_keyboard_arrow_down_24px);
            dividerLast.setVisibility(View.GONE);
        } else {
            videoListView.setVisibility(View.VISIBLE);
            dividerAfterVideoView.setVisibility(View.INVISIBLE);
            showAndHideVideoImageView.setImageResource(R.drawable.ic_keyboard_arrow_up_24px);
            showAndHideVideoImageView.setTag(R.drawable.ic_keyboard_arrow_up_24px);
            dividerLast.setVisibility(View.VISIBLE);
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

    @OnItemClick(R.id.video_list_view)
    void onItemClick(int position){
        if ( mVideoKeyList != null && !mVideoKey.isEmpty() ) {
            Uri youtubeUri = Uri.parse(Constants.YOUTUBE + mVideoKeyList.get(position));
            Intent intent = new Intent(Intent.ACTION_VIEW, youtubeUri);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }

    /**
     * @Return whether this movie is added as favorite
     */
    private boolean isFavorite() {
        Integer resourceFavorite = (Integer) mAddAndRemoveFavoriteButton.getTag();
        // If the image resource of the button is a full pink heart
        // it means this movie is already added to my favorite list
        if (resourceFavorite == R.drawable.ic_favorite_48px) {
            return true;
        }
        return false;
    }

    /**
     * @Return whether reviews are shown or not
     */
    private boolean hasReview() {
        Integer resourceReview = (Integer) showAndHideReviewImageView.getTag();

        if (resourceReview != null && resourceReview == R.drawable.ic_keyboard_arrow_up_24px) {
            return true;
        }
        return false;
    }

    /**
     * @Return whether videos are shown or not
     */
    private boolean hasVideo() {
        Integer resourceVideo = (Integer) showAndHideVideoImageView.getTag();

        if (resourceVideo != null && resourceVideo == R.drawable.ic_keyboard_arrow_up_24px) {
            return true;
        }
        return false;
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
                        + Constants.IMAGE_SIZE_W342 + backdropPath;
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
                        + Constants.IMAGE_SIZE_W185 + mPosterPath;
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

                int runtime = response.body().getRuntime();
                mRuntimeInDetailsTextView.setText(String.valueOf(runtime) + " " + getString(R.string.runtime_min));

                List<Detail.Language> languagesList = response.body().getSpoken_languages();
                List<String> languageNames = new ArrayList<>();
                if (languagesList != null && !(languagesList.isEmpty())) {
                    for (Detail.Language language : languagesList) {
                        languageNames.add(language.getIso_639_1());
                    }
                }
                String languagesNameString = TextUtils.join(", ", languageNames);
                languageTextView.setText(languagesNameString);

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
                if(response.body() != null) {
                    List<Video> videosList = response.body().getResults();
                    if (videosList != null && !(videosList.isEmpty())) {

                        if (videosList.get(0).getSite().equals(DetailsActivity.this.getString(R.string.youtube))) {
                            mVideoKey = videosList.get(0).getKey();
                        }
                        mVideoKeyList = new ArrayList<>();
                        for(Video video : videosList){
                            mVideoKeyList.add(video.getKey());
                        }

                        VideoAdapter adapter = new VideoAdapter(getApplicationContext(), videosList);
                        videoListView.setAdapter(adapter);
                        videoListView.setVisibility(View.GONE);
                    } else HideVideo();
                } else HideVideo();
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
                if (response.body() != null) {
                    List<Review> reviewList = response.body().getResults();
                    if (reviewList != null && !(reviewList.isEmpty())) {
                        ReviewAdapter adapter = new ReviewAdapter(getApplicationContext(), reviewList);
                        reviewListView.setAdapter(adapter);
                        reviewListView.setVisibility(View.GONE);
                    } else HideReview();
                } else HideReview();
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

    // Hide review label, dividers and review TextView
    private void HideReview() {
        mReviewLabelTextView.setVisibility(View.GONE);
        reviewListView.setVisibility(View.GONE);
        dividerBeforeReview.setVisibility(View.GONE);
        dividerAfterReview.setVisibility(View.GONE);
        dividerBeforeVideoView.setVisibility(View.VISIBLE);
        showAndHideReviewImageView.setVisibility(View.GONE);
    }

    // Hide video label, video TextView
    private void HideVideo() {
        mVideoLabelTextView.setVisibility(View.GONE);
        videoListView.setVisibility(View.GONE);
        showAndHideVideoImageView.setVisibility(View.GONE);
    }
}
