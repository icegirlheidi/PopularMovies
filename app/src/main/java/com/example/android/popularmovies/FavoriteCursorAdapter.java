package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.data.FavoriteContract.FavoriteEntry;
import com.squareup.picasso.Picasso;

public class FavoriteCursorAdapter extends CursorRecyclerViewAdapter<FavoriteCursorAdapter.ViewHolder> {

    private final Context mContext;
    private Cursor mCursor;


    public FavoriteCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        this.mContext = context;
        this.mCursor = cursor;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mOriginalTitleTextView;
        public final ImageView mPosterImageView;

        public ViewHolder(View view) {
            super(view);
            mOriginalTitleTextView = (TextView) view.findViewById(R.id.title);
            mPosterImageView = (ImageView) view.findViewById(R.id.poster_image_view);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {

        String originalTitle = cursor.getString(cursor.getColumnIndex(FavoriteEntry.COLUMN_MOVIE_ORIGINAL_TITLE));
        String posterPath = cursor.getString(cursor.getColumnIndex(FavoriteEntry.COLUMN_MOVIE_POSTER_PATH));
        final int movieId = cursor.getInt(cursor.getColumnIndex(FavoriteEntry.COLUMN_MOVIE_ID));

        String posterUrl = Constants.IMAGE_BASE_URL
                + Constants.IMAGE_SIZE_W342 + posterPath;
        Picasso.with(mContext).load(posterUrl).into(viewHolder.mPosterImageView);
        viewHolder.mOriginalTitleTextView.setText(originalTitle);

        viewHolder.mPosterImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailsActivity.class);
                intent.putExtra(mContext.getString(R.string.id), movieId);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        return super.swapCursor(newCursor);
    }
}
