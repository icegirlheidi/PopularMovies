package com.example.android.popularmovies;


public class Constants {

    // Replace this with your own API key
    public static final String API_KEY = BuildConfig.API_KEY;

    // Base url for fetching movies data through The Movie Database API
    //public static final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    public static final String BASE_URL = "http://api.themoviedb.org/";


    // Base url for fetching image data through The Movie Database API
    public static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";

    // Value of image with size w154
    public static final String IMAGE_SIZE_W154 = "w154/";

    // Value of image with size w185
    public static final String IMAGE_SIZE_W185 = "w185/";

    // Value of image with size w342
    public static final String IMAGE_SIZE_W342 = "w342/";


    // Value of image with size w500
    public static final String IMAGE_SIZE_W500 = "w500/";

    // Query parameter api_key
    public static final String API_KEY_PARAM = "api_key";

    // Title of results from json file
    public static final String JSON_PARSE_RESULTS = "results";

    // Title of original title from json file
    public static final String JSON_PARSE_ORIGINAL_TITLE = "original_title";

    // Title of poster path from json file
    public static final String JSON_PARSE_POSTER_PATH = "poster_path";

    // Title of backdrop path from json file
    public static final String JSON_PARSE_BACKDROP_PATH = "backdrop_path";

    // Title of overview from json file
    public static final String JSON_PARSE_OVERVIEW_PATH = "overview";

    // Title of release date from json file
    public static final String JSON_PARSE_RELEASE_DATE_PATH = "release_date";

    // Title of id from json file
    public static final String JSON_PARSE_ID = "id";

    // Title of genres from json file
    public static final String JSON_PARSE_GENRES = "genres";

    // Title of name from json file
    public static final String JSON_PARSE_NAME = "name";

    // Title of vote average from json file
    public static final String JSON_PARSE_VOTE_AVERAGE = "vote_average";

    // Date separator for data such as "2017-6-29"
    public static final String DATE_SEPARATOR = "-";

    // Genres separator for data such as Action | Adventure | Fantasy
    public static final String GENRES_SEPARATOR = " | ";

    public static final String YOUTUBE = "http://www.youtube.com/watch?v=";

}
