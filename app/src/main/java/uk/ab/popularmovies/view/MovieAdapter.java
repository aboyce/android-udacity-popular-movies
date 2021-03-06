package uk.ab.popularmovies.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.stream.Collectors;

import uk.ab.popularmovies.MovieActivity;
import uk.ab.popularmovies.R;
import uk.ab.popularmovies.entities.Movie;
import uk.ab.popularmovies.preferences.TMDbPreferences;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private static final String TAG = MovieAdapter.class.getSimpleName();

    private static final int MOVIE_IMAGE_SPACE = 10;

    private List<Movie> mMovies;

    private Context context;

    public MovieAdapter(Context context) {
        this.context = context;
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {

        private static final String TAG = MovieViewHolder.class.getSimpleName();

        final ImageView mMovieImageImageView;

        MovieViewHolder(View view) {
            super(view);
            mMovieImageImageView = view.findViewById(R.id.iv_movie_item_image);
        }
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // Get the LayoutInflater to inflate each view for the movies.
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        // Inflate the 'movie_item' view.
        View view = inflater.inflate(R.layout.movie_item, viewGroup, false);
        MovieViewHolder viewHolder = new MovieViewHolder(view);

        // Handle when a movie has been clicked on, let handleMovieClick start the intent.
        view.setOnClickListener(clickedView -> {
            Integer moviePosition = viewHolder.getAdapterPosition();
            Log.i(TAG, "Movie at position " + moviePosition + " has been clicked on.");
            handleMovieClick(moviePosition);
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder movieViewHolder, int position) {
        // The movie object that will be displayed.
        Movie movie = mMovies.get(position);

        String imagePath = TMDbPreferences.getImageURL(movie.getAnyImagePath()).toString();

        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        // The width of the image should be half the screen witch, minus any extra for a gap.
        int imageWidth = (screenWidth / 2) - MOVIE_IMAGE_SPACE;
        // The images are all provided (poster images) at 500px*750px, keep this ratio (1*1.5).
        int imageHeight = (int)(imageWidth * 1.5);

        Picasso.with(context)
                .load(imagePath)
                .resize(imageWidth, imageHeight)
                .into(movieViewHolder.mMovieImageImageView);
    }

    @Override
    public int getItemCount() {
        // Return the number of movies if it is possible.
        return (mMovies != null) ? mMovies.size() : 0;
    }

    private void handleMovieClick(int moviePosition) {
        Movie movie = mMovies.get(moviePosition);
        if (movie == null) {
            Log.wtf(TAG, "An invalid movie was clicked on at position " + moviePosition + ".");
            return;
        }
        Log.d(TAG, "Movie '" + movie.getTitle() + "' was clicked on.");

        Intent intent = new Intent(context, MovieActivity.class);
        intent.putExtra(MovieActivity.MOVIE_INTENT, movie);
        Log.d(TAG, "Passing '" + movie.getTitle() + "' through as an intent extra.");
        context.startActivity(intent);
    }

    public void setMovies(List<Movie> movies) {
        // For this version of the application, if there is no images available they are not useful.
        // This functionality may want changing in the future.
        mMovies = movies.stream().filter(Movie::hasImagePath).collect(Collectors.toList());
        notifyDataSetChanged();
    }
}
