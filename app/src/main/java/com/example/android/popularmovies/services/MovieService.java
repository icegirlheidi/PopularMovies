package com.example.android.popularmovies.services;

import com.example.android.popularmovies.model.Detail;
import com.example.android.popularmovies.model.ListResponse;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.Review;
import com.example.android.popularmovies.model.ReviewList;
import com.example.android.popularmovies.model.Video;
import com.example.android.popularmovies.model.VideoList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MovieService {

    // "http://api.themoviedb.org/3/movie/popular?api_key=[your api key here]"
    // "http://api.themoviedb.org/3/movie/top_rated?api_key=[your api key here]"
    @GET("3/movie/{sortBy}")
    Call<ListResponse<Movie>> getMovies(@Path("sortBy") String sortBy);

    // "https://api.themoviedb.org/3/movie/315635?api_key=[your api key here]"
    @GET("3/movie/{id}")
    Call<Detail> getDetails(@Path("id") String id);

    // "https://api.themoviedb.org/3/movie/283366/videos?api_key=[your api key here]"
    @GET("3/movie/{id}/videos")
    Call<VideoList<Video>> getVideos(@Path("id") String id);

    // "https://api.themoviedb.org/3/movie/283366/reviews?api_key=[your api key here]"
    @GET("3/movie/{id}/reviews")
    Call<ReviewList<Review>> getReviews(@Path("id") String id);

}



