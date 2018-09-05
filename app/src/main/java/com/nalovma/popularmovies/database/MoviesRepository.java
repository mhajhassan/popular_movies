package com.nalovma.popularmovies.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.nalovma.popularmovies.model.Movie;

import java.util.List;

public class MoviesRepository {

    private final MovieDao mMovieDao;
    private final LiveData<List<Movie>> mMostPopularMovies;
    private final LiveData<List<Movie>> mTopRatedMovies;
    private final LiveData<List<Movie>> mFavoriteMovies;

    public MoviesRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        this.mMovieDao = db.movieDao();
        this.mMostPopularMovies = mMovieDao.getPopularMovies();
        this.mTopRatedMovies = mMovieDao.getTopRatedMovies();
        this.mFavoriteMovies = mMovieDao.getFavoriteMovies();
    }

    public LiveData<List<Movie>> getMostPopularMovies() {
        return mMostPopularMovies;
    }

    public LiveData<List<Movie>> getTopRatedMovies() {
        return mTopRatedMovies;
    }

    public LiveData<List<Movie>> getFavoriteMovies() {
        return mFavoriteMovies;
    }

    public Movie getMovieById(int id) {
        return mMovieDao.getMovieById(id);
    }

    public boolean isMovieFavorite(int movieId) {
        return mMovieDao.isMovieFavorite(movieId);
    }

    public boolean doesOriginExist(int origin) {
        return mMovieDao.doesOriginExist(origin);
    }

    public void deleteByOrigin(int origin) {
        mMovieDao.deleteByOrigin(origin);
    }

    public void insertMovies(List<Movie> movies) {
        mMovieDao.insertMovies(movies);
    }

    public void addToFavorite(Movie movie){
        mMovieDao.addToFavorite(movie);
    }

    public void deleteFromFavorite(Movie movie){
        mMovieDao.deleteFromFavorite(movie.getMovie_id());
    }
}
