package in.tosc.studddin.fragments.signon;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.transition.Explode;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.parse.LocationCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import in.tosc.studddin.MainActivity;
import in.tosc.studddin.R;
import in.tosc.studddin.customview.MaterialEditText;
import in.tosc.studddin.externalapi.UserDataFields;

/**
 * SignupDataFragment
 */
public class SignupDataFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "SignupDataFragment";

    Bundle userDataBundle;

    View rootView;

    private HashMap<String, String> input;
    private SparseArray<EditText> editTextArray = new SparseArray<>();
    private Button submitButton;

    private ImageView profileImageView;

    public boolean viewReady = false, bitmapReady = false;
    public Bitmap profileBitmap;

    private GoogleApiClient mGoogleApiClient;
    private Location currentUserLoc;

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

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

        currentUserLoc = new Location("");

        ParseGeoPoint.getCurrentLocationInBackground(6000, new LocationCallback() {
            @Override
            public void done(ParseGeoPoint parseGeoPoint, ParseException e) {
                if (parseGeoPoint != null) {
                    currentUserLoc.setLatitude(parseGeoPoint.getLatitude());
                    currentUserLoc.setLongitude(parseGeoPoint.getLongitude());
                }
            }
        });

        Button locationEditText = (Button) rootView.findViewById(R.id.user_location);
        locationEditText.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationSelectDialog dialog = new LocationSelectDialog();
                if (currentUserLoc != null) {
                    Bundle b = new Bundle();
                    b.putDouble("lat", currentUserLoc.getLatitude());
                    b.putDouble("lon", currentUserLoc.getLongitude());
                    dialog.setArguments(b);
                }
                dialog.setLocationSetCallback(new LocationSelectDialog.LocationSetCallback() {
                    @Override
                    public void gotLocation(LatLng latLng) {
                        currentUserLoc.setLatitude(latLng.latitude);
                        currentUserLoc.setLongitude(latLng.longitude);
                    }
                });
                dialog.show(getChildFragmentManager(), "LocationSelectDialog");
//                dialog.renderMap();
            }
        });
        profileImageView = (ImageView) rootView.findViewById(R.id.sign_up_profile_picture);

        initializeEditTexts(R.id.user_name);
        initializeEditTexts(R.id.user_password);
        initializeEditTexts(R.id.user_dob);
        initializeEditTexts(R.id.user_institute);
        initializeEditTexts(R.id.user_email);
        initializeEditTexts(R.id.user_interests);
        initializeEditTexts(R.id.user_qualifications);

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
                    try {
                        pushInputToParse();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    startNextActivity();
                }
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        viewReady = true;
        setProfilePicture();
        return rootView;
    }

    private String getStringFromEditText(int id) {
        try {
            return editTextArray.get(id).getText().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return " ";
        }
    }

    private void setDataToFields (int id, String fieldName) {
        try {
            editTextArray.get(id).setText(userDataBundle.getString(fieldName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void autoFillData() {
        setDataToFields(R.id.user_name, UserDataFields.USER_NAME);
        setDataToFields(R.id.user_dob, UserDataFields.USER_DOB);
        setDataToFields(R.id.user_institute, UserDataFields.USER_INSTITUTE);
        setDataToFields(R.id.user_email, UserDataFields.USER_EMAIL);
    }

    private void initializeEditTexts(int id) {
        EditText mEditText = (EditText) rootView.findViewById(id);
        if (mEditText == null) {
            Log.e(TAG, "edit text is null");
        }
        editTextArray.put(id, mEditText);
    }

    private void getInput() {
        input.put(UserDataFields.USER_NAME, getStringFromEditText(R.id.user_name));
        input.put(UserDataFields.USER_PASSWORD, getStringFromEditText(R.id.user_password));
        input.put(UserDataFields.USER_DOB, getStringFromEditText(R.id.user_dob));
        input.put(UserDataFields.USER_INSTITUTE, getStringFromEditText(R.id.user_institute));
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
            Toast.makeText(getActivity(), getActivity().getString(R.string.enter_email),
                    Toast.LENGTH_LONG).show();
            f = false;
        }

        if (f && input.get(UserDataFields.USER_QUALIFICATIONS).isEmpty()) {
            Toast.makeText(getActivity(), getString(R.string.enter_qualifications),
                    Toast.LENGTH_LONG).show();
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

    private void pushInputToParse() throws ParseException {
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
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        profileBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        ParseFile profile = new ParseFile("profilePicture.png", stream.toByteArray());
        profile.save();
        user.put(UserDataFields.USER_IMAGE, profile);
        if (currentUserLoc != null) {
            ParseGeoPoint geoPoint = new ParseGeoPoint(currentUserLoc.getLatitude(), currentUserLoc.getLongitude());
            user.put(UserDataFields.USER_LOCATION, geoPoint);
        }

        user.put(UserDataFields.USER_FULLY_REGISTERED, true);

        if (user.getSessionToken() != null) {
            Log.d(TAG, "saving user in background");
            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        // Hooray! Let them use the app now.
                        goToMainActivity(getActivity());
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Log.d(TAG, "signing up user in background");
            user.signUpInBackground(new SignUpCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        // Hooray! Let them use the app now.
                        goToMainActivity(getActivity());
                    } else {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void setProfilePicture() {
        if (viewReady && bitmapReady) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                        profileImageView.setImageBitmap(profileBitmap);
                }
            });
        }
    }

    public static void goToMainActivity (Activity act) {
        Intent i = new Intent(act, MainActivity.class);
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            Activity activity = act;
            Bundle options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity).toBundle();
            activity.getWindow().setExitTransition(new Explode().setDuration(1500));
            ActivityCompat.startActivityForResult(activity, i, 0,options);
        }else{
            act.startActivity(i);
        }
        act.finish();
    }

    @Override
    public void onConnected(Bundle bundle) {
        currentUserLoc = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }
}
