package in.tosc.studddin.externalapi;

import android.graphics.Bitmap;
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

import in.tosc.studddin.ApplicationWrapper;
import in.tosc.studddin.utils.Utilities;

/**
 * Created by championswimmer on 26/1/15.
 */
public class FacebookApi {
    public static final boolean DEBUG = ApplicationWrapper.LOG_DEBUG;
    public static final boolean INFO = ApplicationWrapper.LOG_INFO;
    public static final String APP_ID = "903137443064438";
    private static final String TAG = "FacebookApi";
    public static String USER_ID = "";
    public static Bundle FbDataBundle = new Bundle();
    private static Session session;

    public static void setSession(Session s) {
        session = s;
    }

    public static void getFacebookData(final FbGotDataCallback fgdc) {
        Request.newMeRequest(session, new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser gu, Response response) {
                if (gu != null) {
                    if (DEBUG) Log.d(TAG, "json = " + response.getGraphObject().getInnerJSONObject().toString());
                    JSONObject responseObject = response.getGraphObject().getInnerJSONObject();
                    USER_ID = gu.getId();
                    try {
                        FbDataBundle.putString(ParseTables.Users.EMAIL, responseObject.getString("email"));
                        FbDataBundle.putString(ParseTables.Users.NAME, gu.getName());
                        FbDataBundle.putString(ParseTables.Users.USERNAME, gu.getUsername());
                        FbDataBundle.putString(ParseTables.Users.CITY, "" + gu.getLocation().getLocation().getCity());
                    } catch (Exception e) {
//                        e.printStackTrace();
                    }
                    FbDataBundle.putString(ParseTables.Users.DOB, gu.getBirthday());
                    fgdc.gotData(FbDataBundle);
                }
            }
        }).executeAsync();
        return;
    }

    public static void getFacebookUserEvents(final FbGotEventDataCallback fgedc) {
        Bundle bundle = new Bundle();
        bundle.putString("fields", "description,start_time,owner,name,cover");
        Request r = new Request(session, "/me/events", bundle,
                HttpMethod.GET, new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
                try {
                    if (DEBUG) Log.d(TAG, "facebook response = " + response.getRawResponse());
                    fgedc.gotEventData(response.getGraphObject().getInnerJSONObject().getJSONArray("data"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        r.executeAsync();
    }

    public static void getProfilePicture(final FbGotProfilePictureCallback listener) {
        if (DEBUG) Log.d(TAG, "getting profile picture");
        Bundle bundle = new Bundle();
        bundle.putString("fields", "picture");
        new Request(session, "me", bundle,
                HttpMethod.GET, new Request.Callback() {
            @Override
            public void onCompleted(final Response response) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            String objectId = response.getGraphObject().getInnerJSONObject().getString("id");
                            String sUrl = "https://graph.facebook.com/" + objectId + "/picture??width=300&&height=300";
                            Bitmap bitmap = Utilities.downloadBitmap(sUrl);
                            listener.gotProfilePicture(bitmap);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute();
            }
        }).executeAsync();
    }

    public static void getCoverPicture(final FbGotCoverPictureCallback listener) {
        if (DEBUG) Log.d(TAG, "getting cover picture");
        Bundle bundle = new Bundle();
        bundle.putString("fields", "cover");
        new Request(session, "me", bundle,
                HttpMethod.GET, new Request.Callback() {
            @Override
            public void onCompleted(final Response response) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            String sUrl = response.getGraphObject().getInnerJSONObject().getJSONObject("cover").getString("source");
                            Bitmap bitmap = Utilities.downloadBitmap(sUrl);
                            listener.gotCoverPicture(bitmap);
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
        public void gotData(Bundle b);
    }

    public interface FbGotProfilePictureCallback {
        public void gotProfilePicture(Bitmap profilePicture);
    }

    public interface FbGotEventDataCallback {
        public void gotEventData(JSONArray jArray);
    }

    public interface FbGotCoverPictureCallback {
        public void gotCoverPicture(Bitmap coverPicture);
    }
}
