package uk.ab.popularmovies.entities.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;

import uk.ab.popularmovies.entities.Movie;

@Database(entities = {Movie.class}, version = 1, exportSchema = false)
@TypeConverters(LocalDateConverter.class)
public abstract class ApplicationDatabase extends RoomDatabase {

    private static final String TAG = ApplicationDatabase.class.getSimpleName();
    private static final String DATABASE_NAME = "popular_movies";

    // For singleton instantiation.
    private static final Object LOCK = new Object();

    // The singleton instance.
    private static ApplicationDatabase sInstance;

    public abstract MovieDao movieDao();

    public static ApplicationDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(TAG, "Create a new database instance.");
                sInstance = Room.databaseBuilder(context.getApplicationContext(), ApplicationDatabase.class, DATABASE_NAME)
                        .build();
                Log.d(TAG, "Created a new database instance '" + DATABASE_NAME + "'.");
            }
        }
        Log.d(TAG, "Returning the database instance '" + DATABASE_NAME + "'.");
        return sInstance;
    }
}