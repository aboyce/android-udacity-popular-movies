package uk.ab.popularmovies.entities.executors;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class ApplicationExecutors {

    private static final String TAG = ApplicationExecutors.class.getSimpleName();

    // For singleton instantiation.
    private static final Object LOCK = new Object();

    // The singleton instance.
    private static ApplicationExecutors sInstance;

    private final Executor diskIO;
    private final Executor mainThread;

    private ApplicationExecutors(Executor diskIO, Executor mainThread) {
        this.diskIO = diskIO;
        this.mainThread = mainThread;
    }

    public static ApplicationExecutors getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new ApplicationExecutors(Executors.newSingleThreadExecutor(), new MainThreadExecutor());
            }
        }
        return sInstance;
    }

    public Executor diskIO() {
        return this.diskIO;
    }

    public Executor mainThread() {
        return this.mainThread;
    }

    private static class MainThreadExecutor implements Executor {

        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable runnable) {
            mainThreadHandler.post(runnable);
        }
    }
}
