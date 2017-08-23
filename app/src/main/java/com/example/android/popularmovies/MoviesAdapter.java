package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    private final Context mContext;
    public List<Movie> mMoviesList;

    public MoviesAdapter(Context context, List<Movie> moviesList) {
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
        final Movie movie = mMoviesList.get(position);
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
        @BindView(R.id.poster_image_view) ImageView posterImageView;
        @BindView(R.id.title) TextView titleTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

    public void swapData(List<Movie> movies) {
        mMoviesList = movies;
        if (movies != null) {
            notifyDataSetChanged();
        }
    }

}
