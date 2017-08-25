package com.example.android.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Movie implements Parcelable {

    @SerializedName("original_title")
    private String mOriginalTitle;

    @SerializedName("poster_path")
    private String mPosterPath;

    @SerializedName("id")
    private int mId;

    @SerializedName("vote_average")
    private double mVoteAverage;

    public Movie(String originalTitle, String posterPath) {
        this.mOriginalTitle = originalTitle;
        this.mPosterPath = posterPath;
    }

    protected Movie(Parcel in) {
        mOriginalTitle = in.readString();
        mPosterPath = in.readString();
        mId = in.readInt();
        mVoteAverage = in.readDouble();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };


    public void setVoteRate(double voteAverage) {
        this.mVoteAverage = voteAverage;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public int getId() {
        return mId;
    }

    public double getVoteAverage() {
        return mVoteAverage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(mOriginalTitle);
        parcel.writeString(mPosterPath);
        parcel.writeInt(mId);
        parcel.writeDouble(mVoteAverage);
    }

}
