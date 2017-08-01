package com.example.android.popularmovies;

public class Constants {

    // Replace this with your own API key
    public static final String api_key = "";

    // Base url for fetching movies data through The Movie Database API
    public static final String BASE_URL = "http://api.themoviedb.org/3/movie/";

    // Base url for fetching image data through The Movie Database API
    public static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";

    // Value of large image size
    public static final String IMAGE_SIZE_LARGE = "w185/";

    // Value of extra large image size
    public static final String IMAGE_SIZE_EXTRA_LARGE = "w342/";

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

}
