package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    private final Context mContext;
    public List<Movies> mMoviesList;

    public MoviesAdapter(Context context, List<Movies> moviesList) {
        this.mContext = context;
        this.mMoviesList = moviesList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View moviesView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(moviesView);
    }

    @Override
    public void onBindViewHolder(MoviesAdapter.ViewHolder viewHolder, final int position) {
        final Movies movie = mMoviesList.get(position);
        TextView titleTextView = viewHolder.titleTextView;
        ImageView imageView = viewHolder.posterImageView;

        String posterUrl = movie.getPosterPath();
        // User Picasso library to load poster into ImageView
        Picasso.with(mContext).load(posterUrl).into(imageView);
        titleTextView.setText(movie.getOriginalTitle());

        final int id = movie.getId();
        // Click movie poster to open details of movie
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailsActivity.class);
                intent.putExtra(mContext.getString(R.string.id), id);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMoviesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView posterImageView;
        public final TextView titleTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            posterImageView = (ImageView) itemView.findViewById(R.id.poster_image_view);
            titleTextView = (TextView) itemView.findViewById(R.id.title);
        }
    }

    public void swapData(List<Movies> movies) {
        mMoviesList = movies;
        if (movies != null) {
            notifyDataSetChanged();
        }
    }

}
