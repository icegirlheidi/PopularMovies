package com.example.android.popularmovies.model;


import java.util.List;

public class ReviewList<T> {

    private int id;

    private int page;

    private List<T> results;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<T> getResults() {
        return results;
    }

}
