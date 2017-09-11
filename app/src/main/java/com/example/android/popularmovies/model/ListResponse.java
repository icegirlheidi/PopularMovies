package com.example.android.popularmovies.model;

import java.util.List;

public class ListResponse<T> {
    /**
     * Current page number
     */
    private int page;

    /**
     * A list of results
     */
    private List<T> results;

    /**
     * Number of total results
     */
    private int total_results;

    /**
     * Number of total pages
     */
    private int total_pages;

    public List<T> getResults() {
        return results;
    }

}