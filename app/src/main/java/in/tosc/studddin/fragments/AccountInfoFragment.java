package in.tosc.studddin.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.HashMap;

import in.tosc.studddin.R;


public class AccountInfoFragment extends Fragment {

    private Button editInfo;
    private EditText eName,ePassword,eEmail,eInterests;
    private View rootView;

    public AccountInfoFragment() {
        // Required empty public constructor
    }

    private static final String USER_NAME = "NAME";
    private static final String USER_PASSWORD = "NAME";
    private static final String USER_INSTITUTE = "INSTITUTE";
    private static final String USER_EMAIL = "EMAIL";
    private static final String USER_INTERESTS = "INTERESTS";
    private static final String USER_QUALIFICATIONS = "QUALIFICATIONS";


    private HashMap<String,String> userInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_account_info, container, false);

        init();

        return rootView;
    }


    private void fetchInfoFromParse()
    {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if(currentUser != null)
        {
            userInfo.put(USER_EMAIL,currentUser.getEmail());
            eEmail.setText(userInfo.get(USER_EMAIL));
            userInfo.put(USER_NAME,currentUser.getString(USER_NAME));
            eName.setText(userInfo.get(USER_NAME));
            userInfo.put(USER_INSTITUTE,currentUser.getString(USER_INSTITUTE));


            userInfo.put(USER_QUALIFICATIONS,currentUser.getString(USER_QUALIFICATIONS));

            userInfo.put(USER_INTERESTS,currentUser.getString(USER_INTERESTS));
            eInterests.setText(userInfo.get(USER_INTERESTS));
        }
        else
        {
            //TODO: handle errors if any generated
        }
    }

    private void init()
    {
        editInfo = (Button)rootView.findViewById(R.id.button_edit_info);
        eName = (EditText)rootView.findViewById(R.id.account_info_name);
        ePassword = (EditText)rootView.findViewById(R.id.account_info_password);
        eEmail = (EditText)rootView.findViewById(R.id.account_info_email);
        eInterests = (EditText)rootView.findViewById(R.id.account_info_interests);

        eName.setEnabled(false);
        ePassword.setEnabled(false);
        eInterests.setEnabled(false);
        eEmail.setEnabled(false);

        editInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                eName.setEnabled(true);
                ePassword.setEnabled(true);
                eInterests.setEnabled(true);
                eEmail.setEnabled(true);
            }
        });
    }




}
