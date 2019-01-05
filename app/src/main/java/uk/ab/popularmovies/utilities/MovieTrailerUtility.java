package uk.ab.popularmovies.utilities;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import uk.ab.popularmovies.entities.MovieTrailer;

public class MovieTrailerUtility {

    private static final String TAG = MovieTrailerUtility.class.getSimpleName();

    private static final String JSON_TRAILER_RESULTS = "results";
    private static final String JSON_TRAILER_ID = "id";
    private static final String JSON_TRAILER_KEY = "key";
    private static final String JSON_TRAILER_NAME = "name";
    private static final String JSON_TRAILER_SITE = "site";
    private static final String JSON_TRAILER_SIZE = "size";
    private static final String JSON_TRAILER_TYPE = "type";

    private static final String TRAILER_SITE_URL_YOUTUBE = "https://www.youtube.com/watch?v=";

    // This Class should not need to be instantiated.
    private MovieTrailerUtility() { }

    public static Uri getMovieTrailerUri(MovieTrailer movieTrailer) {
        // Try and create a URI from the known sources.
        switch (movieTrailer.getSite().toLowerCase()) {
            case "youtube":
                Log.d(TAG, "Movie trailer has been matched with 'youtube'.");
                String uri = (TRAILER_SITE_URL_YOUTUBE + movieTrailer.getKey());
                return Uri.parse(uri);
            default:
                Log.e(TAG, "Could not match the movie trailer site, returning null.");
                return null;
        }
    }

    public static List<MovieTrailer> getMovieTrailersFromJson(String json) {

        List<MovieTrailer> movieTrailers = new ArrayList<>();

        if (json == null || json.equals("")) {
            String message = "The JSON provided is null or empty, cannot be parsed into movie trailers.";
            Log.e(TAG, message);
            throw new IllegalArgumentException(message);
        }

        // The full JSON object to parse.
        JSONObject responseJson;

        try {
            // Convert the full response object into a JSON object.
            responseJson = new JSONObject(json);

            // Extract the array of JSON objects into a JSON Array.
            JSONArray movieTrailersJson = responseJson.getJSONArray(JSON_TRAILER_RESULTS);
            if (movieTrailersJson != null) {
                // Go over each JSON object that represents a JSON movie trailer.
                for (int i = 0; i < movieTrailersJson.length(); i++) {
                    JSONObject movieTrailerJson = movieTrailersJson.getJSONObject(i);
                    if (movieTrailerJson == null) {
                        Log.w(TAG, "Could not get a movie trailer from the JSON Array of movie trailers. ");
                        continue;
                    }
                    // Convert the current JSON object into a movie trailer object.
                    MovieTrailer currentMovieTrailer = getMovieTrailerFromJson(movieTrailerJson);
                    if (currentMovieTrailer == null) {
                        Log.w(TAG, "Could not get a movie trailer from the JSON object at index " + i + ".");
                        continue;
                    }
                    Log.d(TAG, "Added movie trailer " + currentMovieTrailer.getId() + " to the array.");
                    // Add the newly parsed movie trailer to the List.
                    movieTrailers.add(currentMovieTrailer);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Could not parse the JSON provided, message: " + e.getMessage());
            return null;
        }

        return movieTrailers;
    }

    private static MovieTrailer getMovieTrailerFromJson(JSONObject json) {

        if (json == null) {
            String message = "The JSON provided is null, cannot be parsed into a movie trailer.";
            Log.e(TAG, message);
            throw new IllegalArgumentException(message);
        }

        // Check that all the required properties are present;
        if (!json.has(JSON_TRAILER_ID)) {
            Log.w(TAG, "The JSON object does not contain a value for the id.");
            return null;
        }
        if (!json.has(JSON_TRAILER_KEY)) {
            Log.w(TAG, "The JSON object does not contain a value for the key.");
            return null;
        }
        if (!json.has(JSON_TRAILER_NAME)) {
            Log.w(TAG, "The JSON object does not contain a value for the name.");
            return null;
        }
        if (!json.has(JSON_TRAILER_SITE)) {
            Log.w(TAG, "The JSON object does not contain a value for the site.");
            return null;
        }
        if (!json.has(JSON_TRAILER_SIZE)) {
            Log.w(TAG, "The JSON object does not contain a value for the size.");
            return null;
        }
        if (!json.has(JSON_TRAILER_TYPE)) {
            Log.w(TAG, "The JSON object does not contain a value for the type.");
            return null;
        }

        String id = null;
        String key = null;
        String name = null;
        String site = null;
        Integer size = null;
        String type = null;

        // Extract all the required properties from JSON to Java.
        try {
            id = json.getString(JSON_TRAILER_ID);
            key = json.getString(JSON_TRAILER_KEY);
            name = json.getString(JSON_TRAILER_NAME);
            site = json.getString(JSON_TRAILER_SITE);
            size = json.getInt(JSON_TRAILER_SIZE);
            type = json.getString(JSON_TRAILER_TYPE);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Could not convert the JSON into a movie trailer, message: " + e.getMessage());
        }

        Log.v(TAG, "Movie trailer id: '" + id + "'.");
        Log.v(TAG, "Movie trailer key: '" + key + "'.");
        Log.v(TAG, "Movie trailer name: '" + name + "'.");
        Log.v(TAG, "Movie trailer site: '" + site + "'.");
        Log.v(TAG, "Movie trailer size: '" + size + "'.");
        Log.v(TAG, "Movie trailer type: '" + type + "'.");

        // Combine all the extracted properties into a Movie object.
        return new MovieTrailer(id, key, name, site, size, type);
    }
}
