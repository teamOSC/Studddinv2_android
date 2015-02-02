package in.tosc.studddin.fragments.signon;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import in.tosc.studddin.MainActivity;
import in.tosc.studddin.R;
import in.tosc.studddin.customview.MaterialEditText;
import in.tosc.studddin.externalapi.FacebookApi;
import in.tosc.studddin.externalapi.TwitterApi;
import in.tosc.studddin.externalapi.UserDataFields;
import in.tosc.studddin.utils.FloatingActionButton;
import in.tosc.studddin.utils.Utilities;


/**
 * SignOnFragment
 */
public class SignOnFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = "SignOnFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View rootView;
    private FloatingActionButton facebookLoginButton;
    private FloatingActionButton twitterLoginButton;
    private FloatingActionButton googleLoginButton;
    private Button signUpButton, signInButton;
    private TextView guestContinue;
    private MaterialEditText emailEditText;
    private MaterialEditText passwordEditText;

    private static final int RC_SIGN_IN = 69;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    public static String token;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public SignOnFragment() {
        // Required empty public constructor
    }

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

        ParseUser pUser = ParseUser.getCurrentUser();
        if ((pUser != null)
                && (pUser.isAuthenticated())
                && (pUser.getSessionToken() != null)
                && (pUser.getBoolean(UserDataFields.USER_FULLY_REGISTERED))) {
            Log.d("SignOnFragment", pUser.getUsername() + pUser.getSessionToken());
            Intent i = new Intent(getActivity(), MainActivity.class);
            startActivity(i);

            getActivity().finish();
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

    private void displayInit() {
        facebookLoginButton = (FloatingActionButton) rootView.findViewById(R.id.signon_button_facebook);
        twitterLoginButton = (FloatingActionButton) rootView.findViewById(R.id.signon_button_twitter);
        googleLoginButton = (FloatingActionButton) rootView.findViewById(R.id.signon_button_google);
        signUpButton = (Button) rootView.findViewById(R.id.signon_button_signup);
        signInButton = (Button) rootView.findViewById(R.id.signon_button_signin);
        guestContinue = (TextView) rootView.findViewById(R.id.sign_in_guest);
        emailEditText = (MaterialEditText) rootView.findViewById(R.id.sign_in_user_name);
        passwordEditText = (MaterialEditText) rootView.findViewById(R.id.sign_in_user_password);

        emailEditText.setText(Utilities.getUserEmail(getActivity()));

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
        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doGoogleSignOn(v);
            }
        });
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

    public void doSignIn(View v) {
        ParseUser.logInInBackground(
                emailEditText.getText().toString(),
                passwordEditText.getText().toString(),
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
                                    .setMessage("Logging in to LearnHut failed !")
                                    .show();
                        }
                    }
                }
        );
    }

    public void doSignUp(View v) {
        Bundle b = new Bundle();

        AccountManager am = AccountManager.get(getActivity());
        Account[] accounts = am.getAccountsByType("com.google");
        if (accounts.length > 0)
            b.putString(UserDataFields.USER_EMAIL, accounts[0].name);

        showSignupDataFragment(b);
    }

    public void doFacebookSignOn(View v) {
        List<String> permissions = Arrays.asList("public_profile", "user_friends",
                ParseFacebookUtils.Permissions.User.EMAIL,
                ParseFacebookUtils.Permissions.User.ABOUT_ME,
                ParseFacebookUtils.Permissions.User.RELATIONSHIPS,
                ParseFacebookUtils.Permissions.User.BIRTHDAY,
                ParseFacebookUtils.Permissions.User.LOCATION,
                ParseFacebookUtils.Permissions.User.PHOTOS);
        ParseFacebookUtils.logIn(permissions, getActivity(), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                try {
                    Log.w(TAG, "user = " + user.getUsername());
                    Log.w(TAG, "pe = " + err.getCode() + err.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (user == null) {
                    Log.w(TAG, "Uh oh. The user cancelled the Facebook login.");
                } else {
                    boolean fullyRegistered = false;
                    try {
                        fullyRegistered = user.getBoolean(UserDataFields.USER_FULLY_REGISTERED);
                    } catch (Exception e) {
                    }

                    if (user.isNew() || (!fullyRegistered)) {
                        Log.w(TAG, "User signed up and logged in through Facebook!");

                        Log.w(TAG,
                                "FBSHIT \n" +
                                        ParseFacebookUtils.getSession().getAccessToken() + " \n" +
                                        ParseFacebookUtils.getFacebook().getAppId()
                        );
                        FacebookApi.setSession(ParseFacebookUtils.getSession());
                        FacebookApi.getFacebookData(new FacebookApi.FbGotDataCallback() {
                            @Override
                            public void gotData(final Bundle bundle) {
                                final SignupDataFragment fragment = showSignupDataFragment(bundle);
                                FacebookApi.getProfilePicture(new FacebookApi.FbGotProfilePictureCallback() {
                                    @Override
                                    public void gotProfilePicture(Bitmap profilePicture) {
                                        fragment.bitmapReady = true;
                                        fragment.profileBitmap = profilePicture;
                                        fragment.setProfilePicture();
                                    }
                                });
                                FacebookApi.getCoverPicture(new FacebookApi.FbGotCoverPictureCallback() {
                                    @Override
                                    public void gotCoverPicture(Bitmap coverPicture) {
                                        fragment.setCoverPicture(coverPicture);
                                    }
                                });
                            }
                        });
                    } else {
                        Log.w(TAG, "User logged in through Facebook!");
                        Log.w(TAG,
                                "FBSHIT \n" +
                                        ParseFacebookUtils.getSession().getAccessToken() + " \n" +
                                        ParseFacebookUtils.getSession().getAccessToken() + " \n" +
                                        ParseFacebookUtils.getFacebook().getAppId()
                        );
                        SignupDataFragment.goToMainActivity(getActivity());
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.w(TAG, "onActivityResult called");
        if (requestCode == RC_SIGN_IN) {
            if (resultCode != -1) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }

    public void doTwitterSignOn(View v) {
        ParseTwitterUtils.logIn(getActivity(), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (err != null) {
                    err.printStackTrace();
                    return;
                }
                if (user == null) {
                    Log.w(TAG, "Uh oh. The user cancelled the Twitter login.");
                } else {
                    boolean fullyRegistered = false;
                    try {
                        fullyRegistered = user.getBoolean(UserDataFields.USER_FULLY_REGISTERED);
                    } catch (Exception e) {
                    }

                    if (user.isNew() || (!fullyRegistered)) {
                        Log.w(TAG, "User signed up and logged in through Twitter!" + ParseTwitterUtils.getTwitter().getScreenName());
                        TwitterApi.getTwitterData(new TwitterApi.TwitterDataCallback() {
                            @Override
                            public void gotData(Bundle bundle) {
                                final SignupDataFragment fragment = showSignupDataFragment(bundle);
                                TwitterApi.getUserInfo(new TwitterApi.TwitterInfoCallback() {
                                    @Override
                                    public void gotInfo(JSONObject object, Bitmap profileBitmap, Bitmap coverBitmap) throws JSONException {
                                        fragment.profileBitmap = profileBitmap;
                                        fragment.bitmapReady = true;
                                        fragment.setProfilePicture();
                                        fragment.setCoverPicture(coverBitmap);
                                    }
                                });
                            }
                        });
                    } else {
                        Log.w(TAG, "User logged in through Twitter!");
                        SignupDataFragment.goToMainActivity(getActivity());
                    }
                }
            }
        });
    }

    public void doGoogleSignOn(View v) {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            mGoogleApiClient.connect();
        }
    }

    public SignupDataFragment showSignupDataFragment(Bundle b) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_signin_enter, R.anim.anim_signin_exit);

        SignupDataFragment newFragment = SignupDataFragment.newInstance(b);

        transaction.replace(R.id.signon_container, newFragment).addToBackStack("SignIn").commit();
        return newFragment;
    }

    @Override
    public void onConnected(Bundle bundle) {
        mSignInClicked = false;
        Toast.makeText(getActivity(), "Google+ sign-in successful", Toast.LENGTH_LONG).show();
        final Bundle b = new Bundle();

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    token = GoogleAuthUtil.getToken(
                            getActivity(),
                            Plus.AccountApi.getAccountName(mGoogleApiClient),
                            "oauth2:" + Scopes.PLUS_LOGIN);
                } catch (IOException transientEx) {
                    // Network or server error, try later
                    Log.e(TAG, transientEx.toString());
                } catch (UserRecoverableAuthException e) {
                    Log.e(TAG, e.toString());
                } catch (GoogleAuthException authEx) {
                    Log.e(TAG, authEx.toString());
                }

                return token;
            }

            @Override
            protected void onPostExecute(String token) {
                Log.d(TAG, "Access token retrieved:" + token);
            }
        }.execute();

        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                b.putString(UserDataFields.USER_NAME, currentPerson.getDisplayName());
                b.putString(UserDataFields.USER_EMAIL, Plus.AccountApi.getAccountName(mGoogleApiClient));
                if(currentPerson.getBirthday()!=null){
                    String reverseDate = new StringBuffer(currentPerson.getBirthday()).reverse().toString();
                    b.putString(UserDataFields.USER_DOB, reverseDate);
                }
                SignupDataFragment fragment = showSignupDataFragment(b);
                String profilePictureURL = currentPerson.getImage().getUrl();
                String coverPictureURL = currentPerson.getCover().getCoverPhoto().getUrl();
                new FetchProfilePicture(fragment).execute(profilePictureURL);
                new FetchCoverPicture(fragment).execute(coverPictureURL);
            } else {
                Log.d(TAG,"Person info is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class FetchProfilePicture extends AsyncTask<String, Void, Bitmap> {
        SignupDataFragment fragment;

        public FetchProfilePicture(SignupDataFragment fragment) {
            this.fragment = fragment;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            url = url.substring(0,
                    url.length() - 2)
                    + "400";
            Bitmap bitmap = null;
            bitmap = Utilities.downloadBitmap(url);
            return bitmap;
        }

        protected void onPostExecute(Bitmap bitmap) {
            fragment.profileBitmap = bitmap;
            fragment.bitmapReady = true;
            fragment.setProfilePicture();
        }
    }

    private class FetchCoverPicture extends AsyncTask<String, Void, Bitmap> {
        SignupDataFragment fragment;

        public FetchCoverPicture(SignupDataFragment fragment) {
            this.fragment = fragment;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap bitmap = null;
            bitmap = Utilities.downloadBitmap(url);
            return bitmap;
        }

        protected void onPostExecute(Bitmap bitmap) {
            fragment.setCoverPicture(bitmap);
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!mIntentInProgress) {
            mConnectionResult = result;
            if (mSignInClicked) {
                resolveSignInError();
            }
        }

    }

    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                getActivity().startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }
}
