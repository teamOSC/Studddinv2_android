package in.tosc.studddin.fragments.signon;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.Typeface;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.parse.FunctionCallback;
import com.parse.LogInCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import in.tosc.studddin.MainActivity;
import in.tosc.studddin.R;
import in.tosc.studddin.externalapi.FacebookApi;
import in.tosc.studddin.externalapi.ParseTables;
import in.tosc.studddin.externalapi.TwitterApi;
import in.tosc.studddin.ui.FloatingActionButton;
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
    private EditText emailEditText;
    private EditText passwordEditText;

    private static final int RC_SIGN_IN = 69;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    public static String token;
    private Typeface signOnFont;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ProgressDialog mProgressDialog;

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
                && (pUser.getBoolean(ParseTables.Users.FULLY_REGISTERED))) {
            Log.d(TAG, pUser.getUsername() + pUser.getSessionToken());
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

        //Initialize the progress dialog which is shown at various times
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setCancelable(false);

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
        emailEditText = (EditText) rootView.findViewById(R.id.sign_in_user_name);
        passwordEditText = (EditText) rootView.findViewById(R.id.sign_in_user_password);
        signOnFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Gotham-Light.ttf");
        emailEditText.setTypeface(signOnFont);
        passwordEditText.setTypeface(signOnFont);

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
        mProgressDialog.setMessage("Signing in...");
        mProgressDialog.show();
        ParseUser.logInInBackground(
                emailEditText.getText().toString(),
                passwordEditText.getText().toString(),
                new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        mProgressDialog.dismiss();
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
            b.putString(ParseTables.Users.EMAIL, accounts[0].name);

        showSignupDataFragment(b);
    }

    public void doFacebookSignOn(View v) {
        mProgressDialog.setMessage("Signing in via Facebook");
        mProgressDialog.show();
        List<String> permissions = Arrays.asList("public_profile", "user_friends",
                ParseFacebookUtils.Permissions.User.EMAIL,
                ParseFacebookUtils.Permissions.User.ABOUT_ME,
                ParseFacebookUtils.Permissions.User.RELATIONSHIPS,
                ParseFacebookUtils.Permissions.User.BIRTHDAY,
                ParseFacebookUtils.Permissions.User.LOCATION,
                ParseFacebookUtils.Permissions.User.EVENTS,
                ParseFacebookUtils.Permissions.User.PHOTOS);
        ParseFacebookUtils.logIn(permissions, getActivity(), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                mProgressDialog.dismiss();
                if (err != null) {
                    Log.w(TAG, "pe = " + err.getCode() + err.getMessage());
                    Toast.makeText(getActivity(), err.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (user == null) {
                    Log.w(TAG, "Uh oh. The user cancelled the Facebook login.");
                } else {
                    boolean fullyRegistered = false;
                    try {
                        fullyRegistered = user.getBoolean(ParseTables.Users.FULLY_REGISTERED);
                    } catch (Exception e) {
                        Log.w(TAG, "could not get fully registered data", e);
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
                                /*
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
                                });*/
                                new PushUserIntoParse().execute(bundle);
                            }
                        });
                    } else {
                        Log.w(TAG, "User logged in through Facebook!");
                        Log.w(TAG,
                                "FB \n" +
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
        mProgressDialog.setMessage("Signing in via Twitter");
        mProgressDialog.show();
        ParseTwitterUtils.logIn(getActivity(), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                mProgressDialog.dismiss();
                if (err != null) {
                    err.printStackTrace();
                    return;
                }
                if (user == null) {
                    Log.w(TAG, "Uh oh. The user cancelled the Twitter login.");
                } else {
                    boolean fullyRegistered = false;
                    try {
                        fullyRegistered = user.getBoolean(ParseTables.Users.FULLY_REGISTERED);
                    } catch (Exception ignored) {
                    }

                    if (user.isNew() || (!fullyRegistered)) {
                        Log.w(TAG, "User signed up and logged in through Twitter!" + ParseTwitterUtils.getTwitter().getScreenName());
                        TwitterApi.getTwitterData(new TwitterApi.TwitterDataCallback() {
                            @Override
                            public void gotData(Bundle bundle) {
//                                final SignupDataFragment fragment = showSignupDataFragment(bundle);
                                new PushUserIntoParse().execute(bundle);
                                /*
                                TwitterApi.getUserInfo(new TwitterApi.TwitterInfoCallback() {
                                    @Override
                                    public void gotInfo(JSONObject object, Bitmap profileBitmap, Bitmap coverBitmap) throws JSONException {
                                        fragment.profileBitmap = profileBitmap;
                                        fragment.bitmapReady = true;
                                        fragment.setProfilePicture();
                                        fragment.setCoverPicture(coverBitmap);
                                    }
                                });*/
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
                } catch (IOException | GoogleAuthException transientEx) {
                    // Network or server error, try later
                    Log.e(TAG, transientEx.toString());
                }

                return token;
            }

            @Override
            protected void onPostExecute(String token) {
                Log.d(TAG, "Access token retrieved:" + token);
                final HashMap<String, Object> params = new HashMap<>();
                params.put("code", token);
                params.put("email", Plus.AccountApi.getAccountName(mGoogleApiClient));
                ParseCloud.callFunctionInBackground("accessGoogleUser", params, new FunctionCallback<Object>() {
                    @Override
                    public void done(Object returnObj, ParseException e) {
                        if (e == null) {
                            ParseUser.becomeInBackground(returnObj.toString(), new LogInCallback() {
                                public void done(ParseUser user, ParseException e) {
                                    if (user != null && e == null) {
                                        Log.i(TAG, "Google + user validated");
                                        boolean fullyRegistered = false;
                                        try {
                                            fullyRegistered = user.getBoolean(ParseTables.Users.FULLY_REGISTERED);
                                        } catch (Exception ignored) {
                                        }
                                        if (user.isNew() || (!fullyRegistered)) {
                                            try {
                                                if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                                                    Person currentPerson = Plus.PeopleApi
                                                            .getCurrentPerson(mGoogleApiClient);
                                                    b.putString(ParseTables.Users.NAME, currentPerson.getDisplayName());
                                                    b.putString(ParseTables.Users.EMAIL, Plus.AccountApi.getAccountName(mGoogleApiClient));
                                                    if (currentPerson.getBirthday() != null) {
                                                        String reverseDate = new StringBuffer(currentPerson.getBirthday()).reverse().toString();
                                                        b.putString(ParseTables.Users.DOB, reverseDate);
                                                    }
                                                    new PushUserIntoParse().execute(b);
                                                }
                                            } catch (Exception ex) {
                                                ex.printStackTrace();
                                            }


                                            /*

                                            SignupDataFragment fragment = null;
                                            String profilePictureURL = null;
                                            String coverPictureURL = null;

                                            try {
                                                if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                                                    Person currentPerson = Plus.PeopleApi
                                                            .getCurrentPerson(mGoogleApiClient);
                                                    b.putString(ParseTables.Users.NAME, currentPerson.getDisplayName());
                                                    b.putString(ParseTables.Users.EMAIL, Plus.AccountApi.getAccountName(mGoogleApiClient));
                                                    if (currentPerson.getBirthday() != null) {
                                                        String reverseDate = new StringBuffer(currentPerson.getBirthday()).reverse().toString();
                                                        b.putString(ParseTables.Users.DOB, reverseDate);
                                                    }
                                                    fragment = showSignupDataFragment(b);
                                                    profilePictureURL = currentPerson.getImage().getUrl();
                                                    coverPictureURL = currentPerson.getCover().getCoverPhoto().getUrl();
                                                } else {
                                                    Log.d(TAG, "Person info is null");
                                                }
                                            } catch (Exception exception) {
                                                exception.printStackTrace();
                                            } finally {
                                                if(profilePictureURL!=null)
                                                    new FetchProfilePicture(fragment).execute(profilePictureURL);
                                                if(coverPictureURL!=null)
                                                    new FetchCoverPicture(fragment).execute(coverPictureURL);
                                            }

                                            */

                                        } else {
                                            Log.w(TAG, "User logged in through G Plus");
                                            SignupDataFragment.goToMainActivity(getActivity());
                                        }
                                    } else if (e != null) {
                                        e.printStackTrace();
                                        mGoogleApiClient.disconnect();
                                    } else
                                        Log.i(TAG, "The Google token could not be validated");
                                }
                            });
                        } else {
                            e.printStackTrace();
                            mGoogleApiClient.disconnect();
                        }
                    }
                });
            }
        }.execute();
    }

    private class PushUserIntoParse extends AsyncTask<Bundle, Void, Bundle> {

        @Override
        protected void onPreExecute() {
            mProgressDialog.setMessage("Signing up...");
            mProgressDialog.show();
        }

        @Override
        protected Bundle doInBackground(Bundle... bundles) {
            Bundle bundle = bundles[0];
            ParseUser currentUser = ParseUser.getCurrentUser();
            if (bundle.getString(ParseTables.Users.NAME) != null) {
                currentUser.put(ParseTables.Users.NAME, bundle.getString(ParseTables.Users.NAME));
            }
            if (bundle.getString(ParseTables.Users.EMAIL) != null) {
                currentUser.put(ParseTables.Users.EMAIL, bundle.getString(ParseTables.Users.EMAIL));
                currentUser.setUsername(bundle.getString(ParseTables.Users.EMAIL));
            }
            if (bundle.getString(ParseTables.Users.DOB) != null) {
                currentUser.put(ParseTables.Users.DOB, bundle.getString(ParseTables.Users.DOB));
            }
            try {
                if (currentUser.getSessionToken() != null) {
                    currentUser.save();
                } else {
                    currentUser.setPassword("todoGenerateARandomString");
                    currentUser.signUp();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return bundle;
        }

        @Override
        protected void onPostExecute(Bundle b) {
            mProgressDialog.dismiss();
            showInterestFragment(b);
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
            Bitmap bitmap;
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
            Bitmap bitmap;
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

    private void showInterestFragment(Bundle bundle) {
        ItemSelectorFragment fragment = InterestSelectorFragment.newInstance(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_signin_enter, R.anim.anim_signin_exit);

        transaction.replace(R.id.signon_container, fragment).addToBackStack("SignIn").commit();
    }

    public SignupDataFragment showSignupDataFragment(Bundle b) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_signin_enter, R.anim.anim_signin_exit);

        SignupDataFragment newFragment = SignupDataFragment.newInstance(b);

        transaction.replace(R.id.signon_container, newFragment).addToBackStack("SignIn").commit();
        return newFragment;
    }
}
