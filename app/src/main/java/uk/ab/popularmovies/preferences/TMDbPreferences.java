package uk.ab.popularmovies.preferences;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

import uk.ab.popularmovies.R;
import uk.ab.popularmovies.entities.enums.MovieSort;

public class TMDbPreferences {

    private static final String TAG = TMDbPreferences.class.getSimpleName();

    // API Configuration Values.
    private static final String API_BASE_URL = "https://api.themoviedb.org";
    private static final String API_BASE_IMAGE_URL = "https://image.tmdb.org/t/p/";
    private static final String API_IMAGE_SIZE = "w500";
    private static final String API_VERSION = "3";
    private static final String API_KEY = "api_key";
    private static final String API_DISCOVER = "discover";
    private static final String API_POPULAR = "popular";
    private static final String API_RATED = "top_rated";
    private static final String API_MOVIE = "movie";
    private static final String API_TRAILER = "videos";
    private static final String API_REVIEW = "reviews";

    private static final String API_SORT_BY = "sort_by";
    private static final MovieSort API_SORT_BY_DEFAULT = MovieSort.POPULARITY;

    private static final String API_LANGUAGE = "language";
    private static final String API_LANGUAGE_DEFAULT = "en-GB";

    private static final String API_NSFW = "include_adult";
    private static final String API_NSFW_DEFAULT = "false";

    // This Class should not need to be instantiated.
    private TMDbPreferences() { }

    private static String getApiKey(Context context) {

        if (context == null) {
            String message = "A valid context was not provided to access the API key.";
            Log.e(TAG, message);
            throw new IllegalArgumentException(message);
        }

        String apiKey = null;
        try {
            // Extract the API key using the provided context.
            apiKey = context.getString(R.string.movie_db_api_key);
            Log.d(TAG, "Located the API Key.");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Could not extract the API Key.");
        }
        return apiKey;
    }

    public static URL getDiscoverURL(Context context) {
        return getDiscoverURL(context, API_SORT_BY_DEFAULT);
    }

    public static URL getDiscoverURL(Context context, MovieSort sortOrder) {

        URL discoverUrl = null;
        String apiKey = getApiKey(context);

        Uri builtUri = Uri.parse(API_BASE_URL + "/" + API_VERSION + "/" + API_DISCOVER + "/" + API_MOVIE)
                .buildUpon()
                .appendQueryParameter(API_KEY, apiKey)
                .appendQueryParameter(API_LANGUAGE, API_LANGUAGE_DEFAULT)
                .appendQueryParameter(API_NSFW, API_NSFW_DEFAULT)
                .appendQueryParameter(API_SORT_BY, sortOrder.toString())
                .build();

        try {
            discoverUrl = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG, "Unable to parse the built discover URL, message: " + e.getMessage());
        }

        // Remove the API for logging purposes.
        String cleanUrl = builtUri.toString().replace(apiKey, "****");
        Log.d(TAG, "Built the discover URL: " + cleanUrl);

        return discoverUrl;
    }

    public static URL getMoviePopularURL(Context context) {

        URL moviePopularUrl = null;
        String apiKey = getApiKey(context);

        Uri builtUri = Uri.parse(API_BASE_URL + "/" + API_VERSION + "/" + API_MOVIE + "/" + API_POPULAR)
                .buildUpon()
                .appendQueryParameter(API_KEY, apiKey)
                .appendQueryParameter(API_LANGUAGE, API_LANGUAGE_DEFAULT)
                .build();

        try {
            moviePopularUrl = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG, "Unable to parse the built movie URL, message: " + e.getMessage());
        }

        // Remove the API for logging purposes.
        String cleanUrl = builtUri.toString().replace(apiKey, "****");
        Log.d(TAG, "Built the discover URL: " + cleanUrl);

        return moviePopularUrl;
    }

    public static URL getMovieRatedURL(Context context) {

        URL moviePopularUrl = null;
        String apiKey = getApiKey(context);

        Uri builtUri = Uri.parse(API_BASE_URL + "/" + API_VERSION + "/" + API_MOVIE + "/" + API_RATED)
                .buildUpon()
                .appendQueryParameter(API_KEY, apiKey)
                .appendQueryParameter(API_LANGUAGE, API_LANGUAGE_DEFAULT)
                .build();

        try {
            moviePopularUrl = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG, "Unable to parse the built movie URL, message: " + e.getMessage());
        }

        // Remove the API for logging purposes.
        String cleanUrl = builtUri.toString().replace(apiKey, "****");
        Log.d(TAG, "Built the discover URL: " + cleanUrl);

        return moviePopularUrl;
    }

    public static URL getMovieTrailersURL(Context context, Integer movieId) {

        URL trailerUrl = null;
        String apiKey = getApiKey(context);

        Uri builtUri = Uri.parse(API_BASE_URL + "/" + API_VERSION + "/" + API_MOVIE + "/" + movieId + "/" + API_TRAILER)
                .buildUpon()
                .appendQueryParameter(API_KEY, apiKey)
                .appendQueryParameter(API_LANGUAGE, API_LANGUAGE_DEFAULT)
                .build();

        try {
            trailerUrl = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG, "Unable to parse the built trailer URL, message: " + e.getMessage());
        }

        // Remove the API for logging purposes.
        String cleanUrl = builtUri.toString().replace(apiKey, "****");
        Log.d(TAG, "Built the trailer URL: " + cleanUrl);

        return trailerUrl;
    }

    public static URL getMovieReviewsURL(Context context, Integer movieId) {

        URL reviewUrl = null;
        String apiKey = getApiKey(context);

        Uri builtUri = Uri.parse(API_BASE_URL + "/" + API_VERSION + "/" + API_MOVIE + "/" + movieId + "/" + API_REVIEW)
                .buildUpon()
                .appendQueryParameter(API_KEY, apiKey)
                .appendQueryParameter(API_LANGUAGE, API_LANGUAGE_DEFAULT)
                .build();

        try {
            reviewUrl = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG, "Unable to parse the built review URL, message: " + e.getMessage());
        }

        // Remove the API for logging purposes.
        String cleanUrl = builtUri.toString().replace(apiKey, "****");
        Log.d(TAG, "Built the review URL: " + cleanUrl);

        return reviewUrl;
    }

    public static URL getImageURL(String imagePath) {

        if (imagePath == null || imagePath.equals("")) {
            Log.e(TAG, "The image path for the image URL was null or empty.");
            return null;
        }

        URL imageUrl = null;
        Uri builtUri = Uri.parse(API_BASE_IMAGE_URL + "/" + API_IMAGE_SIZE + imagePath);
        try {
            imageUrl = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG, "Unable to parse the built discover URL, message: " + e.getMessage());
        }
        Log.d(TAG, "Built the image URL: " + imageUrl);
        return imageUrl;
    }
}