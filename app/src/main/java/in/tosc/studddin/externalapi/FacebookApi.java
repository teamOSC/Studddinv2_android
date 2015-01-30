package in.tosc.studddin.externalapi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by championswimmer on 26/1/15.
 */
public class FacebookApi {
    public static final String APP_ID = "903137443064438";
    public static String USER_ID = "";

    private static Session session;
    public static Bundle FbDataBundle = new Bundle();
    public static void setSession (Session s) {
        session = s;
    }

    private static final String TAG = "FacebookApi";

    public static void getFacebookData (final FbGotDataCallback fgdc) {
        Request.newMeRequest(session, new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser gu, Response response) {
                if (gu != null) {
                    USER_ID = gu.getId();
                    FbDataBundle.putString(UserDataFields.USER_NAME, gu.getName());
                    FbDataBundle.putString(UserDataFields.USER_USERNAME, gu.getUsername());
                    try {
                        FbDataBundle.putString(UserDataFields.USER_CITY, gu.getLocation().getLocation().getCity());
                        FbDataBundle.putDouble(UserDataFields.USER_LAT, gu.getLocation().getLocation().getLatitude());
                        FbDataBundle.putDouble(UserDataFields.USER_LONG, gu.getLocation().getLocation().getLongitude());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    FbDataBundle.putString(UserDataFields.USER_DOB, gu.getBirthday());
                    fgdc.gotData(FbDataBundle);
                }
            }
        }).executeAsync();
        return;
    }

    public static void getFacebookUserEvents (final FbGotEventDataCallback fgedc) {
        JSONArray jsonArray;
        Request r = new Request(session, "/me/events",null,
                HttpMethod.GET, new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
                try {
                    fgedc.gotEventData(response.getGraphObject().getInnerJSONObject().getJSONArray("data"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        r.executeAsync();
    }

    public static void getProfilePicture (final FbGotProfilePictureCallback listener) {
        Log.d(TAG, "getting profile picture");
        Bundle bundle = new Bundle();
        bundle.putString("fields", "picture");
        new Request(session, "me",bundle,
                HttpMethod.GET, new Request.Callback() {
            @Override
            public void onCompleted(final Response response) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            JSONObject jsonObject = response.getGraphObject().getInnerJSONObject();
                            jsonObject = jsonObject.getJSONObject("picture");
                            jsonObject = jsonObject.getJSONObject("data");
                            URL url = new URL(jsonObject.getString("url"));
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setDoInput(true);
                            connection.connect();
                            InputStream input = connection.getInputStream();
                            Bitmap bitmap = BitmapFactory.decodeStream(input);
                            Log.d(TAG, "picture = " + jsonObject.getString("url"));
                            listener.gotProfilePicture(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute();
            }
        }).executeAsync();
    }

    public interface FbGotDataCallback {
        public void gotData (Bundle b);
    }

    public interface FbGotProfilePictureCallback {
        public void gotProfilePicture (Bitmap profilePicture);
    }

    public interface FbGotEventDataCallback {
        public void gotEventData (JSONArray jArray);
    }


}
