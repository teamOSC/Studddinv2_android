package in.tosc.studddin.externalapi;

import android.os.AsyncTask;

import com.parse.ParseTwitterUtils;
import com.parse.twitter.Twitter;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by championswimmer on 26/1/15.
 */
public class TwitterApi {
    private static final String infoGetUrl = "https://api.twitter.com/1.1/users/show.json?screen_name=%s";

    public static void getUserInfo(final TwitterInfoCallback callback) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                Twitter twitter = ParseTwitterUtils.getTwitter();
                HttpClient client = new DefaultHttpClient();
                HttpGet verifyGet = new HttpGet(String.format(infoGetUrl, twitter.getScreenName()));
                twitter.signRequest(verifyGet);
                try {
                    HttpResponse response = client.execute(verifyGet);
                    return EntityUtils.toString(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return "";
            }

            @Override
            protected void onPostExecute(String result) {
                try {
                    callback.gotInfo(new JSONObject(result));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    public interface TwitterInfoCallback {
        public void gotInfo(JSONObject object);
    }
}
