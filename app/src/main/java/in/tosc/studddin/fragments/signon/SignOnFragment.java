package in.tosc.studddin.fragments.signon;


import android.app.AlertDialog;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.twitter.Twitter;

import java.util.Arrays;
import java.util.List;

import in.tosc.studddin.MainActivity;
import in.tosc.studddin.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignOnFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignOnFragment extends Fragment {

    public static final String TAG = "SignOnFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View rootView;
    private Button facebookLoginButton, twitterLoginButton, googleLoginButton;
    private Button signUpButton, signInButton;
    private TextView guestContinue;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignOnFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignOnFragment newInstance(String param1, String param2) {
        SignOnFragment fragment = new SignOnFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SignOnFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_sign_on, container, false);


        displayInit();

        return rootView;
    }

    private void displayInit()
    {
        int screenWidth, screenHeight;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
        facebookLoginButton = (Button) rootView.findViewById(R.id.signon_button_facebook);
        twitterLoginButton = (Button) rootView.findViewById(R.id.signon_button_twitter);
        googleLoginButton = (Button) rootView.findViewById(R.id.signon_button_google);
        signUpButton = (Button) rootView.findViewById(R.id.signon_button_signup);
        signInButton = (Button) rootView.findViewById(R.id.signon_button_signin);
        guestContinue = (TextView) rootView.findViewById(R.id.sign_in_guest);


        guestContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Put stuff here
            }
        });


        facebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doFacebookSignOn(v);
            }
        });


        twitterLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doTwitterSignOn(v);
            }
        });
        //googleLoginButton.setOnClickListener(signUpListener);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSignUp(v);
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSignIn(v);
            }
        });


    }

    public void doSignIn (View v) {
        ParseUser.logInInBackground(
                ((EditText)rootView.findViewById(R.id.sign_in_user_name)).getText().toString(),
                ((EditText)rootView.findViewById(R.id.sign_in_user_password)).getText().toString(),
                new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (parseUser != null) {
                            Intent i = new Intent(getActivity(), MainActivity.class);
                            startActivity(i);
                        } else {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Login failed")
                                    .setCancelable(true)
                                    .setMessage("Logging in to Studdd.in failed !")
                                    .show();
                        }
                    }
                }
        );
    }
    public void doSignUp (View v) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_signin_enter,R.anim.anim_signin_exit);

        SignupDataFragment newFragment = new SignupDataFragment();

        transaction.replace(R.id.signon_container,newFragment).addToBackStack(null).commit();

    }
    public void doFacebookSignOn (View v) {
        List<String> permissions = Arrays.asList("public_profile", "user_friends",
                ParseFacebookUtils.Permissions.User.ABOUT_ME,
                ParseFacebookUtils.Permissions.User.RELATIONSHIPS,
                ParseFacebookUtils.Permissions.User.BIRTHDAY,
                ParseFacebookUtils.Permissions.User.LOCATION);
        ParseFacebookUtils.logIn(permissions, getActivity(), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                try {
                    Log.d(TAG, "user = " + user.getUsername());
                    Log.d(TAG, "pe = " + err.getCode() + err.getMessage());
                } catch (Exception e) {
                    // Do nothing
                }
                if (user == null) {
                    Log.d(TAG, "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    Log.d(TAG, "User signed up and logged in through Facebook!");
                } else {
                    Log.d(TAG, "User logged in through Facebook!");
                }
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult called");
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }

    public void doTwitterSignOn (View v) {
        ParseTwitterUtils.logIn(getActivity(), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                try {
                    Log.d(TAG, "pe = " + err.getCode() + err.getMessage());
                } catch (Exception e) {
                    // Do nothing
                }
                if (user == null) {
                    Log.d(TAG, "Uh oh. The user cancelled the Twitter login.");
                } else if (user.isNew()) {
                    ParseTwitterUtils.getTwitter().getScreenName();
                    Log.d(TAG, "User signed up and logged in through Twitter!" + ParseTwitterUtils.getTwitter().getScreenName());
                } else {
                    Log.d(TAG, "User logged in through Twitter!");
                }
            }
        });
    }

    public void doGoogleSignOn (View v) {

    }


}
