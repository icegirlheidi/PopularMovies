package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Movies implements Parcelable {

    private String mOriginalTitle;
    private String mPosterPath;
    private String mBackdropPath;
    private String mOverview;
    private String mReleaseDate;
    private int mId;
    private List<String> mGenres;
    private double mVoteAverage;

    public Movies(String originalTitle, String posterPath) {
        this.mOriginalTitle = originalTitle;
        this.mPosterPath = posterPath;
    }

    protected Movies(Parcel in) {
        mOriginalTitle = in.readString();
        mPosterPath = in.readString();
        mBackdropPath = in.readString();
        mOverview = in.readString();
        mReleaseDate = in.readString();
        mId = in.readInt();
    }

    public static final Creator<Movies> CREATOR = new Creator<Movies>() {
        @Override
        public Movies createFromParcel(Parcel in) {
            return new Movies(in);
        }

        @Override
        public Movies[] newArray(int size) {
            return new Movies[size];
        }
    };

    public void setOriginalTitle(String originalTitle) {
        this.mOriginalTitle = originalTitle;
    }

    public void setPosterPath(String posterPath) {
        this.mPosterPath = posterPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.mBackdropPath = backdropPath;
    }

    public void setOverview(String overview) {
        this.mOverview = overview;
    }

    public void setReleaseDate(String releaseDate) {
        this.mReleaseDate = releaseDate;
    }

    public void setVoteRate(double voteAverage) {
        this.mVoteAverage = voteAverage;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public void setGenres(List<String> genres) {
        this.mGenres = genres;
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public String getBackdropPath() {
        return mBackdropPath;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public int getId() {
        return mId;
    }

    public List<String> getGenres() {
        return mGenres;
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
        parcel.writeString(mBackdropPath);
        parcel.writeString(mOverview);
        parcel.writeString(mReleaseDate);
        parcel.writeInt(mId);
    }
}
