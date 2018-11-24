package uk.ab.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class MovieActivity extends AppCompatActivity {

    private static final String TAG = MovieActivity.class.getSimpleName();

    private TextView mMovieTitleTextView;
    private ImageView mMovieImageImageView;
    private TextView mMovieDescriptionTextView;
    private TextView mMovieReleaseDateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        mMovieTitleTextView = findViewById(R.id.movie_title_tv);
        mMovieImageImageView = findViewById(R.id.movie_image_iv);
        mMovieDescriptionTextView = findViewById(R.id.movie_description_tv);
        mMovieReleaseDateTextView = findViewById(R.id.movie_release_date_tv);

        Intent callingIntent = getIntent();
        if (callingIntent == null) {
            Log.e(TAG, "The calling intent was null");
            finish();
        }


    }
}
