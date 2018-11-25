package uk.ab.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URL;

import uk.ab.popularmovies.entities.Movie;
import uk.ab.popularmovies.preferences.TMDbPreferences;

public class MovieActivity extends AppCompatActivity {

    public static final String MOVIE_INTENT = "MOVIE_OBJECT";

    private static final String TAG = MovieActivity.class.getSimpleName();

    private TextView mMovieTitleTextView;
    private ImageView mMovieImageImageView;
    private TextView mMoviePlotSynopsisTextView;
    private TextView mMovieReleaseDateTextView;
    private TextView mMovieRatingTextView;

    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        mMovieTitleTextView = findViewById(R.id.tv_movie_title);
        mMovieImageImageView = findViewById(R.id.iv_movie_image);
        mMoviePlotSynopsisTextView = findViewById(R.id.tv_movie_plot_synopsis);
        mMovieReleaseDateTextView = findViewById(R.id.tv_movie_release_date);
        mMovieRatingTextView = findViewById(R.id.tv_movie_rating);
        Log.d(TAG, "Located all of the view components.");

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
        updateUserInterface();
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
}
