package in.tosc.studddin;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseConfig;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;

/**
 * Created by championswimmer on 25/1/15.
 */
public class ApplicationWrapper extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "9nhyJ0OEkfqmGygl44OAYfdFdnapE27d9yj9UI5x", "7pJlc2KZgpFXZHwvoXwVeZUsEtiDoTrtjPM7EGBa");
        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        ParseTwitterUtils.initialize("FfUOeQ5OBuv0qOkdHbfXCrwdk", "xQmFnUSii54eS3iUrl0uIrxfeL4EfIdFc6iyoHUDgSIVGDbauD");
    }
}
