package com.example.android.popularmovies;


import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.model.Review;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewAdapter extends ArrayAdapter<Review> {

    private Context mContext;

    public ReviewAdapter(@NonNull Context context, List<Review> reviews) {
        super(context, 0, reviews);
        this.mContext = context;
    }

    static class ViewHolder{
        @BindView(R.id.review_author) TextView reviewAuthor;
        @BindView(R.id.review_content) TextView reviewContent;
        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.review_list_item, parent, false);
        }

        Review review = getItem(position);
/*        ViewHolder holder;
        if (convertView != null) {
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }*/
        ViewHolder holder = new ViewHolder(convertView);
        String reviewAuthor = review.getAuthor();
        holder.reviewAuthor.setText(getContext().getString(R.string.review_by) + " " + reviewAuthor);

        String reviewContent = review.getContent();
        holder.reviewContent.setText(reviewContent);

        return convertView;
    }
}
