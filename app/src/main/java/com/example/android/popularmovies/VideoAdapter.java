package com.example.android.popularmovies;

import android.content.Context;
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


class VideoAdapter extends ArrayAdapter<Video> {

    public VideoAdapter(@NonNull Context context, List<Video> videos) {
        super(context, 0, videos);
        Context mContext = context;
    }

    static class ViewHolder {
        @BindView(R.id.trailer_play_button)
        ImageView trailerPlayButton;
        @BindView(R.id.trailer_text_view)
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

        VideoAdapter.ViewHolder holder;
        if (convertView != null) {
            holder = new VideoAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder = new VideoAdapter.ViewHolder(convertView);
        holder.trailerTextView.setText("Trailer" + " " + Integer.toString(position + 1));

        return convertView;
    }
}
