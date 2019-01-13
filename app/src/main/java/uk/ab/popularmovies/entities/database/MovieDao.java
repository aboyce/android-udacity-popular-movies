package uk.ab.popularmovies.entities.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import uk.ab.popularmovies.entities.Movie;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movie")
    List<Movie> getAllMovies();

    @Query("SELECT * FROM movie")
    LiveData<List<Movie>> getAllMoviesLive();

    @Query("SELECT * FROM movie WHERE id = :movieId")
    LiveData<Movie> getMovieFromId(Integer movieId);

    @Insert
    void insertMovie(Movie movie);

    @Delete
    void deleteMovie(Movie movie);
}
