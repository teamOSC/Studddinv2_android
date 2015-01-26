package in.tosc.studddin.externalapi;

import android.content.Context;
import android.os.Bundle;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

/**
 * Created by championswimmer on 26/1/15.
 */
public class FbApi {
    public static final String APP_ID = "903137443064438";
    public static String USER_ID = "";

    private static Session session;
    public static Bundle FbDataBundle = new Bundle();
    public static void setSession (Session s) {
        session = s;
    }

    public static void getFacebookData (final FbGotDataCallback fgdc) {
        Request req = Request.newMeRequest(session, new Request.GraphUserCallback() {
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
                        // Bakchodi
                    }
                    FbDataBundle.putString(UserDataFields.USER_DOB, gu.getBirthday());
                    fgdc.gotData(FbDataBundle);
                }
            }
        });
        req.executeAsync();
        return;
    }

    public interface FbGotDataCallback {
        public void gotData (Bundle b);
    }


}
