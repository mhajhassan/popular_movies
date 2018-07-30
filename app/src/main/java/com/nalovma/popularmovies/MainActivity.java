package com.nalovma.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.nalovma.popularmovies.adapter.MovieAdapter;
import com.nalovma.popularmovies.model.Movie;
import com.nalovma.popularmovies.utils.NetworkUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    final static String POPULAR_MOVIE = "popular";
    final static String TOP_RATED_MOVIE = "top_rated";
    private MovieAdapter mAdapter;
    private RecyclerView mMoviesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        new createMovieList().execute(POPULAR_MOVIE);
    }

    private void initViews() {
        mMoviesList = findViewById(R.id.rv_movies);
    }

    class createMovieList extends AsyncTask<String, Void, List<Movie>> {

        @Override
        protected List<Movie> doInBackground(String... urls) {
            if (urls[0] == null || urls[0].length() < 1) {
                return null;
            }
            // create the connection and fetch the data from the api
            return NetworkUtils.fetchMoviesData(urls[0]);
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            if (movies != null && !movies.isEmpty()) {
                showContent(movies);
            } else {
                //showErrorMessage("No books found");
            }
        }
    }

    private void showContent(List<Movie> movies) {
        mAdapter = new MovieAdapter(movies);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mMoviesList.setLayoutManager(gridLayoutManager);
        mMoviesList.setHasFixedSize(true);
        mMoviesList.setAdapter(mAdapter);
    }

    private void showErrorMessage(String message) {

    }

    // this method checks the connectivity and return true when it's connected and false when it's not connected
    private boolean checkConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

}
