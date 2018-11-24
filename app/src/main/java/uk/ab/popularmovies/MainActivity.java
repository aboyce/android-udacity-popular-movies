package uk.ab.popularmovies;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.List;

import uk.ab.popularmovies.entities.Movie;
import uk.ab.popularmovies.preferences.TMDbPreferences;
import uk.ab.popularmovies.utilities.MovieUtility;
import uk.ab.popularmovies.utilities.NetworkUtility;
import uk.ab.popularmovies.view.MovieAdapter;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mMoviesRecyclerView;
    private MovieAdapter mMovieAdapter;

    private TextView mErrorMessageTextView;
    private ProgressBar mMoviesLoadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Locate and assign the error/progress view items.
        mErrorMessageTextView = findViewById(R.id.tv_main_error);
        mMoviesLoadingProgressBar = findViewById(R.id.pb_loading_movies);

        // Locate and assign the RecyclerView to display the movies.
        mMoviesRecyclerView = findViewById(R.id.rview_movies);
        // Optimisation due to the API only returning a set number at this stage.
        mMoviesRecyclerView.setHasFixedSize(true);
        // Create a new LayoutManager for the movies.
        LinearLayoutManager layoutManager = new GridLayoutManager(this, 2);
        // Create a new MovieAdapter for the movies.
        mMovieAdapter = new MovieAdapter(this);
        // Set the LayoutManager and Adapter for the RecyclerView.
        mMoviesRecyclerView.setLayoutManager(layoutManager);
        mMoviesRecyclerView.setAdapter(mMovieAdapter);

        loadMovies();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void loadMovies() {
        Log.d(TAG, "Will invoke a new FetchDiscoverMoviesTask to load the Movies.");
        // TODO: Replace this, rename this, and pass in the required data.
        new FetchDiscoverMoviesTask(this).execute();
    }

    private void showMovies(List<Movie> movies) {
        // Hide the error message, if applicable.
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        // Show the view with the movies after setting them.
        mMovieAdapter.setMovies(movies);
        mMoviesRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showError(String errorMessage) {
        // Show the error message to the user.
        mErrorMessageTextView.setText(errorMessage);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
        // Hide the view.
        mMoviesRecyclerView.setVisibility(View.INVISIBLE);
    }

    public class FetchDiscoverMoviesTask extends AsyncTask<Void, Integer, List<Movie>> {

        private final String TAG = FetchDiscoverMoviesTask.class.getSimpleName();

        private final WeakReference<Activity> weakActivity;

        public FetchDiscoverMoviesTask(Activity activity) {
            this.weakActivity = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Now the task is starting, show the progress bar.
            mMoviesLoadingProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Movie> doInBackground(Void... voids) {

            try {
                // Get the generated request URL.
                // TODO: Swap this overload out for one that takes a sorting order.
                Log.d(TAG, "Will attempt to get the movie request URL.");
                URL moviesRequestUrl = TMDbPreferences.getDiscoverURL(weakActivity.get());
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
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Integer progress = values[0];
            Log.d(TAG, "Progress update '" + progress + "'.");
            mMoviesLoadingProgressBar.setProgress(progress, true);
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            // No matter what, the task has finished, hide the progress bar.
            mMoviesLoadingProgressBar.setVisibility(View.INVISIBLE);
            // Check the the Movies are present before use.
            if (movies == null) {
                showError("Could not display the movies, none were returned.");
                return;
            }
            // The movies are present, show them on the UI.
            showMovies(movies);
        }
    }
}
