package com.example.android.popularmovies.services;


import com.example.android.popularmovies.model.ListResponse;
import com.example.android.popularmovies.model.Movie;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MovieService {

    // "http://api.themoviedb.org/3/movie/popular?api_key=[your api key here]"
    // "http://api.themoviedb.org/3/movie/top_rated?api_key=[your api key here]"
    @GET("3/movie/{sortBy}")
    Call<ListResponse<Movie>> getMovies(@Path("sortBy") String sortBy);


}



