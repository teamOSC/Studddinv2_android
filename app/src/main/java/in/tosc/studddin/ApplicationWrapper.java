package in.tosc.studddin;

import android.app.Application;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;

/**
 * Created by championswimmer on 25/1/15.
 */
public class ApplicationWrapper extends Application {
    public static final boolean LOG_DEBUG = true;
    public static final boolean LOG_INFO = true;

    @Override
    public void onCreate() {
        super.onCreate();
//        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "9nhyJ0OEkfqmGygl44OAYfdFdnapE27d9yj9UI5x", "7pJlc2KZgpFXZHwvoXwVeZUsEtiDoTrtjPM7EGBa");
        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        ParseFacebookUtils.initialize(getResources().getString(R.string.facebook_app_id));
        ParseTwitterUtils.initialize("FfUOeQ5OBuv0qOkdHbfXCrwdk", "xQmFnUSii54eS3iUrl0uIrxfeL4EfIdFc6iyoHUDgSIVGDbauD");
    }

    public static void setCustomTheme(ActionBarActivity actionBarActivity, int primary, int secondary){
        actionBarActivity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(primary));
        if(Build.VERSION.SDK_INT==Build.VERSION_CODES.LOLLIPOP){
            actionBarActivity.getWindow().setNavigationBarColor(secondary);
            actionBarActivity.getWindow().setStatusBarColor(secondary);
        }
    }

}
