package com.nalovma.popularmovies.utils;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.nalovma.popularmovies.model.Movie;

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

    private final static String BASE_URL = "http://api.themoviedb.org/3";
    private final static String PATH_MOVIES = "movie";
    private final static String API_KEY = "";
    private final static String PATH_KEY = "api_key";
    //image size can be one of this: "w92", "w154", "w185", "w342", "w500", "w780", or "original"
    private final static String PATH_IMAGE_WIDTH = "w185";
    // Base url for images
    private final static String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";


    public static List<Movie> fetchMoviesData(String requestUrl) {
        URL url = createUrl(requestUrl);
        String jsonResponse = null;

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failed creating HTTP request", e);
        }

        return extractJsonResponse(jsonResponse);
    }

    /**
     * Returns new URL object from the given sort type.
     * http://api.themoviedb.org/3/movie/popular?api_key=[YOUR_API_KEY]
     */
    private static URL createUrl(String sortType) {
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
    private static String createImageUrl(String imageCode) {
        Uri builtUri = Uri.parse(IMAGE_BASE_URL).buildUpon()
                .appendEncodedPath(PATH_IMAGE_WIDTH)
                .appendEncodedPath(imageCode)
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

    private static List<Movie> extractJsonResponse(String response) {
        if (TextUtils.isEmpty(response)) {
            return null;
        }

        List<Movie> movieList = new ArrayList<>();

        try {
            JSONObject baseJsonObject = new JSONObject(response);
            JSONArray jsonArray = baseJsonObject.optJSONArray("results");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject currentMovie = jsonArray.optJSONObject(i);
                int id = currentMovie.optInt("id", 0);
                String title = currentMovie.optString("title", "no_title");
                int voteCount = currentMovie.optInt("vote_count", 0);
                double voteAverage = currentMovie.optDouble("vote_average", 0.0);
                String posterPath = currentMovie.optString("poster_path", "no_image");
                String backdropPath = currentMovie.optString("backdrop_path", "no_image");
                String plot = currentMovie.optString("overview", "no_overview");

                Movie movie = new Movie(id, title, voteCount, voteAverage, createImageUrl(posterPath), createImageUrl(backdropPath), plot);
                movieList.add(movie);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the JSON results", e);
        }

        return movieList;
    }
}
