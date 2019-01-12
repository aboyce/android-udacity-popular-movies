package uk.ab.popularmovies.asynctasks;

import java.util.List;

import uk.ab.popularmovies.entities.MovieReview;

public interface GetReviewsAsyncTaskExecutor {

    void onGetReviewsTaskCompletion(List<MovieReview> movieReviews);
}
