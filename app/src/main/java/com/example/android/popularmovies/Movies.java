package com.example.android.popularmovies;

public class Movies {

    private String mOriginalTitle;
    private String mPosterPath;

    public Movies(String originalTitle, String posterPath) {
        this.mOriginalTitle = originalTitle;
        this.mPosterPath = posterPath;
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public String getPosterPath(){
        return mPosterPath;
    }
}
