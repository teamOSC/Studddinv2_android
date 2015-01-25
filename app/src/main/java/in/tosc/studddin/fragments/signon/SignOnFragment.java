package in.tosc.studddin.fragments.signon;


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
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import in.tosc.studddin.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignOnFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignOnFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View view;
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
        view = inflater.inflate(R.layout.fragment_sign_in, container, false);


        displayInit();

        return view;
    }

    private void displayInit()
    {
        int screenWidth, screenHeight;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
        facebookLoginButton = (Button)view.findViewById(R.id.signon_button_facebook);
        twitterLoginButton = (Button)view.findViewById(R.id.signon_button_twitter);
        googleLoginButton = (Button)view.findViewById(R.id.signon_button_google);
        signUpButton = (Button) view.findViewById(R.id.signon_button_signup);
        guestContinue = (TextView)view.findViewById(R.id.sign_in_guest);

        View.OnClickListener signUpListener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.anim_signin_enter,R.anim.anim_signin_exit);

                SignupDataFragment newFragment = new SignupDataFragment();

                transaction.replace(R.id.signon_container,newFragment).addToBackStack(null).commit();
            }
        };

        View.OnClickListener facebookSignUpListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseFacebookUtils.logIn(getActivity(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        if (user == null) {
                            Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                        } else if (user.isNew()) {
                            Log.d("MyApp", "User signed up and logged in through Facebook!");
                        } else {
                            Log.d("MyApp", "User logged in through Facebook!");
                        }
                    }
                });
            }
        };

        guestContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Put stuff here
            }
        });

        facebookLoginButton.setOnClickListener(facebookSignUpListener);
        twitterLoginButton.setOnClickListener(signUpListener);
        googleLoginButton.setOnClickListener(signUpListener);

        signUpButton.setOnClickListener(signUpListener);


    }


}
