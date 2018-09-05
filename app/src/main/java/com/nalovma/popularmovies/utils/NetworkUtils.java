package com.nalovma.popularmovies.utils;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.nalovma.popularmovies.model.Movie;
import com.nalovma.popularmovies.model.MovieReview;
import com.nalovma.popularmovies.model.MovieVideo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class NetworkUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();


    public static List<Movie> fetchMoviesData(String requestUrl, int origin) {
        URL url = createUrl(requestUrl);
        String jsonResponse = null;

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failed creating HTTP request", e);
        }
        return extractJsonResponse(jsonResponse, origin);
    }

    public static List<MovieVideo> fetchMovieVideos(String movieId) {
        URL url = createMovieContentUrl(movieId, Constants.VIDEOS);
        String jsonResponse = null;

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failed creating HTTP request", e);
        }

        return extractMovieVideosJsonResponse(jsonResponse);
    }

    public static List<MovieReview> fetchMovieReviews(String movieId) {
        URL url = createMovieContentUrl(movieId, Constants.REVIEWS);
        String jsonResponse = null;

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failed creating HTTP request", e);
        }

        return extractMovieReviewsJsonResponse(jsonResponse);
    }

    /**
     * Returns new URL object from the given sort type.
     * http://api.themoviedb.org/3/movie/popular?api_key=[YOUR_API_KEY]
     */
    private static URL createUrl(String sortType) {
        Uri builtUri = Uri.parse(Constants.BASE_URL).buildUpon()
                .appendEncodedPath(Constants.PATH_MOVIES)
                .appendEncodedPath(sortType)
                .appendQueryParameter(Constants.PATH_KEY, Constants.API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    private static URL createMovieContentUrl(String movieId, String contentType) {
        Uri builtUri = Uri.parse(Constants.BASE_URL).buildUpon()
                .appendEncodedPath(Constants.PATH_MOVIES)
                .appendEncodedPath(movieId)
                .appendEncodedPath(contentType)
                .appendQueryParameter(Constants.PATH_KEY, Constants.API_KEY)
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
    private static String createImageUrl(String imageCode) {
        Uri builtUri = Uri.parse(Constants.IMAGE_BASE_URL).buildUpon()
                .appendEncodedPath(Constants.PATH_IMAGE_WIDTH)
                .appendEncodedPath(imageCode)
                .build();
        return builtUri.toString();
    }


    public static String createYoutubeImageUrl(String key) {
        Uri builtUri = Uri.parse(Constants.YOUTUBE_IMAGE_BASE_URL).buildUpon()
                .appendEncodedPath(key)
                .appendEncodedPath(Constants.YOUTUBE_IMAGE_FILE_EXT)
                .build();
        return builtUri.toString();
    }

    /*
     *
     * make http connection to server and get the response
     *
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error code:" + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (inputStream != null) {
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    /*
     *
     * this method will return all the lines from the response
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();

        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();

            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }

        return output.toString();
    }

    /*
     * parse the json response
     */


    private static List<Movie> extractJsonResponse(String response, int origin) {
        if (TextUtils.isEmpty(response)) {
            return null;
        }
        List<Movie> movieList = new ArrayList<>();

        try {
            JSONObject baseJsonObject = new JSONObject(response);
            JSONArray jsonArray = baseJsonObject.optJSONArray(Constants.RESULTS);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject currentMovie = jsonArray.optJSONObject(i);
                int movieId = currentMovie.optInt(Constants.MOVIE_ID, 0);
                String title = currentMovie.optString(Constants.TITLE, Constants.NO_DATA);
                int voteCount = currentMovie.optInt(Constants.VOTE_COUNT, 0);
                double voteAverage = currentMovie.optDouble(Constants.VOTE_AVERAGE, 0.0);
                double popularity = currentMovie.optDouble(Constants.POPULARITY, 0.0);
                String posterPath = currentMovie.optString(Constants.POSTER_PATH, Constants.NO_DATA);
                String backdropPath = currentMovie.optString(Constants.BACKDROP_PATH, Constants.NO_DATA);
                String overview = currentMovie.optString(Constants.OVERVIEW, Constants.NO_DATA);
                String releaseDate = currentMovie.optString(Constants.RELEASE_DATE, Constants.NO_DATA);

                Movie movie = new Movie(movieId, title, voteCount, voteAverage, popularity, createImageUrl(posterPath), createImageUrl(backdropPath), overview, releaseDate, origin);
                movieList.add(movie);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the JSON results", e);
        }
        return movieList;
    }

    private static List<MovieVideo> extractMovieVideosJsonResponse(String response) {
        if (TextUtils.isEmpty(response)) {
            return null;
        }
        List<MovieVideo> movieVideoList = new ArrayList<>();
        try {
            JSONObject baseJsonObject = new JSONObject(response);
            JSONArray jsonArray = baseJsonObject.optJSONArray(Constants.RESULTS);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject currentData = jsonArray.optJSONObject(i);
                String id = currentData.optString(Constants.ID, Constants.NO_DATA);
                String key = currentData.optString(Constants.KEY, Constants.NO_DATA);
                String name = currentData.optString(Constants.NAME, Constants.NO_DATA);
                String site = currentData.optString(Constants.SITE, Constants.NO_DATA);
                String type = currentData.optString(Constants.TYPE, Constants.NO_DATA);
                int size = currentData.optInt(Constants.SIZE, 0);

                MovieVideo movieVideo = new MovieVideo(id, key, name, site, type, size);
                movieVideoList.add(movieVideo);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the JSON results", e);
        }
        return movieVideoList;
    }

    private static List<MovieReview> extractMovieReviewsJsonResponse(String response) {
        if (TextUtils.isEmpty(response)) {
            return null;
        }
        List<MovieReview> movieReviewList = new ArrayList<>();
        try {
            JSONObject baseJsonObject = new JSONObject(response);
            JSONArray jsonArray = baseJsonObject.optJSONArray(Constants.RESULTS);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject currentData = jsonArray.optJSONObject(i);
                String author = currentData.optString(Constants.AUTHOR, Constants.NO_DATA);
                String content = currentData.optString(Constants.CONTENT, Constants.NO_DATA);

                MovieReview movieReview = new MovieReview(author, content);
                movieReviewList.add(movieReview);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the JSON results", e);
        }
        return movieReviewList;
    }
}
