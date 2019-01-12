package uk.ab.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import uk.ab.popularmovies.asynctasks.GetMoviesAsyncTask;
import uk.ab.popularmovies.asynctasks.GetMoviesAsyncTaskExecutor;
import uk.ab.popularmovies.entities.Movie;
import uk.ab.popularmovies.entities.database.ApplicationDatabase;
import uk.ab.popularmovies.entities.enums.MovieSort;
import uk.ab.popularmovies.utilities.NetworkUtility;
import uk.ab.popularmovies.view.MovieAdapter;
import uk.ab.popularmovies.viewmodels.MainViewModel;

public class MainActivity extends AppCompatActivity implements GetMoviesAsyncTaskExecutor {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int NUMBER_OF_COLUMNS = 2;
    private static final MovieSort DEFAULT_MOVIE_SORT = MovieSort.POPULARITY;

    private MovieSort movieSortOrder = DEFAULT_MOVIE_SORT;

    private RecyclerView mMoviesRecyclerView;
    private MovieAdapter mMovieAdapter;

    private TextView mErrorMessageTextView;
    private ProgressBar mMoviesLoadingProgressBar;

    private NetworkBroadcastReceiver mNetworkReceiver;
    private IntentFilter mNetworkIntentFilter;

    private ApplicationDatabase mDatabase;

    private Menu mMenu;

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
        GridLayoutManager layoutManager = new GridLayoutManager(this, NUMBER_OF_COLUMNS);
        // Create a new MovieAdapter for the movies.
        mMovieAdapter = new MovieAdapter(this);
        // Set the LayoutManager and Adapter for the RecyclerView.
        mMoviesRecyclerView.setLayoutManager(layoutManager);
        mMoviesRecyclerView.setAdapter(mMovieAdapter);

        mNetworkReceiver = new NetworkBroadcastReceiver();
        mNetworkIntentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

        mDatabase = ApplicationDatabase.getInstance(getApplicationContext());

        loadMovies(getApplicationContext());
        setupViewModel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register for any network changes.
        registerReceiver(mNetworkReceiver, mNetworkIntentFilter);
        // The connection status may have changed since the application was last used.
        displayMenuItems();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister for any network changes.
        unregisterReceiver(mNetworkReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.mMenu = menu;
        // Ensure that the correct menu items are visible.
        displayMenuItems();
        return true;
    }

    private void loadMovies(Context context) {

        boolean isConnected = NetworkUtility.isConnectedToInternet(context);
        boolean isFavourites = movieSortOrder.equals(MovieSort.FAVOURITES);

        // If there is an internet connection, and something to load, get the movies from the API.
        if (isConnected && !(isFavourites)) {
            Log.d(TAG, "Will invoke a new GetMoviesAsyncTask to load the Movies.");
            new GetMoviesAsyncTask(this, this).execute(movieSortOrder);
            return;
        }

        // If there is no connection but we are loading movies, ensure it is on the favourites selection.
        // There is no other option if the connection is down, other scenarios have already been handled.
        movieSortOrder = MovieSort.FAVOURITES;

        // Now we can load the movies in from the local database.
        final LiveData<List<Movie>> favouriteMovies = mDatabase.movieDao().getAllMovies();
        favouriteMovies.observe(this, movies -> {
            Log.d(TAG, "Receiving database update of favourite movies, will display.");
            showMovies(movies);
        });
    }

    private void setupViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getMovies().observe(this, movies -> {
            Log.d(TAG, "The movies in the main view model has been updated.");
            // If the application was primarily offline, this is where you would update the movie collection.
        });
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

    private void displayMenuItems() {
        if (mMenu == null) {
            Log.w(TAG, "Could not update the menu items as the menu was not available.");
            return;
        }

        if (NetworkUtility.isConnectedToInternet(this)) {
            Log.d(TAG, "Will update the menu items to have the online options.");
            mMenu.findItem(R.id.main_sort_popularity).setVisible(true);
            mMenu.findItem(R.id.main_sort_rating).setVisible(true);
        } else {
            Log.d(TAG, "Will update the menu items to have only the offline options.");
            mMenu.findItem(R.id.main_sort_popularity).setVisible(false);
            mMenu.findItem(R.id.main_sort_rating).setVisible(false);
        }
        // This is always shown.
        mMenu.findItem(R.id.main_sort_favourites).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId = item.getItemId();
        Log.i(TAG, "The menu item " + menuId + " was clicked.");
        switch (menuId) {
            case R.id.main_refresh:
                Log.i(TAG, "The refresh menu item was clicked.");
                loadMovies(getApplicationContext());
                return true;
            case R.id.main_sort_popularity:
                Log.i(TAG, "The sort by popularity menu item was clicked.");
                movieSortOrder = MovieSort.POPULARITY;
                loadMovies(getApplicationContext());
                return true;
            case R.id.main_sort_rating:
                Log.i(TAG, "The sort by rating menu item was clicked.");
                movieSortOrder = MovieSort.RATED;
                loadMovies(getApplicationContext());
                return true;
            case R.id.main_sort_favourites:
                Log.i(TAG, "The sort by favourites menu item was clicked.");
                movieSortOrder = MovieSort.FAVOURITES;
                loadMovies(getApplicationContext());
                return true;
            default:
                Log.w(TAG, "There was no match for the clicked menu item " + menuId + ".");
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onGetMoviesTaskStart() {
        // Now the task is starting, show the progress bar.
        mMoviesLoadingProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onGetMoviesTaskProgressUpdate(int progress) {
        Log.d(TAG, "Progress update '" + progress + "'.");
        mMoviesLoadingProgressBar.setProgress(progress, true);
    }

    @Override
    public void onGetMoviesTaskCompletion(List<Movie> movies) {
        // No matter what, the task has finished, hide the progress bar.
        mMoviesLoadingProgressBar.setVisibility(View.INVISIBLE);
        // Check the the Movies are present before use.
        if (movies == null) {
            showError(getString(R.string.movie_load_error));
            return;
        }
        // The movies are present, show them on the UI.
        showMovies(movies);
    }

    private class NetworkBroadcastReceiver extends BroadcastReceiver {

        private final String TAG = NetworkBroadcastReceiver.class.getSimpleName();

        @Override
        public void onReceive(Context context, Intent intent) {

            // If the connection status has changes, update the menu items.
            displayMenuItems();

            // We will handle this check when the activity first starts manually, to prevent spam toasts.
            if (isInitialStickyBroadcast()) {
                Log.d(TAG, "Received initial sticky broadcast for network connectivity, ignoring.");
                return;
            }

            // The connection state has changed, check to see what the status is.
            if (NetworkUtility.isConnectedToInternet(context)) {
                // We don't want to reload as they may be happy on the favourite movies selection.
                String message = "Internet connection has been restored, you can change the sort order to load new movies.";
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            } else {
                String message = "Internet connection has been lost, will load your saved (favourite) movies.";
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                // Reload the movies, it will change to favourites (offline) movies if required.
                loadMovies(context);
            }
        }
    }
}
