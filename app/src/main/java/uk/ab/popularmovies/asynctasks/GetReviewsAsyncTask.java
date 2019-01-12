package uk.ab.popularmovies.asynctasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.List;

import uk.ab.popularmovies.entities.MovieReview;
import uk.ab.popularmovies.preferences.TMDbPreferences;
import uk.ab.popularmovies.utilities.MovieReviewUtility;
import uk.ab.popularmovies.utilities.NetworkUtility;

public class GetReviewsAsyncTask extends AsyncTask<Integer, Integer, List<MovieReview>> {

    private final String TAG = GetReviewsAsyncTask.class.getSimpleName();

    private final WeakReference<Activity> weakActivity;
    private final GetReviewsAsyncTaskExecutor executor;

    public GetReviewsAsyncTask(Activity activity, GetReviewsAsyncTaskExecutor executor) {
        this.weakActivity = new WeakReference<>(activity);
        this.executor = executor;
    }

    @Override
    protected List<MovieReview> doInBackground(Integer... movieIds) {

        if (movieIds.length != 1) {
            String message = "The task has not been called with the single expected Movie Id.";
            Log.e(TAG, message);
            throw new IllegalArgumentException(message);
        }

        try {
            Integer movieId = movieIds[0];

            Log.d(TAG, "Will attempt to get the movie reviews URL for movie " + movieId + ".");
            URL movieReviewsUrl = TMDbPreferences.getMovieReviewsURL(weakActivity.get(), movieId);
            Log.d(TAG, "Retrieved the URL for the movie reviews request.");

            Log.d(TAG, "Will attempt to request the movie reviews from the URL.");
            String movieReviewsJson = NetworkUtility.getJSONFromURL(movieReviewsUrl);
            if (movieReviewsJson == null) {
                Log.e(TAG, "The movie reviews JSON has been returned as null.");
                return null;
            }
            Log.d(TAG, "The movie reviews JSON data has been returned.");

            // Now the JSON has been returned, convert this to a List of movie reviews.
            Log.d(TAG, "Will attempt to parse the movie reviews JSON into movie review objects.");
            List<MovieReview> movieReviews = MovieReviewUtility.getMovieReviewsFromJson(movieReviewsJson);
            if (movieReviews == null) {
                Log.e(TAG, "The attempt to parse the movie reviews JSON returned null.");
                return null;
            }
            Log.d(TAG, "The movie reviews JSON has successfully been parsed into movie reviews.");

            // Now the movie reviews have been converted, return them to the UI thread.
            Log.d(TAG, "Will return the " + movieReviews.size() + " movie reviews to be displayed.");
            return movieReviews;

        } catch (IOException e) {
            e.printStackTrace();
            String message = "Could not retrieve the movie reviews JSON or parse them into objects";
            Log.e(TAG, message + ", message: " + e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<MovieReview> movieReviews) {
        executor.onGetReviewsTaskCompletion(movieReviews);
    }
}