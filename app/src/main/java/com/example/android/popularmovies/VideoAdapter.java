package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.model.Video;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class VideoAdapter extends ArrayAdapter<Video> {

    private Context mContext;

    public VideoAdapter(@NonNull Context context, List<Video> videos) {
        super(context, 0, videos);
        this.mContext = context;
    }

    static class ViewHolder {
        @BindView(R.id.trailor_play_button)
        ImageView trailerPlayButton;
        @BindView(R.id.trailor_text_view)
        TextView trailerTextView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.video_list_item, parent, false);
        }

        Video video = getItem(position);
        VideoAdapter.ViewHolder holder;
        if (convertView != null) {
            holder = new VideoAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (VideoAdapter.ViewHolder) convertView.getTag();
        }
        holder = new VideoAdapter.ViewHolder(convertView);
        holder.trailerTextView.setText("Trailer" + " " + Integer.toString(position + 1));

        return convertView;
    }
}
