package com.nalovma.popularmovies;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nalovma.popularmovies.adapter.MovieAdapter;
import com.nalovma.popularmovies.model.Movie;
import com.nalovma.popularmovies.utils.NetworkUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ItemClickListener {

    final static String POPULAR_MOVIE = "popular";
    final static String TOP_RATED_MOVIE = "top_rated";
    private MovieAdapter mAdapter;
    private RecyclerView mMoviesList;
    private TextView mEmptyStateTV;
    private ProgressBar mLoadingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        if (checkConnectivity()) {
            new createMovieList().execute(POPULAR_MOVIE);
            setTitle(R.string.popular_movies);
        } else {
            showErrorMessage(getString(R.string.no_internet));
        }
    }

    // initializing the views
    private void initViews() {
        mMoviesList = findViewById(R.id.rv_movies);
        mEmptyStateTV = findViewById(R.id.empty_message);
        mLoadingProgress = findViewById(R.id.loadingProgressBar);
    }

    /*
     *
     * method to open the movie details activity when the user clicks on one movie poster
     *
     * */
    @Override
    public void onItemClick(View view, int position) {
        Movie movie = mAdapter.getItem(position);
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(getString(R.string.extra_movie_id), movie);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this, view.findViewById(R.id.iv_poster), getString(R.string.transition_poster)).toBundle());
    }

    // task to load movies on the background
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
                showErrorMessage(getString(R.string.no_movies));
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // show loading
            mLoadingProgress.setVisibility(View.VISIBLE);
        }
    }

    private void showContent(List<Movie> movies) {
        // stop loading
        mLoadingProgress.setVisibility(View.INVISIBLE);

        // hide error TextView
        mEmptyStateTV.setVisibility(View.INVISIBLE);

        // show the movies list
        mMoviesList.setVisibility(View.VISIBLE);

        mAdapter = new MovieAdapter(movies);
        mAdapter.setItemClickListener(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mMoviesList.setLayoutManager(gridLayoutManager);
        mMoviesList.setHasFixedSize(true);
        mMoviesList.setAdapter(mAdapter);
    }

    private void showErrorMessage(String message) {
        // stop loading
        mLoadingProgress.setVisibility(View.INVISIBLE);

        // hide the movies list items
        mMoviesList.setVisibility(View.INVISIBLE);

        // show the error message
        mEmptyStateTV.setVisibility(View.VISIBLE);
        mEmptyStateTV.setText(message);
    }

    // this method checks the connectivity and return true when it's connected and false when it's not connected
    private boolean checkConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.popular_movies_action:
                if (checkConnectivity()) {
                    new createMovieList().execute(POPULAR_MOVIE);
                    setTitle(R.string.popular_movies);
                } else {
                    showErrorMessage(getString(R.string.no_internet));
                }

                return true;
            case R.id.top_rated_movie_action:
                if (checkConnectivity()) {
                    new createMovieList().execute(TOP_RATED_MOVIE);
                    setTitle(R.string.top_rated_movie);
                } else {
                    showErrorMessage(getString(R.string.no_internet));
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
