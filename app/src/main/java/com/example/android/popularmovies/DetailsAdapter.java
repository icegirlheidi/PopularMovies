package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;


public class DetailsAdapter extends ArrayAdapter<Movies> {

    private final Context mContext;

    public DetailsAdapter(Context context, List<Movies> movies) {
        super(context, 0, movies);
        this.mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String year;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.details_item, parent, false);
        }
        Movies currentMovie = getItem(position);

        ImageView backdropImageView = (ImageView) convertView.findViewById(R.id.backdrop_image_view_details);
        String backdropPath = currentMovie.getBackdropPath();
        //create a new progress bar for each backdrop to be loaded
        ProgressBar progressBar = null;
        if (convertView != null) {
            // Get and show progress bar for loading backdrop
            progressBar = (ProgressBar) convertView.findViewById(R.id.backdropProgressBar);
            progressBar.setVisibility(View.VISIBLE);
        }
        //load the image url with a callback to a callback method/class
        Picasso.with(mContext)
                .load(backdropPath)
                .into(backdropImageView, new ImageLoadedCallback(progressBar) {
                    @Override
                    public void onSuccess() {
                        if (this.progressBar != null) {
                            // Once the backdrop is loaded successfully, hide the progress indicator
                            this.progressBar.setVisibility(View.GONE);
                        }
                    }
                });

        TextView titleTextView = (TextView) convertView.findViewById(R.id.title_in_details);
        String originalTitle = currentMovie.getOriginalTitle();
        String releaseDate = currentMovie.getReleaseDate();
        // If movies has release date
        if (releaseDate.contains(Constants.DATE_SEPARATOR)) {
            // Then separate the release data by "-"
            String[] parts = releaseDate.split(Constants.DATE_SEPARATOR);
            // Assign the first part of the array as year
            year = parts[0];
            // Set the title to be, for example: Beauty and the Beast (2017)
            titleTextView.setText(originalTitle + " " + "(" + year + ")");
        } else {
            // If the movies has no release date, then just set the original title to title
            titleTextView.setText(originalTitle);
        }

        List<String> genresList = currentMovie.getGenres();
        TextView genresTextView = (TextView) convertView.findViewById(R.id.genres_in_details);
        // Separate each item in genresList with " | ".
        // for example: Action | Adventure | Fantasy
        String genresString = TextUtils.join(Constants.GENRES_SEPARATOR, genresList);
        genresTextView.setText(genresString);

        TextView voteAverageTextView = (TextView) convertView.findViewById(R.id.vote_average_in_details);
        double voteAverage = currentMovie.getVoteAverage();
        voteAverageTextView.setText(String.valueOf(voteAverage));

        TextView overviewTextView = (TextView) convertView.findViewById(R.id.overview_in_details);
        String overview = currentMovie.getOverview();
        overviewTextView.setText(overview);

        ImageView posterImageView = (ImageView) convertView.findViewById(R.id.poster_image_view_details);
        String posterUrl = currentMovie.getPosterPath();
        // Use picasso library to load poster
        Picasso.with(mContext).load(posterUrl).into(posterImageView);

        return convertView;
    }


    private class ImageLoadedCallback implements Callback {
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
