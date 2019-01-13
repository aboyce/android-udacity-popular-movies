package uk.ab.popularmovies;

import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import uk.ab.popularmovies.entities.Movie;
import uk.ab.popularmovies.entities.enums.MovieSort;
import uk.ab.popularmovies.utilities.NetworkUtility;
import uk.ab.popularmovies.view.MovieAdapter;
import uk.ab.popularmovies.viewmodels.MainViewModel;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String BUNDLE_MOVIES_VIEW_STATE = "BUNDLE_MOVIES_VIEW_STATE";

    private static final int NUMBER_OF_COLUMNS = 2;

    private MainViewModel viewModel;

    private RecyclerView mMoviesRecyclerView;
    private MovieAdapter mMovieAdapter;

    private TextView mErrorMessageTextView;

    private NetworkBroadcastReceiver mNetworkReceiver;
    private IntentFilter mNetworkIntentFilter;

    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Locate and assign the error view item.
        mErrorMessageTextView = findViewById(R.id.tv_main_error);

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

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getObserver().observe(this, event -> {
            // The view model has let us know that a movie update has occurred.
            Log.d(TAG, "An update has been made from the view model.");
            showMovies(viewModel.getMovies(getApplicationContext()));
            // As soon as the movies have loaded, ensure the scroll position is correct.
            if (savedInstanceState != null) {
                Log.d(TAG, "There is a saved instance state, will attempt to resume.");
                // Restore the recycler view.
                if (savedInstanceState.containsKey(BUNDLE_MOVIES_VIEW_STATE)) {
                    Parcelable savedRecyclerViewState = savedInstanceState.getParcelable(BUNDLE_MOVIES_VIEW_STATE);
                    layoutManager.onRestoreInstanceState(savedRecyclerViewState);
                    Log.d(TAG, "Restored the recycler view state.");
                }
            }
        });
        mNetworkReceiver = new NetworkBroadcastReceiver();
        mNetworkIntentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register for any network changes.
        registerReceiver(mNetworkReceiver, mNetworkIntentFilter);
        // The connection status may have changed since the application was last used.
        displayMenuItems();
        if (viewModel.getLiveMovies() != null) {
            viewModel.getLiveMovies().observe(this, movies -> {
                viewModel.setLocalMovies(movies);
                if (viewModel.getMovieSortOrder().equals(MovieSort.FAVOURITES)) {
                    showMovies(movies);
                }
            });
        }
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
        showMovies(viewModel.getMovies(context));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "Saving the recycler view state.");
        outState.putParcelable(BUNDLE_MOVIES_VIEW_STATE, mMoviesRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    private void showMovies(List<Movie> movies) {
        if (movies == null) {
            Log.w(TAG, "The list of movies to show was null.");
            return;
        }
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
            case R.id.main_sort_popularity:
                Log.i(TAG, "The sort by popularity menu item was clicked.");
                viewModel.setMovieSortOrder(MovieSort.POPULARITY);
                loadMovies(getApplicationContext());
                // The movie sort has been reselected, reset the scroll position.
                mMoviesRecyclerView.getLayoutManager().scrollToPosition(0);
                return true;
            case R.id.main_sort_rating:
                Log.i(TAG, "The sort by rating menu item was clicked.");
                viewModel.setMovieSortOrder(MovieSort.RATED);
                loadMovies(getApplicationContext());
                // The movie sort has been reselected, reset the scroll position.
                mMoviesRecyclerView.getLayoutManager().scrollToPosition(0);
                return true;
            case R.id.main_sort_favourites:
                Log.i(TAG, "The sort by favourites menu item was clicked.");
                viewModel.setMovieSortOrder(MovieSort.FAVOURITES);
                loadMovies(getApplicationContext());
                // The movie sort has been reselected, reset the scroll position.
                mMoviesRecyclerView.getLayoutManager().scrollToPosition(0);
                return true;
            default:
                Log.w(TAG, "There was no match for the clicked menu item " + menuId + ".");
                return super.onOptionsItemSelected(item);
        }
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
                // In case they were not previously available, load the movies now.
                viewModel.refreshOnlineMovies(context);
            } else {
                String message = "Internet connection has been lost, will load your saved (favourite) movies.";
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                // Reload the movies, it will change to favourites (offline) movies if required.
                loadMovies(context);
            }
        }
    }
}
