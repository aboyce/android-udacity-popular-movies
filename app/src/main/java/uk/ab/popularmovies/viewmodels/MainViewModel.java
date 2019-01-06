package uk.ab.popularmovies.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

import uk.ab.popularmovies.entities.Movie;
import uk.ab.popularmovies.entities.database.ApplicationDatabase;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();

    private LiveData<List<Movie>> movies;

    public MainViewModel(Application application) {
        super(application);
        ApplicationDatabase database = ApplicationDatabase.getInstance(this.getApplication());
        movies = database.movieDao().getAllMovies();
    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }
}
