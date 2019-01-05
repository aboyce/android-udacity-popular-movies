package uk.ab.popularmovies.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import uk.ab.popularmovies.R;
import uk.ab.popularmovies.entities.MovieReview;

public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.MovieReviewViewHolder> {

    private static final String TAG = MovieReviewAdapter.class.getSimpleName();

    private List<MovieReview> mMovieReviews;

    static class MovieReviewViewHolder extends RecyclerView.ViewHolder {

        private static final String TAG = MovieReviewViewHolder.class.getSimpleName();

        final TextView mMovieReviewAuthor;
        final TextView mMovieReviewContent;

        public MovieReviewViewHolder(View itemView) {
            super(itemView);
            mMovieReviewAuthor = itemView.findViewById(R.id.tv_movie_review_item_author);
            mMovieReviewContent = itemView.findViewById(R.id.tv_movie_review_item_content);
        }
    }

    @NonNull
    @Override
    public MovieReviewViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // Get the LayoutInflater to inflate each view for the movie reviews.
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        // Inflate the 'movie_review_item' view.
        View view = inflater.inflate(R.layout.movie_review_item, viewGroup, false);
        return new MovieReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieReviewViewHolder viewHolder, int position) {
        // The movie review object that will be displayed.
        MovieReview movieReview = mMovieReviews.get(position);
        viewHolder.mMovieReviewAuthor.setText(movieReview.getAuthor());
        viewHolder.mMovieReviewContent.setText(movieReview.getContent());
    }

    @Override
    public int getItemCount() {
        return (mMovieReviews != null) ? mMovieReviews.size() : 0;
    }

    public void setMovieReview(List<MovieReview> movieReviews) {
        mMovieReviews = movieReviews;
        notifyDataSetChanged();
    }
}
