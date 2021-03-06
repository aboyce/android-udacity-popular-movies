package uk.ab.popularmovies;

import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;

import uk.ab.popularmovies.asynctasks.GetReviewsAsyncTask;
import uk.ab.popularmovies.asynctasks.GetReviewsAsyncTaskExecutor;
import uk.ab.popularmovies.asynctasks.GetTrailersAsyncTask;
import uk.ab.popularmovies.asynctasks.GetTrailersAsyncTaskExecutor;
import uk.ab.popularmovies.entities.Movie;
import uk.ab.popularmovies.entities.MovieReview;
import uk.ab.popularmovies.entities.MovieTrailer;
import uk.ab.popularmovies.entities.database.ApplicationDatabase;
import uk.ab.popularmovies.entities.executors.ApplicationExecutors;
import uk.ab.popularmovies.preferences.TMDbPreferences;
import uk.ab.popularmovies.utilities.NetworkUtility;
import uk.ab.popularmovies.view.MovieReviewAdapter;
import uk.ab.popularmovies.view.MovieTrailerAdapter;

public class MovieActivity extends AppCompatActivity implements GetTrailersAsyncTaskExecutor, GetReviewsAsyncTaskExecutor {

    public static final String MOVIE_INTENT = "MOVIE_OBJECT";

    private static final String TAG = MovieActivity.class.getSimpleName();

    private TextView mMovieTitleTextView;
    private ImageView mMovieImageImageView;
    private TextView mMoviePlotSynopsisTextView;
    private TextView mMovieReleaseDateTextView;
    private TextView mMovieRatingTextView;
    private ImageView mMovieFavouriteImageView;

    private TextView mMovieTrailerLabel;
    private RecyclerView mMovieTrailerRecyclerView;
    private MovieTrailerAdapter mMovieTrailerAdapter;

    private TextView mMovieReviewLabel;
    private RecyclerView mMovieReviewRecyclerView;
    private MovieReviewAdapter mMovieReviewAdapter;

    private Movie movie;
    private boolean isMovieAFavourite = false;

    private ApplicationDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        Intent callingIntent = getIntent();

        if (callingIntent == null || !callingIntent.hasExtra(MOVIE_INTENT)) {
            Log.e(TAG, "The calling intent was null or not provided with a Movie.");
            finish();
            return;
        }

        Log.d(TAG, "The calling intent was provided with the expected extra.");

        initialiseViews();
        // Get the passed through movie from the main activity.
        movie = callingIntent.getParcelableExtra(MOVIE_INTENT);
        Log.d(TAG, "Extracted the movie '" + movie.getTitle() + "' from the intent extra.");

        // Initialise/populate the database instance.
        database = ApplicationDatabase.getInstance(getApplicationContext());

        // Create a new LayoutManager for the movie trailers and reviews.
        LinearLayoutManager trailersLayoutManager = new LinearLayoutManager(this);
        LinearLayoutManager reviewsLayoutManager = new LinearLayoutManager(this);
        // Create a new MovieTrailerAdapter for the movie trailers and reviews.
        mMovieTrailerAdapter = new MovieTrailerAdapter(this);
        mMovieReviewAdapter = new MovieReviewAdapter();
        // Set the LayoutManagers and Adapters for the RecyclerViews.
        mMovieTrailerRecyclerView.setLayoutManager(trailersLayoutManager);
        mMovieTrailerRecyclerView.setAdapter(mMovieTrailerAdapter);
        mMovieReviewRecyclerView.setLayoutManager(reviewsLayoutManager);
        mMovieReviewRecyclerView.setAdapter(mMovieReviewAdapter);

