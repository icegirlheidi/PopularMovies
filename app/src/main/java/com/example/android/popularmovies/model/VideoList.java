package com.example.android.popularmovies.model;


import java.util.List;

public class VideoList<T>{

    private int id;

    private List<Video> results;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Video> getResults() {
        return results;
    }

}

