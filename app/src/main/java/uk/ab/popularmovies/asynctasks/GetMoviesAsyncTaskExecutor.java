package uk.ab.popularmovies.asynctasks;

import java.util.List;

import uk.ab.popularmovies.entities.Movie;

public interface GetMoviesAsyncTaskExecutor {

    void onGetMoviesTaskStart();

    void onGetMoviesTaskProgressUpdate(int progress);

    void onGetMoviesTaskCompletion(List<Movie> movies);
}
