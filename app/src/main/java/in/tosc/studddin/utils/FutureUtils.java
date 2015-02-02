package in.tosc.studddin.utils;

import android.os.AsyncTask;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by omerjerk on 2/2/15.
 */
public class FutureUtils {
    public interface FutureCallback<T> {
        public void execute(T result);
    }

    public static class FutureShit<T> {
        private Callable callable;
        private FutureCallback callback;
        private Future<T> future;

        public FutureShit(Callable callable, FutureCallback callback) {
            this.callable = callable;
            this.callback = callback;
            createExecutorPool();
        }

        public void createExecutorPool() {
            ExecutorService executor = Executors.newFixedThreadPool(3);
            future = executor.submit(callable);
        }

        public void getShitDone() {
            new AsyncTask<Void, Void, T>() {
                @Override
                protected T doInBackground(Void... params) {
                    try {
                        return future.get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(T result) {
                    super.onPostExecute(result);
                    callback.execute(result);
                }
            }.execute();
        }
    }
}
