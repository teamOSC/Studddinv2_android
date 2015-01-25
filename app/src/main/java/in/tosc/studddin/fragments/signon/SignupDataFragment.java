package in.tosc.studddin.fragments.signon;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.HashMap;
import java.util.UUID;

import in.tosc.studddin.MainActivity;
import in.tosc.studddin.R;


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
    private static final String USER_INSTITUTE = "INSTITUTE";
    private static final String USER_EMAIL = "EMAIL";
    private static final String USER_INTERESTS = "INTERESTS";
    private static final String USER_QUALIFICATIONS = "QUALIFICATIONS";
    EditText nameET;
    EditText instituteET;
    EditText emailET;
    EditText interestsET;
    EditText qualificatonET;
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
        input = new HashMap<String, String>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user_data_input, container, false);

        nameET = (EditText) v.findViewById(R.id.name_input);
        instituteET = (EditText) v.findViewById(R.id.institute_input);
        emailET = (EditText) v.findViewById(R.id.email_input);
        interestsET = (EditText) v.findViewById(R.id.interest_input);
        qualificatonET = (EditText) v.findViewById(R.id.qualification_input);

        submitButton = (Button) v.findViewById(R.id.submit_button);
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

        return v;
    }

    private void getInput() {

//        Log.d("nametext : ",nameET.getText().toString());

        input.put(USER_NAME, nameET.getText().toString());
        input.put(USER_INSTITUTE, instituteET.getText().toString());
        input.put(USER_EMAIL, emailET.getText().toString());
        input.put(USER_INTERESTS, interestsET.getText().toString());
        input.put(USER_QUALIFICATIONS, qualificatonET.getText().toString());


    }

    private boolean validateInput() {
        //validate input stored in input
        boolean f = true;
        if (input.get(USER_NAME).isEmpty()) {
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
        user.setPassword(UUID.randomUUID().toString().substring(1,9));
        user.setEmail(input.get(USER_EMAIL));

// other fields can be set just like with ParseObject
        user.put(USER_NAME, input.get(USER_NAME));
        user.put(USER_INSTITUTE, input.get(USER_INSTITUTE));
        user.put(USER_QUALIFICATIONS, input.get(USER_QUALIFICATIONS));
        user.put(USER_INTERESTS, input.get(USER_INTERESTS));

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                    Intent i = new Intent(getActivity(), MainActivity.class);
                    startActivity(i);
                    getActivity().finish();
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                }
            }
        });

    }


}
