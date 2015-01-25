package in.tosc.studddin.utils;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;

/**
 * Created by omerjerk on 25/1/15.
 */
public class HttpExecute {
    HttpExecutor httpExecutor = null;
    String Url;
    List<NameValuePair> nameValuePairs;

    public HttpExecute(HttpExecutor httpExecutor, String Url) {
        this(httpExecutor, Url, null);
    }

    public HttpExecute(HttpExecutor httpExecutor, String Url, List<NameValuePair> nameValuePairs) {
        this.httpExecutor = httpExecutor;
        this.Url = Url;
        this.nameValuePairs = nameValuePairs;
    }

    public void execute() {
        new ExecuteHttp(Url, nameValuePairs);
    }

    private class ExecuteHttp extends AsyncTask<Void, Void, String> {

        private String Url;
        List<NameValuePair> nameValuePairs;

        public ExecuteHttp(String Url, List<NameValuePair> nameValuePairs) {
            this.Url = Url;
            this.nameValuePairs = nameValuePairs;
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://www.yoursite.com/script.php");

            try {
                if (nameValuePairs != null) {
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                }
                HttpResponse response = httpclient.execute(httppost);
                String result = EntityUtils.toString(response.getEntity());
                return result;
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
            return null;
        }

        @Override
        public void onPostExecute(String result) {
            HttpExecute.this.httpExecutor.onResponse(result);
        }
    }
}
