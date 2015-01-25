package in.tosc.studddin.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;

import java.util.HashMap;

import in.tosc.studddin.R;


public class AccountInfoFragment extends Fragment {

    private Button editInfo;
    private EditText eName,ePassword,eEmail,eUid,eQualificaton;//eInterests
    private Button editName,editUid,editEmail,editPassword,editQualification; //editInterest
    View.OnClickListener oclEdit,oclSubmit;
    private View rootView;

    public AccountInfoFragment() {
        // Required empty public constructor
    }

    private static final String USER_NAME = "NAME";
    private static final String USER_UID = "USER NAME";
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
        userInfo = new HashMap<>();
        init();
        fetchInfoFromParse();

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
            userInfo.put(USER_UID,currentUser.getUsername());

            userInfo.put(USER_QUALIFICATIONS,currentUser.getString(USER_QUALIFICATIONS));
//            eQualificaton.setText(userInfo.get(USER_QUALIFICATIONS));

            userInfo.put(USER_INTERESTS,currentUser.getString(USER_INTERESTS));
            eQualificaton.setText(userInfo.get(USER_INTERESTS));
        }
        else
        {
            //TODO: handle errors if any generated
        }
    }

    private void init() {
        editInfo = (Button) rootView.findViewById(R.id.button_edit_info);
        eName = (EditText) rootView.findViewById(R.id.account_info_name);
        editName = (Button) rootView.findViewById(R.id.edit_name_button);

        ePassword = (EditText) rootView.findViewById(R.id.account_info_password);
        editPassword = (Button) rootView.findViewById(R.id.edit_user_name);

        eEmail = (EditText) rootView.findViewById(R.id.account_info_email);
        editEmail = (Button) rootView.findViewById(R.id.edit_email_button);


        eQualificaton = (EditText) rootView.findViewById(R.id.account_info_qualification);
        editQualification = (Button) rootView.findViewById(R.id.edit_qualification_button);

        eUid = (EditText) rootView.findViewById(R.id.account_info_user_name);

        eName.setEnabled(false);
        ePassword.setEnabled(false);
        eQualificaton.setEnabled(false);
        eEmail.setEnabled(false);
        eUid.setEnabled(false);

        oclEdit = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId())
                {
                    case R.id.edit_email_button:
                        eEmail.setEnabled(true);
                        break;
                    case R.id.edit_name_button:
                        eName.setEnabled(true);
                        break;
                    case R.id.edit_qualification_button:
                        eQualificaton.setEnabled(true);
                        break;
                }

                //TODO: change image on click
                //v.setBackground();

                v.setOnClickListener(oclSubmit);
            }
        };

        oclSubmit = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.edit_email_button:
                        changeAttribute(eEmail,USER_EMAIL);
                        break;
                    case R.id.edit_name_button:
                        changeAttribute(eName,USER_NAME);
                        break;
                    case R.id.edit_qualification_button:
                        changeAttribute(eQualificaton,USER_QUALIFICATIONS);
                        break;
                }

                //TODO: change background image
                //v.setDrawable()

                v.setOnClickListener(oclEdit);
            }
        };

        editQualification.setOnClickListener(oclEdit);
        editEmail.setOnClickListener(oclEdit);
        editName.setOnClickListener(oclEdit);


        editInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                eName.setEnabled(true);
                ePassword.setEnabled(true);
                eQualificaton.setEnabled(true);
                eEmail.setEnabled(true);
                eUid.setEnabled(true);
            }
        });
    }

    private void changeAttribute(EditText e,String attr)
    {
        if(attr.equals(USER_EMAIL) || attr.equals(USER_NAME))
        {
            if(e.getText().toString().isEmpty())
            Toast.makeText(getActivity(),attr + "cannot be empty",Toast.LENGTH_LONG);
        }

        ParseUser cu = ParseUser.getCurrentUser();
        cu.put(attr,e.getText().toString());
        cu.saveEventually();
        e.setEnabled(false);
    }
}
