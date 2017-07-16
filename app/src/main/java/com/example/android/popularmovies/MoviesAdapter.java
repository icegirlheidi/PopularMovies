package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    private static final String LOG_TAG = MoviesAdapter.class.getName();
    private Context mContext;
    public List<Movies> mMoviesList;

    public MoviesAdapter (Context context, List<Movies> moviesList) {
        this.mContext = context;
        this.mMoviesList = moviesList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View moviesView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(moviesView);
    }

    @Override
    public void onBindViewHolder(MoviesAdapter.ViewHolder viewHolder, int position) {
        Movies movie = mMoviesList.get(position);
        TextView titleTextView = viewHolder.titleTextView;
        ImageView imageView = viewHolder.posterImageView;
        String posterUrl = movie.getPosterPath();
        // User Picasso library to load poster into ImageView
        Picasso.with(mContext).load(posterUrl).into(imageView);
        titleTextView.setText(movie.getOriginalTitle());
    }

    @Override
    public int getItemCount() {
        return mMoviesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView posterImageView;
        public CardView myCardView;
        public TextView titleTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            posterImageView = (ImageView) itemView.findViewById(R.id.poster_image_view);
            myCardView = (CardView)itemView.findViewById(R.id.card_view);
            titleTextView = (TextView) itemView.findViewById(R.id.title);
        }
    }
}
