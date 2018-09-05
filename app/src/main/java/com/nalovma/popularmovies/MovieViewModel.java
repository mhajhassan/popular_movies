package com.nalovma.popularmovies;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.nalovma.popularmovies.database.MoviesRepository;
import com.nalovma.popularmovies.model.Movie;

import java.util.List;

public class MovieViewModel extends AndroidViewModel {

    private final MoviesRepository mMoviesRepository;

    public MovieViewModel(@NonNull Application application) {
        super(application);
        mMoviesRepository = new MoviesRepository(application);
    }

    public LiveData<List<Movie>> getMostPopularMovies() {
        return mMoviesRepository.getMostPopularMovies();
    }

    public LiveData<List<Movie>> getTopRatedMovies() {
        return mMoviesRepository.getTopRatedMovies();
    }

    public LiveData<List<Movie>> getFavoriteMovies() {
        return mMoviesRepository.getFavoriteMovies();
    }

    public boolean isFavorite(int id) {
        return mMoviesRepository.isMovieFavorite(id);
    }

    public void insertMovies(List<Movie> movies) {
        mMoviesRepository.insertMovies(movies);
    }

    public void deleteByOrigin(int origin) {
        mMoviesRepository.deleteByOrigin(origin);
    }

    public boolean doesOriginExist(int origin) {
        return mMoviesRepository.doesOriginExist(origin);
    }

    public Movie getMovieById(int id) {
        return mMoviesRepository.getMovieById(id);
    }

    public void addToFavorite(Movie movie){
        mMoviesRepository.addToFavorite(movie);
    }

    public void deleteFromFavorite(Movie movie){
        mMoviesRepository.deleteFromFavorite(movie);
    }
}
