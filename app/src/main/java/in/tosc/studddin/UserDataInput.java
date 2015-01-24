package in.tosc.studddin;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserDataInput#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserDataInput extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String name = "name";
    private static final String institute = "institute";
    private static final String email = "email";
    private static final String interests = "interests";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private HashMap<String,String> input;

    private Button submitButton;
    EditText nameET;
    EditText instituteET;
    EditText emailET;
    EditText interestsET;




    public static UserDataInput newInstance() {
        UserDataInput fragment = new UserDataInput();
        return fragment;
    }

    public UserDataInput() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        input = new HashMap<String,String>();
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

        submitButton = (Button) v.findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInput();
                
                boolean f = validateInput();

                if(f)
                {
                    pushInputToParse();
                    startNextActivity();
                }


            }
        });

        return v;
    }

    private void getInput()
    {

//        Log.d("nametext : ",nameET.getText().toString());

            input.put(name, nameET.getText().toString());
            input.put(institute, instituteET.getText().toString());
            input.put(email, emailET.getText().toString());
            input.put(interests, interestsET.getText().toString());

    }

    private boolean validateInput()
    {
        //validate input stored in input
        boolean f = true;
       if(input.get(name).isEmpty())
       {
           Toast.makeText(getActivity(),getActivity().getString(R.string.enter_name),Toast.LENGTH_LONG).show();
           f = false;
       }

       if(f && input.get(institute).isEmpty())
       {
           Toast.makeText(getActivity(),getActivity().getString(R.string.enter_institute),Toast.LENGTH_LONG).show();
           f = false;
       }

       if(f && input.get(email).isEmpty())
       {
           Toast.makeText(getActivity(),getActivity().getString(R.string.enter_email),Toast.LENGTH_LONG).show();
           f = false;
       }
       
       if(f && input.get(interests) == null)
       {
           input.remove(interests);
           input.put(interests, getActivity().getString(R.string.empty_interests));
       }

       //TODO: also make sure a valid email id is entered

       return f;
    }


    private void startNextActivity()
    {
        //get to next activity and set flags etc in shared preferences

    }

    private void pushInputToParse()
    {
        //push the valid input to parse

    }



}
