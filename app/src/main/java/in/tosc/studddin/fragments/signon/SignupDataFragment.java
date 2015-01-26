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
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    
    private static final String USER_NAME = "NAME";
    private static final String USER_PASSWORD = "PASSWORD";
    private static final String USER_DOB= "DOB";
    private static final String USER_INSTITUTE = "INSTITUTE";
    private static final String USER_CITY = "CITY";
    private static final String USER_EMAIL = "EMAIL";
    private static final String USER_INTERESTS = "INTERESTS";
    private static final String USER_QUALIFICATIONS = "QUALIFICATIONS";
    private static final String USER_LAT = "LAT";
    private static final String USER_LONG = "LONG";


    View rootView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private HashMap<String, String> input;
    private Button submitButton;


    public SignupDataFragment() {
        // Required empty public constructor
    }

    public static SignupDataFragment newInstance() {
        SignupDataFragment fragment = new SignupDataFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        input = new HashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_sign_up, container, false);


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
                    input.put(USER_LAT, String.valueOf(parseGeoPoint.getLatitude()));
                    input.put(USER_LONG, String.valueOf(parseGeoPoint.getLongitude()));
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

    private void getInput() {
        input.put(USER_NAME, getStringFromEditText(R.id.user_name));
        input.put(USER_PASSWORD, getStringFromEditText(R.id.user_password));
        input.put(USER_DOB, getStringFromEditText(R.id.user_dob));
        input.put(USER_INSTITUTE, getStringFromEditText(R.id.user_institute));
        input.put(USER_CITY, getStringFromEditText(R.id.user_city));
        input.put(USER_EMAIL, getStringFromEditText(R.id.user_email));
        input.put(USER_INTERESTS, getStringFromEditText(R.id.user_interests));
        input.put(USER_QUALIFICATIONS, getStringFromEditText(R.id.user_qualifications));
    }

    private boolean validateInput() {
        //validate input stored in input
        boolean f = true;
        if (input.get(USER_NAME).isEmpty()) {
            Toast.makeText(getActivity(), getActivity().getString(R.string.enter_name), Toast.LENGTH_LONG).show();
            f = false;
        }
        if (input.get(USER_PASSWORD).isEmpty()) {
            Toast.makeText(getActivity(), getActivity().getString(R.string.enter_name), Toast.LENGTH_LONG).show();
            f = false;
        }

        if (f && input.get(USER_INSTITUTE).isEmpty()) {
            Toast.makeText(getActivity(), getActivity().getString(R.string.enter_institute), Toast.LENGTH_LONG).show();
            f = false;
        }

        if (f && input.get(USER_EMAIL).isEmpty()) {
            Toast.makeText(getActivity(), getActivity().getString(R.string.enter_email), Toast.LENGTH_LONG).show();
            f = false;
        }

        if (f && input.get(USER_QUALIFICATIONS).isEmpty()) {
            Toast.makeText(getActivity(), "Please enter qualifications", Toast.LENGTH_LONG).show();
            f = false;
        }

        if (f && input.get(USER_INTERESTS) == null) {
            input.remove(USER_INTERESTS);
            input.put(USER_INTERESTS, getActivity().getString(R.string.empty_interests));
        }

        //TODO: also make sure a valid USER_EMAIL id is entered

        return f;
    }


    private void startNextActivity() {
        //get to next activity and set flags etc in shared preferences

    }

    private void pushInputToParse() {
        //push the valid input to parse
        ParseUser user = new ParseUser();
        user.setUsername(input.get(USER_EMAIL));
        user.setPassword(input.get(USER_PASSWORD));
        user.setEmail(input.get(USER_EMAIL));

        // other fields can be set just like with ParseObject
        user.put(USER_NAME, input.get(USER_NAME));
        user.put(USER_INSTITUTE, input.get(USER_INSTITUTE));
        user.put(USER_QUALIFICATIONS, input.get(USER_QUALIFICATIONS));
        user.put(USER_INTERESTS, input.get(USER_INTERESTS));
        try {
            user.put(USER_LAT, input.get(USER_LAT));
            user.put(USER_LONG, input.get(USER_LONG));
        } catch (Exception e) {
            // Nothing now
        }

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                    Intent i = new Intent(getActivity(), MainActivity.class);
                        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                            Activity activity = getActivity();
                            Bundle options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity).toBundle();
                        activity.getWindow().setExitTransition(new Explode().setDuration(1500));
                            ActivityCompat.startActivityForResult(activity, i, 0,options);
                        }else{
                        startActivity(i);
                    }
                    getActivity().finish();
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                }
            }
        });

    }


}
