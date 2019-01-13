package uk.ab.popularmovies.asynctasks;

import java.util.List;

import uk.ab.popularmovies.entities.Movie;
import uk.ab.popularmovies.entities.enums.MovieSort;

public interface GetMoviesAsyncTaskExecutor {

    void onGetMoviesTaskCompletion(MovieSort movieSort, List<Movie> movies);
}
