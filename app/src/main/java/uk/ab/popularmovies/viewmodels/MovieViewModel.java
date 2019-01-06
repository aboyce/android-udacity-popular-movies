package uk.ab.popularmovies.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import uk.ab.popularmovies.entities.Movie;
import uk.ab.popularmovies.entities.database.ApplicationDatabase;

public class MovieViewModel extends ViewModel {

    private LiveData<Movie> movie;

    public MovieViewModel(ApplicationDatabase database, Integer movieId) {
        movie = database.movieDao().getMovieFromId(movieId);
    }

    public LiveData<Movie> getMovie() {
        return movie;
    }
}
