package com.example.android.popularmovies;

public class Movies {

    private String mOriginalTitle;
    private String mBackdropPath;

    public Movies(String originalTitle, String backdropPath) {
        this.mOriginalTitle = originalTitle;
        this.mBackdropPath = backdropPath;
    }

    public String getmOriginalTitle() {
        return mOriginalTitle;
    }

    public String getmBackdropPath() {
        return mBackdropPath;
    }
}
