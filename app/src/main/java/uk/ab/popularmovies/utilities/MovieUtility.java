package uk.ab.popularmovies.utilities;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import uk.ab.popularmovies.entities.Movie;

public class MovieUtility {

    private static final String TAG = MovieUtility.class.getSimpleName();

    private static final String JSON_MOVIE_RESULTS = "results";
    private static final String JSON_MOVIE_ID = "id";
    private static final String JSON_MOVIE_TITLE = "title";
    private static final String JSON_MOVIE_IMAGE_PATH = "poster_path";
    private static final String JSON_MOVIE_BACKDROP_IMAGE_PATH = "backdrop_path";
    private static final String JSON_MOVIE_PLOT_SYNOPSIS = "overview";
    private static final String JSON_MOVIE_USER_RATING = "vote_average";
    private static final String JSON_MOVIE_RELEASE_DATE = "release_date";

    // This Class should not need to be instantiated.
    private MovieUtility() { }

    public static List<Movie> getMoviesFromDiscoverJson(String json) {

        List<Movie> movies = new ArrayList<>();

        if (json == null || json.equals("")) {
            String message = "The JSON provided is null or empty, cannot be parsed into movies.";
            Log.e(TAG, message);
            throw new IllegalArgumentException(message);
        }

        // The full JSON object to parse.
        JSONObject responseJson;

        try {
            // Convert the full response object into a JSON object.
            responseJson = new JSONObject(json);

            // Extract the array of JSON objects into a JSON Array.
            JSONArray moviesJson = responseJson.getJSONArray(JSON_MOVIE_RESULTS);
            if (moviesJson != null) {
                // Go over each JSON object that represents a JSON movie.
                for (int i = 0; i < moviesJson.length(); i++) {
                    JSONObject movieJson = moviesJson.getJSONObject(i);
                    if (movieJson == null) {
                        Log.w(TAG, "Could not get a movie from the JSON Array of movies.");
                        continue;
                    }
                    // Convert the current JSON object into a movie object.
                    Movie currentMovie = getMovieFromJson(movieJson);
                    if (currentMovie == null) {
                        Log.w(TAG, "Could not get a movie from the JSON object at index " + i + ".");
                        continue;
                    }
                    Log.d(TAG, "Added movie " + currentMovie.getId() + " to the array.");
                    // Add the newly parsed movie to the List.
                    movies.add(currentMovie);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Could not parse the JSON provided, message: " + e.getMessage());
            return null;
        }

        return movies;
    }

    private static Movie getMovieFromJson(JSONObject json) {

        if (json == null) {
            String message = "The JSON provided is null, cannot be parsed into a movie.";
            Log.e(TAG, message);
            throw new IllegalArgumentException(message);
        }

        // Check that all the required properties are present;
        if (!json.has(JSON_MOVIE_ID)) {
            Log.w(TAG, "The JSON object does not contain a value for the id.");
            return null;
        }
        if (!json.has(JSON_MOVIE_TITLE)) {
            Log.w(TAG, "The JSON object does not contain a value for the title.");
            return null;
        }
        if (!json.has(JSON_MOVIE_IMAGE_PATH)) {
            Log.w(TAG, "The JSON object does not contain a value for the image path.");
            return null;
        }
        if (!json.has(JSON_MOVIE_BACKDROP_IMAGE_PATH)) {
            Log.w(TAG, "The JSON object does not contain a value for the backdrop image path.");
            return null;
        }
        if (!json.has(JSON_MOVIE_PLOT_SYNOPSIS)) {
            Log.w(TAG, "The JSON object does not contain a value for the overview.");
            return null;
        }
        if (!json.has(JSON_MOVIE_USER_RATING)) {
            Log.w(TAG, "The JSON object does not contain a value for the user rating.");
            return null;
        }
        if (!json.has(JSON_MOVIE_RELEASE_DATE)) {
            Log.w(TAG, "The JSON object does not contain a value for the release date.");
            return null;
        }

        Integer id = null;
        String title = null;
        String imagePath = null;
        String backdropImagePath = null;
        String plotSynopsis = null;
        Double userRating = null;
        String releaseDate = null;

        // Extract all the required properties from JSON to Java.
        try {
            id = json.getInt(JSON_MOVIE_ID);
            title = json.getString(JSON_MOVIE_TITLE);
            imagePath = json.isNull(JSON_MOVIE_IMAGE_PATH) ? null : json.getString(JSON_MOVIE_IMAGE_PATH);
            backdropImagePath = json.isNull(JSON_MOVIE_BACKDROP_IMAGE_PATH) ? null : json.getString(JSON_MOVIE_BACKDROP_IMAGE_PATH);
            plotSynopsis = json.getString(JSON_MOVIE_PLOT_SYNOPSIS);
            userRating = json.getDouble(JSON_MOVIE_USER_RATING);
            releaseDate = json.isNull(JSON_MOVIE_RELEASE_DATE) ? null : json.getString(JSON_MOVIE_RELEASE_DATE);

            // The release date is not always provided, but instead is just an empty string.
            if (releaseDate != null) {
                Log.d(TAG, "The release date is not null, may still not be a valid date.");
                if (releaseDate.equals("")) {
                    releaseDate = null;
                    Log.d(TAG, "The release date did not have a value, converting to null");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Could not convert the JSON into a movie, message: " + e.getMessage());
        }

        Log.v(TAG, "Movie id: '" + id + "'.");
        Log.v(TAG, "Movie title: '" + title + "'.");
        Log.v(TAG, "Movie image path: '" + imagePath + "'.");
        Log.v(TAG, "Movie backdrop image path: '" + backdropImagePath + "'.");
        Log.v(TAG, "Movie plot synopsis: '" + plotSynopsis + "'.");
        Log.v(TAG, "Movie user rating: '" + userRating + "'.");
        Log.v(TAG, "Movie release date: '" + releaseDate + "'.");

        // Combine all the extracted properties into a Movie object.
        return new Movie(id, title, imagePath, backdropImagePath, plotSynopsis, userRating, releaseDate);
    }
}
