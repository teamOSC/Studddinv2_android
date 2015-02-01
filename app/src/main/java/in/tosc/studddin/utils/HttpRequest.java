package in.tosc.studddin.utils;

import android.os.AsyncTask;

/**
 * Created by root on 31/1/15.
 */
public class HttpRequest {
    private String url;

    public void HttpRequest(HttpExecutor callback, String url) {
        this.url = url;
    }

    public void execute(HttpExecutor callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                //TODO: I'll write later
                return null;
            }
        }.execute();
    }
}
