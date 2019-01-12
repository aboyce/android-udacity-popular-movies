package uk.ab.popularmovies.asynctasks;

import java.util.List;

import uk.ab.popularmovies.entities.MovieTrailer;

public interface GetTrailersAsyncTaskExecutor {

    void onGetTrailersTaskCompletion(List<MovieTrailer> movieTrailers);
}
