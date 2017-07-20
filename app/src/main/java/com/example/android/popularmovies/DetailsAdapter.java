package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class DetailsAdapter extends ArrayAdapter<Movies> {

    private static final String LOG_TAG = DetailsAdapter.class.getName();

    private Context mContext;

    private List<Movies> mMovies;

    public DetailsAdapter(Context context, List<Movies> movies) {
        super(context, 0, movies);
        this.mContext = context;
        this.mMovies = movies;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String year = "";

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.details_item, parent, false);
        }
        Movies currentMovie = getItem(position);

        ImageView backdropImageView = (ImageView) convertView.findViewById(R.id.backdrop_image_view_details);
        String backdropPath = currentMovie.getBackdropPath();
        Picasso.with(mContext).load(backdropPath).into(backdropImageView);

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

        TextView overviewTextView = (TextView) convertView.findViewById(R.id.overview_in_details);
        String overview = currentMovie.getOverview();
        overviewTextView.setText(overview);

        ImageView posterImageView = (ImageView) convertView.findViewById(R.id.poster_image_view_details);
        String posterUrl = currentMovie.getPosterPath();
        // Use picasso library to load poster
        Picasso.with(mContext).load(posterUrl).into(posterImageView);

        return convertView;
    }
}
