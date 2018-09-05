package com.nalovma.popularmovies.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.nalovma.popularmovies.model.Movie;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * from movie_table WHERE origin = 0 ORDER BY popularity DESC")
    LiveData<List<Movie>> getPopularMovies();

    @Query("SELECT * from movie_table WHERE origin = 1 ORDER BY vote_average DESC")
    LiveData<List<Movie>> getTopRatedMovies();

    @Query("SELECT * from movie_table WHERE origin = 2 ORDER BY title ASC")
    LiveData<List<Movie>> getFavoriteMovies();

    @Query("SELECT * from movie_table WHERE id = :id")
    Movie getMovieById(int id);

    @Query("SELECT EXISTS(SELECT * from movie_table WHERE movie_id = :movieId AND origin = 2)")
    boolean isMovieFavorite(int movieId);

    @Query("SELECT EXISTS(SELECT * from movie_table WHERE origin = :origin)")
    boolean doesOriginExist(int origin);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovies(List<Movie> movies);

    @Query("DELETE from movie_table WHERE origin = :origin")
    void deleteByOrigin(int origin);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addToFavorite(Movie movie);

    @Query("DELETE from movie_table WHERE origin = 2 AND movie_id = :movieId")
    void deleteFromFavorite(int movieId);

}
