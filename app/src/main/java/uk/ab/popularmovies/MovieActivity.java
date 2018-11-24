package uk.ab.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import uk.ab.popularmovies.entities.Movie;

public class MovieActivity extends AppCompatActivity {

    public static final String MOVIE_INTENT = "MOVIE_OBJECT";

    private static final String TAG = MovieActivity.class.getSimpleName();

    private TextView mMovieTitleTextView;
    private ImageView mMovieImageImageView;
    private TextView mMoviePlotSynopsisTextView;
    private TextView mMovieReleaseDateTextView;

    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        mMovieTitleTextView = findViewById(R.id.movie_title_tv);
        mMovieImageImageView = findViewById(R.id.movie_image_iv);
        mMoviePlotSynopsisTextView = findViewById(R.id.movie_plot_synopsis_tv);
        mMovieReleaseDateTextView = findViewById(R.id.movie_release_date_tv);
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
        mMovieTitleTextView.setText(movie.getTitle());
        //mMoviePlotSynopsisTextView.setText(movie.getPlotSynopsis());
        //mMovieReleaseDateTextView.setText(movie.getReleaseDate().getYear());
    }
}
