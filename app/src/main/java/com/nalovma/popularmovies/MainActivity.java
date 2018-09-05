package com.nalovma.popularmovies;

import android.app.ActivityOptions;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nalovma.popularmovies.adapter.MovieAdapter;
import com.nalovma.popularmovies.model.Movie;
import com.nalovma.popularmovies.utils.AppExecutors;
import com.nalovma.popularmovies.utils.Constants;
import com.nalovma.popularmovies.utils.NetworkUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ItemClickListener {

    private MovieAdapter mAdapter;
    private RecyclerView mMoviesList;
    private TextView mEmptyStateTV;
    private ProgressBar mLoadingProgress;

    private MovieViewModel mMovieViewModel;
    private Observer<List<Movie>> mObserver;

    private int mMenuSelection = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        GridLayoutManager layoutManager;
        int deviceOrientation = getResources().getConfiguration().orientation;
        if (deviceOrientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new GridLayoutManager(this, 2);
        } else {
            layoutManager = new GridLayoutManager(this, 4);
        }

        mMoviesList.setLayoutManager(layoutManager);
        mMoviesList.setHasFixedSize(true);
        mAdapter = new MovieAdapter(this);
        mMoviesList.setAdapter(mAdapter);

        mMovieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);

        mObserver = mAdapter::setMovieData;


        if (savedInstanceState != null && savedInstanceState.containsKey(Constants.MENU_SELECTION_INSTANCE_KEY)) {
            mMenuSelection = savedInstanceState.getInt(Constants.MENU_SELECTION_INSTANCE_KEY);
        } else {
            setTitle(R.string.popular_movies);
            loadMovies(Constants.POPULAR_MOVIE, Constants.ORIGIN_MOST_POPULAR);
        }

        checkDatabaseAndSetViews(mMenuSelection);

        // Set the observer for the LiveData depending on the selected menu item.
        switch (mMenuSelection) {
            case Constants.ORIGIN_MOST_POPULAR:
                mMovieViewModel.getMostPopularMovies().observe(this, mObserver);
                break;
            case Constants.ORIGIN_TOP_RATED:
                mMovieViewModel.getTopRatedMovies().observe(this, mObserver);
                break;
            case Constants.ORIGIN_FAVORITES:
                mMovieViewModel.getFavoriteMovies().observe(this, mObserver);
                break;
        }

    }

    private void checkDatabaseAndSetViews(int mMenuSelection) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            boolean originExists = mMovieViewModel.doesOriginExist(mMenuSelection);
            runOnUiThread(() -> {
                if (originExists) {
                    showContent();
                } else if (mMenuSelection == Constants.ORIGIN_MOST_POPULAR
                        || mMenuSelection == Constants.ORIGIN_TOP_RATED) {
                    showErrorMessage(getString(R.string.no_movies_or_internet));
                } else if (mMenuSelection == Constants.ORIGIN_FAVORITES) {
                    showErrorMessage(getString(R.string.no_favorites));
                }
            });
        });
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
        intent.putExtra(DetailsActivity.EXTRA_MOVIE_ID, movie.getId());
        //intent.putExtra(getString(R.string.extra_movie_id), movie);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this, view.findViewById(R.id.iv_poster), getString(R.string.transition_poster)).toBundle());
    }

    private void showContent() {
        // stop loading
        mLoadingProgress.setVisibility(View.INVISIBLE);

        // hide error TextView
        mEmptyStateTV.setVisibility(View.INVISIBLE);

        // show the movies list
        mMoviesList.setVisibility(View.VISIBLE);


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
                removeObservers();
                mMenuSelection = Constants.ORIGIN_MOST_POPULAR;
                setTitle(R.string.popular_movies);
                mMovieViewModel.getMostPopularMovies().observe(this, mObserver);
                mMoviesList.smoothScrollToPosition(0);

                if (checkConnectivity()) {
                    loadMovies(Constants.POPULAR_MOVIE, Constants.ORIGIN_MOST_POPULAR);
                } else {
                    checkDatabaseAndSetViews(mMenuSelection);
                }
                return true;
            case R.id.top_rated_movie_action:
                removeObservers();
                mMenuSelection = Constants.ORIGIN_TOP_RATED;
                setTitle(R.string.top_rated_movie);
                mMovieViewModel.getTopRatedMovies().observe(this, mObserver);
                mMoviesList.smoothScrollToPosition(0);

                if (checkConnectivity()) {
                    loadMovies(Constants.TOP_RATED_MOVIE, Constants.ORIGIN_TOP_RATED);
                } else {
                    checkDatabaseAndSetViews(mMenuSelection);
                }
                return true;
            case R.id.favorite_action:
                removeObservers();
                mMenuSelection = Constants.ORIGIN_FAVORITES;
                setTitle(R.string.favorites);
                mMovieViewModel.getFavoriteMovies().observe(this, mObserver);
                mMoviesList.smoothScrollToPosition(0);
                checkDatabaseAndSetViews(mMenuSelection);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadMovies(String type, int origin) {
        if (checkConnectivity()) {
            new CreateMovieList(origin).execute(type);
        } else {
            showErrorMessage(getString(R.string.no_internet));
        }
    }


    private void removeObservers() {
        mMovieViewModel.getMostPopularMovies().removeObserver(mObserver);
        mMovieViewModel.getTopRatedMovies().removeObserver(mObserver);
        mMovieViewModel.getFavoriteMovies().removeObserver(mObserver);
    }

    private void deleteMoviesByOrigin(int origin) {
        AppExecutors.getInstance().diskIO().execute(() -> mMovieViewModel.deleteByOrigin(origin));
    }

    // task to load movies on the background
    private class CreateMovieList extends AsyncTask<String, Void, List<Movie>> {

        private int origin;

        public CreateMovieList(int origin) {
            this.origin = origin;
        }

        @Override
        protected List<Movie> doInBackground(String... urls) {
            if (urls[0] == null || urls[0].length() < 1) {
                return null;
            }
            // create the connection and fetch the data from the api
            return NetworkUtils.fetchMoviesData(urls[0], origin);
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            if (movies != null && !movies.isEmpty()) {
                deleteMoviesByOrigin(origin);
                AppExecutors.getInstance().diskIO().execute(() -> mMovieViewModel.insertMovies(movies));
                showContent();
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
}
