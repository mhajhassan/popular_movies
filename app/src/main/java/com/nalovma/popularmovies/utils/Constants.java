package com.nalovma.popularmovies.utils;

public interface Constants {

    // base url for api
    String BASE_URL = "http://api.themoviedb.org/3";
    // Base url for images
    String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";
    String YOUTUBE_APP_URL = "vnd.youtube:";
    String YOUTUBE_IMAGE_BASE_URL = "https://img.youtube.com/vi";
    String YOUTUBE_IMAGE_FILE_EXT = "0.jpg";
    String PATH_MOVIES = "movie";
    String API_KEY = "";
    String PATH_KEY = "api_key";
    String PATH_IMAGE_WIDTH = "w185";

    // String Constants for parsing the Json String
    String RESULTS = "results";
    String MOVIE_ID = "id";
    String TITLE = "title";
    String VOTE_COUNT = "vote_count";
    String VOTE_AVERAGE = "vote_average";
    String POPULARITY = "popularity";
    String RELEASE_DATE = "release_date";
    String OVERVIEW = "overview";
    String POSTER_PATH = "poster_path";
    String BACKDROP_PATH = "backdrop_path";
    // no data string
    String NO_DATA = "No Data";

    String POPULAR_MOVIE = "popular";
    String TOP_RATED_MOVIE = "top_rated";

    int ORIGIN_MOST_POPULAR = 0;
    int ORIGIN_TOP_RATED = 1;
    int ORIGIN_FAVORITES = 2;

    String DATABASE_NAME = "popular_movies";
    String MENU_SELECTION_INSTANCE_KEY = "selection";

    String VIDEOS = "videos";
    String REVIEWS = "reviews";

    String ID = "id";
    String KEY = "key";
    String NAME = "name";
    String SITE = "site";
    String TYPE = "type";
    String SIZE = "size";
    String AUTHOR = "author";
    String CONTENT = "content";
}
