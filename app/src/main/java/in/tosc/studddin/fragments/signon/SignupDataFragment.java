package in.tosc.studddin.fragments.signon;


import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.transition.Explode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.parse.LocationCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.HashMap;

import in.tosc.studddin.MainActivity;
import in.tosc.studddin.R;
import in.tosc.studddin.customview.MaterialEditText;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupDataFragment extends Fragment {


    public class UserDataFields {
        public static final String USER_NAME = "NAME";
        public static final String USER_PASSWORD = "PASSWORD";
        public static final String USER_DOB= "DOB";
        public static final String USER_INSTITUTE = "INSTITUTE";
        public static final String USER_CITY = "CITY";
        public static final String USER_EMAIL = "EMAIL";
        public static final String USER_INTERESTS = "INTERESTS";
        public static final String USER_QUALIFICATIONS = "QUALIFICATIONS";
        public static final String USER_LAT = "LAT";
        public static final String USER_LONG = "LONG";
    }

    Bundle userDataBundle;
    


    View rootView;

    private HashMap<String, String> input;
    private Button submitButton;


    public SignupDataFragment() {
        // Required empty public constructor
    }

    public static SignupDataFragment newInstance(Bundle bundle) {
        SignupDataFragment fragment = new SignupDataFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userDataBundle = getArguments();
        }
        input = new HashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_sign_up, container, false);

        if (userDataBundle != null) {
            autoFillData();
        }

        submitButton = (Button) rootView.findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInput();

                boolean f = validateInput();

                if (f) {
                    pushInputToParse();
                    startNextActivity();
                }


            }
        });
        ParseGeoPoint.getCurrentLocationInBackground(10000, new LocationCallback() {
            @Override
            public void done(ParseGeoPoint parseGeoPoint, ParseException e) {
                if (parseGeoPoint != null) {
                    input.put(UserDataFields.USER_LAT, String.valueOf(parseGeoPoint.getLatitude()));
                    input.put(UserDataFields.USER_LONG, String.valueOf(parseGeoPoint.getLongitude()));
                }
            }
        });

        return rootView;
    }

    private String getStringFromEditText(int id) {
        try {
            return ((MaterialEditText) rootView.findViewById(id)).getText().toString();
        } catch (Exception e) {
            return " ";
        }
    }

    private void setDataToFields (int id, String fieldName) {
        try {
            ((MaterialEditText) rootView.findViewById(id)).setText(userDataBundle.getString(fieldName));
        } catch (Exception e) {

        }
    }

    private void autoFillData() {
        setDataToFields(R.id.user_name, UserDataFields.USER_NAME);
        setDataToFields(R.id.user_dob, UserDataFields.USER_DOB);
        setDataToFields(R.id.user_institute, UserDataFields.USER_INSTITUTE);
        setDataToFields(R.id.user_city, UserDataFields.USER_CITY);
        setDataToFields(R.id.user_email, UserDataFields.USER_EMAIL);
    }

    private void getInput() {
        input.put(UserDataFields.USER_NAME, getStringFromEditText(R.id.user_name));
        input.put(UserDataFields.USER_PASSWORD, getStringFromEditText(R.id.user_password));
        input.put(UserDataFields.USER_DOB, getStringFromEditText(R.id.user_dob));
        input.put(UserDataFields.USER_INSTITUTE, getStringFromEditText(R.id.user_institute));
        input.put(UserDataFields.USER_CITY, getStringFromEditText(R.id.user_city));
        input.put(UserDataFields.USER_EMAIL, getStringFromEditText(R.id.user_email));
        input.put(UserDataFields.USER_INTERESTS, getStringFromEditText(R.id.user_interests));
        input.put(UserDataFields.USER_QUALIFICATIONS, getStringFromEditText(R.id.user_qualifications));
    }

    private boolean validateInput() {
        //validate input stored in input
        boolean f = true;
        if (input.get(UserDataFields.USER_NAME).isEmpty()) {
            Toast.makeText(getActivity(), getActivity().getString(R.string.enter_name), Toast.LENGTH_LONG).show();
            f = false;
        }
        if (input.get(UserDataFields.USER_PASSWORD).isEmpty()) {
            Toast.makeText(getActivity(), getActivity().getString(R.string.enter_name), Toast.LENGTH_LONG).show();
            f = false;
        }

        if (f && input.get(UserDataFields.USER_INSTITUTE).isEmpty()) {
            Toast.makeText(getActivity(), getActivity().getString(R.string.enter_institute), Toast.LENGTH_LONG).show();
            f = false;
        }

        if (f && input.get(UserDataFields.USER_EMAIL).isEmpty()) {
            Toast.makeText(getActivity(), getActivity().getString(R.string.enter_email), Toast.LENGTH_LONG).show();
            f = false;
        }

        if (f && input.get(UserDataFields.USER_QUALIFICATIONS).isEmpty()) {
            Toast.makeText(getActivity(), "Please enter qualifications", Toast.LENGTH_LONG).show();
            f = false;
        }

        if (f && input.get(UserDataFields.USER_INTERESTS) == null) {
            input.remove(UserDataFields.USER_INTERESTS);
            input.put(UserDataFields.USER_INTERESTS, getActivity().getString(R.string.empty_interests));
        }

        //TODO: also make sure a valid USER_EMAIL id is entered

        return f;
    }


    private void startNextActivity() {
        //get to next activity and set flags etc in shared preferences

    }

    private void pushInputToParse() {
        //push the valid input to parse
        ParseUser user = ParseUser.getCurrentUser();
        user.setUsername(input.get(UserDataFields.USER_EMAIL));
        user.setPassword(input.get(UserDataFields.USER_PASSWORD));
        user.setEmail(input.get(UserDataFields.USER_EMAIL));

        // other fields can be set just like with ParseObject
        user.put(UserDataFields.USER_NAME, input.get(UserDataFields.USER_NAME));
        user.put(UserDataFields.USER_INSTITUTE, input.get(UserDataFields.USER_INSTITUTE));
        user.put(UserDataFields.USER_QUALIFICATIONS, input.get(UserDataFields.USER_QUALIFICATIONS));
        user.put(UserDataFields.USER_INTERESTS, input.get(UserDataFields.USER_INTERESTS));
        try {
            user.put(UserDataFields.USER_LAT, input.get(UserDataFields.USER_LAT));
            user.put(UserDataFields.USER_LONG, input.get(UserDataFields.USER_LONG));
        } catch (Exception e) {
            // Nothing now
        }
        if (user.getSessionToken() != null) {
            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        // Hooray! Let them use the app now.
                        goToMainActivity();
                    } else {
                    }
                }
            });
        } else {
            user.signUpInBackground(new SignUpCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        // Hooray! Let them use the app now.
                        goToMainActivity();
                    } else {
                    }
                }
            });
        }




    }

    private void goToMainActivity () {
        Intent i = new Intent(getActivity(), MainActivity.class);
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            Activity activity = getActivity();
            Bundle options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity).toBundle();
            activity.getWindow().setExitTransition(new Explode().setDuration(1500));
            ActivityCompat.startActivityForResult(activity, i, 0,options);
        }else{
            startActivity(i);
        }
        getActivity().finish();}


}
