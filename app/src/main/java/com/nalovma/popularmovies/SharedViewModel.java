package com.nalovma.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.nalovma.popularmovies.model.Movie;
import com.nalovma.popularmovies.model.MovieReview;
import com.nalovma.popularmovies.model.MovieVideo;

import java.util.List;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Movie> selectedMovie = new MutableLiveData<>();
    private final MutableLiveData<List<MovieVideo>> movieVideoData = new MutableLiveData<>();
    private final MutableLiveData<List<MovieReview>> movieReviewData = new MutableLiveData<>();

    public void setSelectedMovie(Movie movie) {
        selectedMovie.setValue(movie);
    }

    public LiveData<Movie> getSelectedMovie() {
        return selectedMovie;
    }

    public MutableLiveData<List<MovieVideo>> getMovieVideoData() {
        return movieVideoData;
    }

    public MutableLiveData<List<MovieReview>> getMovieReviewData() {
        return movieReviewData;
    }

    public void setMovieVideoData(List<MovieVideo> movieVideoList) {
        movieVideoData.setValue(movieVideoList);
    }

    public void setMovieReviewData(List<MovieReview> movieReviewList) {
        movieReviewData.setValue(movieReviewList);
    }
}
