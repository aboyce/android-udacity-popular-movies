package uk.ab.popularmovies.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import uk.ab.popularmovies.R;
import uk.ab.popularmovies.entities.MovieTrailer;
import uk.ab.popularmovies.utilities.MovieTrailerUtility;

public class MovieTrailerAdapter extends RecyclerView.Adapter<MovieTrailerAdapter.MovieTrailerViewHolder> {

    private static final String TAG = MovieTrailerAdapter.class.getSimpleName();

    private List<MovieTrailer> mMovieTrailers;

    private Context context;

    public MovieTrailerAdapter(Context context) {
        this.context = context;
    }

    static class MovieTrailerViewHolder extends RecyclerView.ViewHolder {

        private static final String TAG = MovieTrailerViewHolder.class.getSimpleName();

        final TextView mMovieTrailerName;

        MovieTrailerViewHolder(View view) {
            super(view);
            mMovieTrailerName = view.findViewById(R.id.tv_movie_trailer_item_name);
        }
    }

    @NonNull
    @Override
    public MovieTrailerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // Get the LayoutInflater to inflate each view for the movie trailers.
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        // Inflate the 'movie_trailer_item' view.
        View view = inflater.inflate(R.layout.movie_trailer_item, viewGroup, false);
        MovieTrailerViewHolder viewHolder = new MovieTrailerViewHolder(view);

        // Handle when a movie trailer has been clicked on, let handleMovieTrailerClick start the intent.
        view.setOnClickListener(clickedView -> {
            Integer movieTrailerPosition = viewHolder.getAdapterPosition();
            Log.i(TAG, "Movie trailer at position " + movieTrailerPosition + " has been clicked on.");
            handleMovieTrailerClick(movieTrailerPosition);
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieTrailerViewHolder viewHolder, int position) {
        // The movie trailer object that will be displayed.
        MovieTrailer movieTrailer = mMovieTrailers.get(position);
        viewHolder.mMovieTrailerName.setText(movieTrailer.getName());
    }

    @Override
    public int getItemCount() {
        return (mMovieTrailers != null) ? mMovieTrailers.size() : 0;
    }

    private void handleMovieTrailerClick(int movieTrailerPosition) {
        MovieTrailer movieTrailer = mMovieTrailers.get(movieTrailerPosition);
        if (movieTrailer == null) {
            Log.wtf(TAG, "An invalid movie trailer was clicked on at position " + movieTrailerPosition + ".");
            return;
        }
        Log.d(TAG, "Movie trailer '" + movieTrailer.getId() + "' was clicked on.");

        Uri intentUri = MovieTrailerUtility.getMovieTrailerUri(movieTrailer);

        // The URI will be null if there is no known way of displaying the movie trailer.
        if (intentUri == null) {
            String message = "Could not properly display the movie trailer, unknown source '" + movieTrailer.getSite() + "'.";
            Log.e(TAG, message);
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, intentUri);
        context.startActivity(intent);
    }

    public void setMovieTrailers(List<MovieTrailer> movieTrailers) {
        mMovieTrailers = movieTrailers;
        notifyDataSetChanged();
    }
}
