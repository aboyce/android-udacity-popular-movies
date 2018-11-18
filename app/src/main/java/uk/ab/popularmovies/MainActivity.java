package uk.ab.popularmovies;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Locate and assign the RecyclerView to display the movies.
        mMoviesRecyclerView = findViewById(R.id.rview_movies);
        // Optimisation due to the API only returning a set number at this stage.
        mMoviesRecyclerView.setHasFixedSize(true);
        // Create a new LayoutManager for the movies.
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        // Create a new MovieAdapter for the movies.
        mMovieAdapter = new MovieAdapter();
        // Set the LayoutManager and Adapter for the RecyclerView.
        mMoviesRecyclerView.setLayoutManager(layoutManager);
        mMoviesRecyclerView.setAdapter(mMovieAdapter);

        // TODO: Replace this, rename this, and pass in the required data.
        new FetchDiscoverMoviesTask(this).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public class FetchDiscoverMoviesTask extends AsyncTask<Void, Void, List<Movie>> {

        private final WeakReference<Activity> weakActivity;

        public FetchDiscoverMoviesTask(Activity activity) {
            this.weakActivity = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Movie> doInBackground(Void... voids) {

            try {
                // Get the generated request URL.
                // TODO: Swap this overload out for one that takes a sorting order.
                Log.d(TAG, "Will attempt to get the movie request URL.");
                URL moviesRequestUrl = TMDbPreferences.getDiscoverURL(weakActivity.get());
                Log.d(TAG, "Retrieved the URL for the movie request.");

                // Use the generated URL to go and retrieve the movie JSON data.
                Log.d(TAG, "Will attempt to request the Movies from the URL.");
                String moviesJson = NetworkUtility.getJSONFromURL(moviesRequestUrl);
                if (moviesJson == null) {
                    Log.e(TAG, "The movie JSON has been returned as null.");
                    return null;
                }
                Log.d(TAG, "The movie JSON data has been returned.");

                // Now the JSON has been returned, convert this to a List of Movies.
                Log.d(TAG, "Will attempt to parse the movie JSON into Movie objects.");
                List<Movie> movies = MovieUtility.getMoviesFromDiscoverJson(moviesJson);
                if (movies == null) {
                    Log.e(TAG, "The attempt to parse the movie JSON returned null.");
                    return null;
                }
                Log.d(TAG, "The movie JSON has successfully been parsed into movies.");

                // Now the Movies have been converted, return them to the UI thread.
                Log.d(TAG, "Will return the " + movies.size() + " movies to be displayed.");
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
            // Check the the Movies are present before use.
            if (movies == null) {
                // TODO: Show the error message.
                return;
            }
            // TODO: Show the actual content.
            mMovieAdapter.setMovies(movies);
        }
    }
}
