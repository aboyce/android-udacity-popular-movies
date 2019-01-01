package uk.ab.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.List;

import uk.ab.popularmovies.entities.Movie;
import uk.ab.popularmovies.entities.MovieTrailer;
import uk.ab.popularmovies.preferences.TMDbPreferences;
import uk.ab.popularmovies.utilities.MovieTrailerUtility;
import uk.ab.popularmovies.utilities.NetworkUtility;
import uk.ab.popularmovies.view.MovieTrailerAdapter;

public class MovieActivity extends AppCompatActivity {

    public static final String MOVIE_INTENT = "MOVIE_OBJECT";

    private static final String TAG = MovieActivity.class.getSimpleName();

    private TextView mMovieTitleTextView;
    private ImageView mMovieImageImageView;
    private TextView mMoviePlotSynopsisTextView;
    private TextView mMovieReleaseDateTextView;
    private TextView mMovieRatingTextView;

    private RecyclerView mMovieTrailerRecyclerView;
    private MovieTrailerAdapter mMovieTrailerAdapter;

    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        Intent callingIntent = getIntent();
        if (callingIntent == null) {
            Log.e(TAG, "The calling intent was null");
            finish();
            return;
        }
        Log.d(TAG, "The calling intent was present.");
        if (!callingIntent.hasExtra(MOVIE_INTENT)) {
            Log.e(TAG, "The calling intent was not provided with a Movie.");
            finish();
            return;
        }
        Log.d(TAG, "The calling intent was provided with the expected extra.");
        movie = callingIntent.getParcelableExtra(MOVIE_INTENT);
        Log.d(TAG, "Extracted the movie '" + movie.getTitle() + "' from the intent extra.");

        mMovieTitleTextView = findViewById(R.id.tv_movie_title);
        mMovieImageImageView = findViewById(R.id.iv_movie_image);
        mMoviePlotSynopsisTextView = findViewById(R.id.tv_movie_plot_synopsis);
        mMovieReleaseDateTextView = findViewById(R.id.tv_movie_release_date);
        mMovieRatingTextView = findViewById(R.id.tv_movie_rating);
        Log.d(TAG, "Located all of the view components.");

        // Locate and assign the RecyclerView to display the movie trailers.
        mMovieTrailerRecyclerView = findViewById(R.id.rview_movie_trailers);
        // Create a new LayoutManager for the movie trailers.
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        // Create a new MovieTrailerAdapter for the movie trailers.
        mMovieTrailerAdapter = new MovieTrailerAdapter(this);
        // Set the LayoutManager and Adapter for the RecyclerView.
        mMovieTrailerRecyclerView.setLayoutManager(layoutManager);
        mMovieTrailerRecyclerView.setAdapter(mMovieTrailerAdapter);

        updateUserInterface();
        loadMovieTrailers();
    }

    private void updateUserInterface() {
        URL imageUrl = TMDbPreferences.getImageURL(movie.getAnyImagePath());
        Log.d(TAG, "The movie image path is '" + imageUrl.toString() + "'.");

        // Due to early screening, there will always be an image available to download.
        Picasso.with(getApplicationContext())
                .load(imageUrl.toString())
                .into(mMovieImageImageView);

        mMovieTitleTextView.setText(movie.getTitle());
        mMoviePlotSynopsisTextView.setText(movie.getPlotSynopsis());

        // Bit of a raw check, but if it will display '0.0' let it use the default value instead.
        if (!movie.getUserRating().toString().equals("0.0")) {
            String rating = Double.toString(movie.getUserRating());
            mMovieRatingTextView.setText(rating);
        }

        // Only update the release date if it is present, leave it to the default value if not.
        if (movie.getReleaseDate() == null) {
            Log.w(TAG, "There was not release date available for '" + movie.getTitle() + "'.");
        } else {
            String releaseDate = Integer.toString(movie.getReleaseDate().getYear());
            Log.d(TAG, "A release date '" + releaseDate + "' is available for '" + movie.getTitle() + "'.");
            mMovieReleaseDateTextView.setText(releaseDate);
        }
    }

    private void loadMovieTrailers() {
        Log.d(TAG, "Will invoke a new FetchTrailersTasks to load the movie trailers.");
        new FetchTrailersTasks(this).execute(movie.getId());
    }

    public class FetchTrailersTasks extends AsyncTask<Integer, Integer, List<MovieTrailer>> {

        private final String TAG = FetchTrailersTasks.class.getSimpleName();

        private final WeakReference<Activity> weakActivity;

        FetchTrailersTasks(Activity activity) {
            this.weakActivity = new WeakReference<>(activity);
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
            // Check the the Movies are present before use.
            if (movieTrailers == null) {
                Log.e(TAG, "Could not load the movie trailers for movie " + movie.getTitle() + ".");
                return;
            }
            mMovieTrailerAdapter.setMovieTrailers(movieTrailers);
        }
    }
}
