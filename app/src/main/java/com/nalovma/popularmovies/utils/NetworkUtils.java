package com.nalovma.popularmovies.utils;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

public class NetworkUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    final static String BASE_URL = "http://api.themoviedb.org/3";
    final static String PATH_MOVIES = "movie";
    final static String API_KEY = "";
    final static String PATH_KEY = "api_key";
    //image size can be one of this: "w92", "w154", "w185", "w342", "w500", "w780", or "original"
    final static String PATH_IMAGE_WIDTH = "w185";
    // Base url for images
    final static String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";


    /**
     * Returns new URL object from the given sort type.
     * http://api.themoviedb.org/3/movie/popular?api_key=[YOUR_API_KEY]
     */
    public static URL createUrl(String sortType) {
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendEncodedPath(PATH_MOVIES)
                .appendEncodedPath(sortType)
                .appendQueryParameter(PATH_KEY, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * Returns new image url
     */
    public static URL createImageUrl(String imageCode) {
        Uri builtUri = Uri.parse(IMAGE_BASE_URL).buildUpon()
                .appendEncodedPath(PATH_IMAGE_WIDTH)
                .appendEncodedPath(imageCode)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }
}
