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

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public int getTotal_results() {
        return total_results;
    }

    public void setTotal_results(int total_results) {
        this.total_results = total_results;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }
}