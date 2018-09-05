package com.nalovma.popularmovies;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.nalovma.popularmovies.databinding.ActivityDetailsBinding;
import com.nalovma.popularmovies.model.Movie;
import com.nalovma.popularmovies.model.MovieReview;
import com.nalovma.popularmovies.model.MovieVideo;
import com.nalovma.popularmovies.utils.AppExecutors;
import com.nalovma.popularmovies.utils.Constants;
import com.nalovma.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class DetailsActivity extends AppCompatActivity {

    // Extra for the Movie ID to be received in the intent
    public static final String EXTRA_MOVIE_ID = "extraMovieId";
    private MovieViewModel mMovieViewModel;
    private boolean isFavorite;
    private SharedViewModel sharedViewModel;

    private ActivityDetailsBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_details);

        mMovieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_MOVIE_ID)) {
            int movieId = getIntent().getIntExtra(EXTRA_MOVIE_ID, -1);

            AppExecutors.getInstance().diskIO().execute(() -> {
                Movie movie = mMovieViewModel.getMovieById(movieId);

                isFavorite = mMovieViewModel.isFavorite(movie.getMovie_id());
                runOnUiThread(() -> {
                    sharedViewModel = ViewModelProviders.of(this).get(SharedViewModel.class);
                    sharedViewModel.setSelectedMovie(movie);
                    populateUI(movie);
                });
            });
        }

        DetailsPageAdapter detailsPageAdapter = new DetailsPageAdapter(this, getSupportFragmentManager());
        mBinding.viewPager.setAdapter(detailsPageAdapter);
        mBinding.tabLayout.setupWithViewPager(mBinding.viewPager);

    }


    /**
     * Populates the UI with the data from the Movie object.
     */
    void populateUI(final Movie movie) {

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            Picasso.get()
                    .load(movie.getBackdropPath())
                    .placeholder(R.drawable.movie_image)
                    .error(R.drawable.movie_image)
                    .into(mBinding.ivBackdrop);
        }

        mBinding.tvTitle.setText(movie.getTitle());
        mBinding.tvReleaseDate.setText(movie.getReleaseDate());
        mBinding.tvVoteCount.setText(String.format(Locale.US, "(%03d)", movie.getVoteCount()));
        mBinding.rbVoteAverage.setRating((float) movie.getVoteAverage() / 2f);
        Picasso.get()
                .load(movie.getPosterPath())
                .placeholder(R.drawable.movie_image)
                .error(R.drawable.movie_image)
                .into(mBinding.ivPoster);


        if (isFavorite) {
            mBinding.favouriteToggleBtn.setChecked(true);
        }

        mBinding.favouriteToggleBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                movie.setOrigin(Constants.ORIGIN_FAVORITES);
                movie.setId(0);
                AppExecutors.getInstance().diskIO().execute(() -> mMovieViewModel.addToFavorite(movie));

            } else {
                AppExecutors.getInstance().diskIO().execute(() -> mMovieViewModel.deleteFromFavorite(movie));
            }
        });

        new CreateMovieVideosTask(String.valueOf(movie.getMovie_id())).execute();
        new CreateMovieReviewsTask(String.valueOf(movie.getMovie_id())).execute();
    }

    private class CreateMovieVideosTask extends AsyncTask<Void, Void, List<MovieVideo>> {

        private String movieId;

        public CreateMovieVideosTask(String movieId) {
            this.movieId = movieId;
        }

        @Override
        protected List<MovieVideo> doInBackground(Void... voids) {
            return NetworkUtils.fetchMovieVideos(movieId);
        }

        @Override
        protected void onPostExecute(List<MovieVideo> movieVideos) {
            if (movieVideos != null && !movieVideos.isEmpty()) {
                sharedViewModel.setMovieVideoData(movieVideos);
            }
        }
    }

    private class CreateMovieReviewsTask extends AsyncTask<Void, Void, List<MovieReview>> {

        private String movieId;

        public CreateMovieReviewsTask(String movieId) {
            this.movieId = movieId;
        }

        @Override
        protected List<MovieReview> doInBackground(Void... voids) {
            return NetworkUtils.fetchMovieReviews(movieId);
        }

        @Override
        protected void onPostExecute(List<MovieReview> movieReviews) {
            if (movieReviews != null && !movieReviews.isEmpty()) {
                sharedViewModel.setMovieReviewData(movieReviews);
            }
        }
    }
}
