package uk.ab.popularmovies.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import uk.ab.popularmovies.R;
import uk.ab.popularmovies.entities.Movie;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> mMovies;

    public MovieAdapter() { }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {

        public final TextView mMovieTitleTextView;

        public MovieViewHolder(View view) {
            super(view);
            mMovieTitleTextView = view.findViewById(R.id.movie_list_item_title);
        }
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // Get the LayoutInflater to inflate each view for the movies.
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        // Inflate the 'movie_list_item' view.
        View view = inflater.inflate(R.layout.movie_list_item, viewGroup, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder movieViewHolder, int position) {
        Movie movie = mMovies.get(position);
        movieViewHolder.mMovieTitleTextView.setText(movie.getTitle());
    }

    @Override
    public int getItemCount() {
        // Return the number of movies if it is possible.
        return (mMovies != null) ? mMovies.size() : 0;
    }

    public void setMovies(List<Movie> movies) {
        mMovies = movies;
        notifyDataSetChanged();
    }
}
