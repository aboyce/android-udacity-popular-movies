package uk.ab.popularmovies.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import uk.ab.popularmovies.asynctasks.GetMoviesAsyncTask;
import uk.ab.popularmovies.asynctasks.GetMoviesAsyncTaskExecutor;
import uk.ab.popularmovies.entities.Movie;
import uk.ab.popularmovies.entities.database.ApplicationDatabase;
import uk.ab.popularmovies.entities.enums.MovieSort;
import uk.ab.popularmovies.entities.executors.ApplicationExecutors;
import uk.ab.popularmovies.utilities.NetworkUtility;

public class MainViewModel extends AndroidViewModel implements GetMoviesAsyncTaskExecutor {

    private static final String TAG = MainViewModel.class.getSimpleName();
    private static final MovieSort DEFAULT_MOVIE_SORT = MovieSort.POPULARITY;

    private MovieSort movieSortOrder = DEFAULT_MOVIE_SORT;

    private LiveData<Object> observer;

    // These are the movies returned from the database.
    private List<Movie> localMovies;
    private LiveData<List<Movie>> liveLocalMovies;
    // These are the movies that are just a 'cache' for the API.
    private List<Movie> popularMovies;
    private List<Movie> ratedMovies;

    public MainViewModel(Application application) {
        super(application);
        // Create the observable object.
        observer = new MutableLiveData<>();
        // Populate all of the movie lists.
        ApplicationExecutors.getInstance().diskIO().execute(() -> {
            refreshOnlineMovies(application.getApplicationContext());
            ApplicationDatabase database = ApplicationDatabase.getInstance(this.getApplication());
            liveLocalMovies = database.movieDao().getAllMoviesLive();
            localMovies = database.movieDao().getAllMovies();
        });
    }

    public List<Movie> getMovies(Context context) {

        boolean isFavourite = movieSortOrder.equals(MovieSort.FAVOURITES);

        // If it is just the favourites, return them, we have no other checks.
        if (isFavourite) {
            Log.d(TAG, "Returning the offline " + localMovies.size() + " favourite movies.");
            ApplicationExecutors.getInstance().diskIO().execute(() -> {
                ApplicationDatabase database = ApplicationDatabase.getInstance(this.getApplication());
                liveLocalMovies = database.movieDao().getAllMoviesLive();
            });
            return localMovies;
        }

        // Now we can do the network connectivity check.
        boolean isConnected = NetworkUtility.isConnectedToInternet(context);
        // If there is not an internet connection, set it to favourites.
        if (!isConnected) {
            setMovieSortOrder(MovieSort.FAVOURITES);
        }

        switch (movieSortOrder) {
            case POPULARITY:
                Log.d(TAG, "Returning the popular movies.");
                return popularMovies;
            case RATED:
                Log.d(TAG, "Returning the rated movies.");
                return ratedMovies;
            case FAVOURITES:
                Log.d(TAG, "Returning the offline favourite movies.");
                return localMovies;
            default:
                Log.w(TAG, "Unexpected movie sort, returning null.");
                return null;
        }
    }

    public LiveData<List<Movie>> getLiveMovies() {
        return this.liveLocalMovies;
    }

    public LiveData<Object> getObserver() {
        return this.observer;
    }

    public MovieSort getMovieSortOrder() {
        return this.movieSortOrder;
    }

    public void setMovieSortOrder(MovieSort movieSortOrder) {
        this.movieSortOrder = movieSortOrder;
    }

    public void setLocalMovies(List<Movie> movies) {
        this.localMovies = movies;
    }

    public void refreshOnlineMovies(Context context) {
        new GetMoviesAsyncTask(context, this, MovieSort.POPULARITY).execute();
        new GetMoviesAsyncTask(context, this, MovieSort.RATED).execute();
    }

    private void setMoviesHaveUpdated() {
        // This should let anything observing that an update has been made.
        ((MutableLiveData<Object>)observer).postValue(new Object());
    }

    @Override
    public void onGetMoviesTaskCompletion(MovieSort movieSort, List<Movie> movies) {

        if (movies == null) {
            Log.w(TAG, "The movies returned for " + movieSort + " was null.");
            popularMovies = null;
            ratedMovies = null;
            return;
        }

        switch (movieSort) {
            case POPULARITY:
                Log.d(TAG, "Received " + movies.size() + " popular movies.");
                popularMovies = movies;
                setMoviesHaveUpdated();
                break;
            case RATED:
                Log.d(TAG, "Received " + movies.size() + " rated movies.");
                ratedMovies = movies;
                setMoviesHaveUpdated();
                break;
            default:
                Log.e(TAG, "Unexpected movie sort order was returned.");
                break;
        }
    }
}
