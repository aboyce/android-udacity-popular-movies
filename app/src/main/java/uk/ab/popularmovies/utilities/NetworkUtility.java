package uk.ab.popularmovies.utilities;

import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtility {

    private static final String TAG = NetworkUtility.class.getSimpleName();

    // This Class should not need to be instantiated.
    private NetworkUtility() {}

    public static String getJSONFromURL(URL url) throws IOException {

        if (url == null) {
            Log.e(TAG, "The provided URL was null");
            return null;
        }

        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try {
            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter("\\A");

            if (scanner.hasNext()) {
                Log.d(TAG, "There is a value to return from the URL.");
                return scanner.next();
            } else {
                Log.w(TAG, "There is not a value to return from the URL.");
                return null;
            }
        } finally {
            connection.disconnect();
        }
    }
}
