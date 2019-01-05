package uk.ab.popularmovies.utilities;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import uk.ab.popularmovies.entities.MovieReview;

public class MovieReviewUtility {

    private static final String TAG = MovieReviewUtility.class.getSimpleName();

    private static final String JSON_REVIEW_RESULTS = "results";
    private static final String JSON_REVIEW_ID = "id";
    private static final String JSON_REVIEW_AUTHOR = "author";
    private static final String JSON_REVIEW_CONTENT = "content";
    private static final String JSON_REVIEW_URL = "url";

    // This Class should not need to be instantiated.
    private MovieReviewUtility() { }

    public static List<MovieReview> getMovieReviewsFromJson(String json) {

        List<MovieReview> movieReviews = new ArrayList<>();

        if (json == null || json.equals("")) {
            String message = "The JSON provided is null or empty, cannot be parsed into movie reviews.";
            Log.e(TAG, message);
            throw new IllegalArgumentException(message);
        }

        // The full JSON object to parse.
        JSONObject responseJson;

        try {
            // Convert the full response object into a JSON object.
            responseJson = new JSONObject(json);

            // Extract the array of JSON objects into a JSON Array.
            JSONArray movieReviewsJson = responseJson.getJSONArray(JSON_REVIEW_RESULTS);
            if (movieReviewsJson != null) {
                // Go over each JSON object that represents a JSON movie review.
                for (int i = 0; i < movieReviewsJson.length(); i++) {
                    JSONObject movieReviewJson = movieReviewsJson.getJSONObject(i);
                    if (movieReviewJson == null) {
                        Log.w(TAG, "Could not get a movie review from the JSON Array of movie reviews. ");
                        continue;
                    }
                    // Convert the current JSON object into a movie review object.
                    MovieReview currentMovieReview = getMovieReviewFromJson(movieReviewJson);
                    if (currentMovieReview == null) {
                        Log.w(TAG, "Could not get a movie review from the JSON object at index " + i + ".");
                        continue;
                    }
                    Log.d(TAG, "Added movie review " + currentMovieReview.getId() + " to the array.");
                    // Add the newly parsed movie review to the List.
                    movieReviews.add(currentMovieReview);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Could not parse the JSON provided, message: " + e.getMessage());
            return null;
        }

        return movieReviews;
    }

    private static MovieReview getMovieReviewFromJson(JSONObject json) {

        if (json == null) {
            String message = "The JSON provided is null, cannot be parsed into a movie review.";
            Log.e(TAG, message);
            throw new IllegalArgumentException(message);
        }

        // Check that all the required properties are present;
        if (!json.has(JSON_REVIEW_ID)) {
            Log.w(TAG, "The JSON object does not contain a value for the id.");
            return null;
        }
        if (!json.has(JSON_REVIEW_AUTHOR)) {
            Log.w(TAG, "The JSON object does not contain a value for the author.");
            return null;
        }
        if (!json.has(JSON_REVIEW_CONTENT)) {
            Log.w(TAG, "The JSON object does not contain a value for the content.");
            return null;
        }
        if (!json.has(JSON_REVIEW_URL)) {
            Log.w(TAG, "The JSON object does not contain a value for the url.");
            return null;
        }

        String id = null;
        String author = null;
        String content = null;
        String url = null;

        // Extract all the required properties from JSON to Java.
        try {
            id = json.getString(JSON_REVIEW_ID);
            author = json.getString(JSON_REVIEW_AUTHOR);
            content = json.getString(JSON_REVIEW_CONTENT);
            url = json.getString(JSON_REVIEW_URL);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Could not convert the JSON into a movie review, message: " + e.getMessage());
        }

        Log.v(TAG, "Movie review id: '" + id + "'.");
        Log.v(TAG, "Movie review author: '" + author + "'.");
        Log.v(TAG, "Movie content name: '" + content + "'.");
        Log.v(TAG, "Movie url site: '" + url + "'.");

        // Combine all the extracted properties into a Movie object.
        return new MovieReview(id, author, content, url);
    }
}
