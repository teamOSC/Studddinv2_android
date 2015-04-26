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
