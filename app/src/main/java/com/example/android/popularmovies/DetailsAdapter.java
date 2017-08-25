package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.model.Detail;
import com.example.android.popularmovies.model.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailsAdapter extends ArrayAdapter<Detail> {

    private final Context mContext;

    public DetailsAdapter(Context context, List<Detail> details) {
        super(context, 0, details);
        this.mContext = context;
    }

    static class ViewHolder {
        @BindView(R.id.backdrop_image_view_details) ImageView backdropImageView;
        @BindView(R.id.title_in_details) TextView titleTextView;
        @BindView(R.id.genres_in_details) TextView genresTextView;
        @BindView(R.id.vote_average_in_details) TextView voteAverageTextView;
        @BindView(R.id.overview_in_details) TextView overviewTextView;
        @BindView(R.id.poster_image_view_details) ImageView posterImageView;

            public ViewHolder(View view) {
                ButterKnife.bind(this, view);
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String year;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.details_item, parent, false);
        }
        Detail currentDetail = getItem(position);

        String backdropPathUrl = Constants.IMAGE_BASE_URL
                + Constants.IMAGE_SIZE_EXTRA_LARGE + currentDetail.getBackdrop_path();
        //create a new progress bar for each backdrop to be loaded
        ProgressBar progressBar = null;

        ViewHolder holder;
        if (convertView != null) {
            // Get and show progress bar for loading backdrop
            progressBar = (ProgressBar) convertView.findViewById(R.id.backdropProgressBar);
            progressBar.setVisibility(View.VISIBLE);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //load the image url with a callback to a callback method/class
        Picasso.with(mContext)
                .load(backdropPathUrl)
                .into(holder.backdropImageView, new ImageLoadedCallback(progressBar) {
                    @Override
                    public void onSuccess() {
                        if (this.progressBar != null) {
                            // Once the backdrop is loaded successfully, hide the progress indicator
                            this.progressBar.setVisibility(View.GONE);
                        }
                    }
                });

        String originalTitle = currentDetail.getOriginal_title();
        holder.titleTextView.setText(originalTitle);
        String releaseDate = currentDetail.getRelease_date();
        // If movies has release date
        if (releaseDate.contains(Constants.DATE_SEPARATOR)) {
            // Then separate the release data by "-"
            String[] parts = releaseDate.split(Constants.DATE_SEPARATOR);
            // Assign the first part of the array as year
            year = parts[0];
            // Set the title to be, for example: Beauty and the Beast (2017)
            holder.titleTextView.setText(originalTitle + " " + "(" + year + ")");
        } else {
            // If the movies has no release date, then just set the original title to title
            holder.titleTextView.setText(originalTitle);
        }

        List<Detail.Genre> genresList = currentDetail.getGenres();
        List<String> genreNames = new ArrayList<>();
        for (Detail.Genre genre : genresList) {
            // Get each genre name and add them into genreNames list
            genreNames.add(genre.getName());
        }
        // Separate each item in genresNames with " | ", for example: Action | Adventure | Fantasy
        String genresNameString = TextUtils.join(Constants.GENRES_SEPARATOR, genreNames);
        holder.genresTextView.setText(genresNameString);


        double voteAverage = currentDetail.getVote_average();
        holder.voteAverageTextView.setText(String.valueOf(voteAverage));

        String overview = currentDetail.getOverview();
        holder.overviewTextView.setText(overview);

        String posterUrl = Constants.IMAGE_BASE_URL
                + Constants.IMAGE_SIZE_EXTRA_LARGE + currentDetail.getPoster_path();
        // Use picasso library to load poster
        Picasso.with(mContext).load(posterUrl).into(holder.posterImageView);

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