        updateUserInterface();
        loadMovieExtras();
        configureFavouriteClickEvent();
    }

    private void initialiseViews() {
        Log.d(TAG, "Will initialise all of the view components.");
        mMovieTitleTextView = findViewById(R.id.tv_movie_title);
        mMovieImageImageView = findViewById(R.id.iv_movie_image);
        mMoviePlotSynopsisTextView = findViewById(R.id.tv_movie_plot_synopsis);
        mMovieReleaseDateTextView = findViewById(R.id.tv_movie_release_date);
        mMovieRatingTextView = findViewById(R.id.tv_movie_rating);
        mMovieFavouriteImageView = findViewById(R.id.iv_movie_favourite);
        mMovieReviewLabel = findViewById(R.id.tv_movie_reviews_label);
        mMovieTrailerLabel = findViewById(R.id.tv_movie_trailers_label);
        // Recycler views.
        mMovieTrailerRecyclerView = findViewById(R.id.rview_movie_trailers);
        mMovieReviewRecyclerView = findViewById(R.id.rview_movie_reviews);
        Log.d(TAG, "Located all of the view components.");
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd LLLL yyyy");
            String releaseDate = movie.getReleaseDate().format(formatter);
            Log.d(TAG, "A release date '" + releaseDate + "' is available for '" + movie.getTitle() + "'.");
            mMovieReleaseDateTextView.setText(releaseDate);
        }
    }

    private void loadMovieExtras() {
        if (NetworkUtility.isConnectedToInternet(this)) {
            Log.d(TAG, "Will invoke a new GetTrailersAsyncTask to load the movie trailers.");
            new GetTrailersAsyncTask(this, this).execute(movie.getId());
            Log.d(TAG, "Will invoke a new GetReviewsAsyncTask to load the movie reviews.");
            new GetReviewsAsyncTask(this, this).execute(movie.getId());
        } else {
            // Hide the recycler views if there is going to be nothing to show.
            mMovieTrailerRecyclerView.setVisibility(View.GONE);
            mMovieReviewRecyclerView.setVisibility(View.GONE);
        }

        final LiveData<Movie> savedMovie = database.movieDao().getMovieFromId(movie.getId());
        savedMovie.observe(this, movie -> {
            if (movie == null) {
                Log.d(TAG, "Movie does not exist in the database, so has not been marked as a favourite.");
                mMovieFavouriteImageView.setImageResource(R.drawable.ic_star_empty);
                isMovieAFavourite = false;
            } else {
                Log.d(TAG, "The movie exists in the database, it will be marked as a favourite.");
                mMovieFavouriteImageView.setImageResource(R.drawable.ic_star);
                isMovieAFavourite = true;
            }
        });
    }

    private void configureFavouriteClickEvent() {
        mMovieFavouriteImageView.setOnClickListener(view -> {
            Log.d(TAG, "Favourite icon for " + movie.getId() + " has been clicked.");
            if (isMovieAFavourite) {
                // The movie is a favourite, this means that it exists in the database.
                // The icon has been clicked, this means that the movie should be removed from the database.
                ApplicationExecutors.getInstance().diskIO().execute(() -> {
                    database.movieDao().deleteMovie(movie);
                });
                mMovieFavouriteImageView.setImageResource(R.drawable.ic_star_empty);
                Log.d(TAG, "Movie " + movie.getId() + " has been removed.");

            } else {
                // The movie is not a favourite, this means that it does not (yet) exist in the database.
                // The icon has been clicked, this means that the movie should be added to the database.
                Log.d(TAG, "Movie " + movie.getId() + " is not a favourite, it will be added.");
                ApplicationExecutors.getInstance().diskIO().execute(() -> {
                    database.movieDao().insertMovie(movie);
                });
                mMovieFavouriteImageView.setImageResource(R.drawable.ic_star);
                Log.d(TAG, "Movie " + movie.getId() + " has been added.");
            }
            // Reset the favourite bool, for next time.
            isMovieAFavourite = !isMovieAFavourite;
        });
    }

    @Override
    public void onGetTrailersTaskCompletion(List<MovieTrailer> movieTrailers) {
        // Check the the movie trailers are present before use.
        if (movieTrailers == null) {
            Log.e(TAG, "Could not load the movie trailers for movie " + movie.getTitle() + ".");
            return;
        }
        // Update the adapter with the newly retrieved trailers.
        mMovieTrailerAdapter.setMovieTrailers(movieTrailers);
        // Update the label so that it displays the number of trailers.
        String trailersLabel = getString(R.string.movie_trailers) + " (" + movieTrailers.size() + ")";
        mMovieTrailerLabel.setText(trailersLabel);
        // Hide the recycler view if there is nothing to show.
        if (movieTrailers.size() < 1) {
            mMovieTrailerRecyclerView.setVisibility(View.GONE);
        } else {
            mMovieTrailerRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onGetReviewsTaskCompletion(List<MovieReview> movieReviews) {
        // Check the the movie reviews are present before use.
        if (movieReviews == null) {
            Log.e(TAG, "Could not load the movie reviews for movie " + movie.getTitle() + ".");
            return;
        }
        // Update the adapter with the newly retrieved reviews.
        mMovieReviewAdapter.setMovieReview(movieReviews);
        // Update the label so that it displays the number of reviews.
        String reviewsLabel = getString(R.string.movie_reviews) + " (" + movieReviews.size() + ")";
        mMovieReviewLabel.setText(reviewsLabel);
        // Hide the recycler view if there is nothing to show.
        if (movieReviews.size() < 1) {
            mMovieReviewRecyclerView.setVisibility(View.GONE);
        } else {
            mMovieReviewRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}
