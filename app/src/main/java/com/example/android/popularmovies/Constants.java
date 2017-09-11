package com.example.android.popularmovies;


public class Constants {

    // Replace this with your own API key
    public static final String API_KEY = BuildConfig.API_KEY;

    // Base url for fetching movies data through The Movie Database API
    //public static final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    public static final String BASE_URL = "http://api.themoviedb.org/";

    // Base url for fetching image data through The Movie Database API
    public static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";

    // Value of image with size w185
    public static final String IMAGE_SIZE_W185 = "w185/";

    // Value of image with size w342
    public static final String IMAGE_SIZE_W342 = "w342/";

    // Date separator for data such as "2017-6-29"
    public static final String DATE_SEPARATOR = "-";

    // Genres separator for data such as Action | Adventure | Fantasy
    public static final String GENRES_SEPARATOR = " | ";

    public static final String YOUTUBE = "http://www.youtube.com/watch?v=";

}
