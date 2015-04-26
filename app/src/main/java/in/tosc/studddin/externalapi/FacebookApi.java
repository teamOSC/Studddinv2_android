package in.tosc.studddin.externalapi;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.tosc.studddin.utils.Utilities;

/**
 * Created by championswimmer on 26/1/15.
 */
public class FacebookApi {
    public static final String APP_ID = "903137443064438";
    private static final String TAG = "FacebookApi";

    public static String PROFILE_URL = "";
    public static String COVER_URL = "";

    public static void getFacebookData(final FbGotDataCallback fgdc) {

        final Bundle b = new Bundle();
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        Log.d(TAG,"" +object.toString());
                        try {
                            if(!object.isNull("cover"))
                                b.putString(ParseTables.Users.COVER,object.getJSONObject("cover").getString("source"));
                            else
                                b.putString(ParseTables.Users.COVER, "");
                            String id = object.getString("id");
                            b.putString(ParseTables.Users.IMAGE,"https://graph.facebook.com/" + id + "/picture??width=300&&height=300");
                            b.putString(ParseTables.Users.NAME, object.getString("name"));
                            b.putString(ParseTables.Users.EMAIL, object.getString("email"));

                            fgdc.gotData(b);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,id,cover,email,birthday");
        request.setParameters(parameters);
        request.executeAsync();
        /*
        LoginClient.Request.newMeRequest(session, new LoginClient.Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser gu, Response response) {
                if (gu != null) {
                    Log.d(TAG, "json = " + response.getGraphObject().getInnerJSONObject().toString());
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
        }).executeAsync();*/
        return;
    }

    public static void getFacebookUserEvents(final FbGotEventDataCallback fgedc) {
        /*
        Bundle bundle = new Bundle();
        bundle.putString("fields", "description,start_time,owner,name,cover");
        Request r = new Request(session, "/me/events", bundle,
                HttpMethod.GET, new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
                try {
                    Log.d(TAG, "facebook response = " + response.getRawResponse());
                    fgedc.gotEventData(response.getGraphObject().getInnerJSONObject().getJSONArray("data"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        r.executeAsync();*/
    }
/*
    public static void getProfilePicture(final FbGotProfilePictureCallback listener) {
        Log.d(TAG, "getting profile picture");
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

    public static Bitmap getProfileAndWait() {
        Bundle bundle = new Bundle();
        bundle.putString("fields", "picture");
        Request request = new Request(session, "me", bundle, HttpMethod.GET);
        Response response = Request.executeAndWait(request);

        try {
            String objectId = response.getGraphObject().getInnerJSONObject().getString("id");
            String sUrl = "https://graph.facebook.com/" + objectId + "/picture??width=300&&height=300";
            Bitmap bitmap = Utilities.downloadBitmap(sUrl);
            return bitmap;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void getCoverPicture(final FbGotCoverPictureCallback listener) {
        Log.d(TAG, "getting cover picture");
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

    public static Bitmap getCoverAndWait() {
        Log.d(TAG, "Getting cover photo");
        Bundle bundle = new Bundle();
        bundle.putString("fields", "cover");

        Response response = Request.executeAndWait(request);
        try {
            Log.d(TAG, "response = " + response.getRawResponse());
            Log.d(TAG, "error = " + response.getError());
            String sUrl = response.getGraphObject().getInnerJSONObject().getJSONObject("cover").getString("source");
            Bitmap bitmap = Utilities.downloadBitmap(sUrl);
            return bitmap;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
*/
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
