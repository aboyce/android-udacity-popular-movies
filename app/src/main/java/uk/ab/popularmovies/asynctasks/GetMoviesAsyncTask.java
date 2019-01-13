package uk.ab.popularmovies.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.List;

import uk.ab.popularmovies.entities.Movie;
import uk.ab.popularmovies.entities.enums.MovieSort;
import uk.ab.popularmovies.preferences.TMDbPreferences;
import uk.ab.popularmovies.utilities.MovieUtility;
import uk.ab.popularmovies.utilities.NetworkUtility;

public class GetMoviesAsyncTask extends AsyncTask<Void, Integer, List<Movie>> {

    private final String TAG = GetMoviesAsyncTask.class.getSimpleName();

    private final WeakReference<Context> weakContext;
    private final GetMoviesAsyncTaskExecutor executor;
    private final MovieSort movieSort;

    public GetMoviesAsyncTask(Context context, GetMoviesAsyncTaskExecutor executor, MovieSort movieSort) {
        this.weakContext = new WeakReference<Context>(context);
        this.executor = executor;
        this.movieSort = movieSort;
    }

    @Override
    protected List<Movie> doInBackground(Void... voids) {

        try {
            // Get the generated request URL.
            Log.d(TAG, "Will attempt to get the movie request URL.");
            URL moviesRequestUrl;
            if (movieSort.equals(MovieSort.POPULARITY)) {
                moviesRequestUrl = TMDbPreferences.getMoviePopularURL(weakContext.get());
            } else if (movieSort.equals(MovieSort.RATED)) {
                moviesRequestUrl = TMDbPreferences.getMovieRatedURL(weakContext.get());
            } else {
                moviesRequestUrl = TMDbPreferences.getDiscoverURL(weakContext.get(), movieSort);
            }
            publishProgress(10);
            Log.d(TAG, "Retrieved the URL for the movie request.");

            // Use the generated URL to go and retrieve the movie JSON data.
            Log.d(TAG, "Will attempt to request the Movies from the URL.");
            String moviesJson = NetworkUtility.getJSONFromURL(moviesRequestUrl);
            publishProgress(20);
            if (moviesJson == null) {
                Log.e(TAG, "The movie JSON has been returned as null.");
                return null;
            }
            Log.d(TAG, "The movie JSON data has been returned.");

            // Now the JSON has been returned, convert this to a List of Movies.
            Log.d(TAG, "Will attempt to parse the movie JSON into Movie objects.");
            List<Movie> movies = MovieUtility.getMoviesFromDiscoverJson(moviesJson);
            publishProgress(70);
            if (movies == null) {
                Log.e(TAG, "The attempt to parse the movie JSON returned null.");
                return null;
            }
            Log.d(TAG, "The movie JSON has successfully been parsed into movies.");
            publishProgress(80);

            // Now the Movies have been converted, return them to the UI thread.
            Log.d(TAG, "Will return the " + movies.size() + " movies to be displayed.");
            publishProgress(100);
            return movies;

        } catch (IOException e) {
            e.printStackTrace();
            String message = "Could not retrieve the movie JSON or parse them into objects";
            Log.e(TAG, message + ", message: " + e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        executor.onGetMoviesTaskCompletion(movieSort, movies);
    }
}
