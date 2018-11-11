package uk.ab.popularmovies.preferences;

import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

import uk.ab.popularmovies.R;
import uk.ab.popularmovies.entities.MovieSort;

public class TMDbPreferences {

    private static final String TAG = TMDbPreferences.class.getSimpleName();

    // API Configuration Values.
    private static final String API_BASE_URL = "https://api.themoviedb.org";
    private static final String API_VERSION = "3";
    private static final String API_KEY = "api_key";
    private static final String API_DISCOVER = "discover/movie";

    private static final String API_SORT_BY = "sort_by";
    private static final MovieSort API_SORT_BY_DEFAULT = MovieSort.POPULARITY_ASCENDING;

    private static final String API_LANGUAGE = "language";
    private static final String API_LANGUAGE_DEFAULT = "en-GB";

    private static final String API_NSFW = "include_adult";
    private static final String API_NSFW_DEFAULT = "false";

    // This Class should not need to be instantiated.
    private TMDbPreferences() { }

    private static String getApiKey() {
        // FIXME: This is not returning the API Key!!
        String apiKey = null;
        try {
            apiKey = Resources.getSystem().getString(R.string.movie_db_api_key);
            Log.d(TAG, "Located the API Key.");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Could not extract the API Key.");
        }
        return apiKey;
    }

    public static URL getDiscoverURL() {
        return getDiscoverURL(API_SORT_BY_DEFAULT);
    }

    public static URL getDiscoverURL(MovieSort sortOrder) {

        URL discoverUrl = null;
        String apiKey = getApiKey();

        Uri builtUri = Uri.parse(API_BASE_URL + "/" + API_VERSION + "/" + API_DISCOVER)
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
            Log.e(TAG, "Enable to parse the built discover URL");
        }

        // Remove the API for logging purposes.
        String cleanUrl = builtUri.toString().replace(apiKey, "****");
        Log.d(TAG, "Built the discover URL: " + cleanUrl);

        return discoverUrl;
    }
}