package uk.ab.popularmovies.asynctasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.List;

import uk.ab.popularmovies.entities.MovieTrailer;
import uk.ab.popularmovies.preferences.TMDbPreferences;
import uk.ab.popularmovies.utilities.MovieTrailerUtility;
import uk.ab.popularmovies.utilities.NetworkUtility;

public class GetTrailersAsyncTask extends AsyncTask<Integer, Integer, List<MovieTrailer>> {

    private final String TAG = GetTrailersAsyncTask.class.getSimpleName();

    private final WeakReference<Activity> weakActivity;
    private final GetTrailersAsyncTaskExecutor executor;

    public GetTrailersAsyncTask(Activity activity, GetTrailersAsyncTaskExecutor executor) {
        this.weakActivity = new WeakReference<>(activity);
        this.executor = executor;
    }

    @Override
    protected List<MovieTrailer> doInBackground(Integer... movieIds) {

        if (movieIds.length != 1) {
            String message = "The task has not been called with the single expected Movie Id.";
            Log.e(TAG, message);
            throw new IllegalArgumentException(message);
        }

        try {
            Integer movieId = movieIds[0];

            Log.d(TAG, "Will attempt to get the movie trailer URL for movie " + movieId + ".");
            URL movieTrailerUrl = TMDbPreferences.getMovieTrailersURL(weakActivity.get(), movieId);
            Log.d(TAG, "Retrieved the URL for the movie trailer request.");

            Log.d(TAG, "Will attempt to request the movie trailers from the URL.");
            String movieTrailerJson = NetworkUtility.getJSONFromURL(movieTrailerUrl);
            if (movieTrailerJson == null) {
                Log.e(TAG, "The movie trailer JSON has been returned as null.");
                return null;
            }
            Log.d(TAG, "The movie trailer JSON data has been returned.");

            // Now the JSON has been returned, convert this to a List of movie trailers.
            Log.d(TAG, "Will attempt to parse the movie trailer JSON into movie trailer objects.");
            List<MovieTrailer> movieTrailers = MovieTrailerUtility.getMovieTrailersFromJson(movieTrailerJson);
            if (movieTrailers == null) {
                Log.e(TAG, "The attempt to parse the movie trailer JSON returned null.");
                return null;
            }
            Log.d(TAG, "The movie trailer JSON has successfully been parsed into movie trailers.");

            // Now the movie trailers have been converted, return them to the UI thread.
            Log.d(TAG, "Will return the " + movieTrailers.size() + " movie trailers to be displayed.");
            return movieTrailers;

        } catch (IOException e) {
            e.printStackTrace();
            String message = "Could not retrieve the movie trailer JSON or parse them into objects";
            Log.e(TAG, message + ", message: " + e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<MovieTrailer> movieTrailers) {
        executor.onGetTrailersTaskCompletion(movieTrailers);
    }
}
