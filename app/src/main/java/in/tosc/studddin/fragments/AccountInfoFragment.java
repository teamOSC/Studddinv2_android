package in.tosc.studddin.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.List;

import in.tosc.studddin.R;


public class AccountInfoFragment extends Fragment {

    private EditText ePassword,eQualificaton,eInstitute;//eInterests
    private TextView tEmail,tFullName;
    private ImageButton editPassword,editQualification,editInstitute; //editInterest
    View.OnClickListener oclEdit,oclSubmit;
    private View rootView;
    private LinearLayout passwordContainer;

    public AccountInfoFragment() {
        // Required empty public constructor
    }

    private static final String USER_FULLNAME = "NAME";
    private static final String USER_UID = "USERNAME";
    private static final String USER_PASSWORD = "xxxxxxxxx";
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
            if ( !(currentUser.getEmail().isEmpty()))
                tEmail.setText(currentUser.getEmail());

            if ( !(currentUser.getString(USER_INSTITUTE).isEmpty()) ) {
                eInstitute.setText(currentUser.getString(USER_INSTITUTE));
            }

            if ( !(currentUser.getString(USER_FULLNAME).isEmpty()) )
                tFullName.setText(currentUser.getString(USER_FULLNAME));

            if ( !(currentUser.getString(USER_QUALIFICATIONS).isEmpty()) )
                eQualificaton.setText(currentUser.getString(USER_QUALIFICATIONS));
        }
        else
        {
            //TODO: handle errors if any generated
        }
    }

    private void init() {


        tFullName = (TextView) rootView.findViewById(R.id.account_info_fullname);
        tEmail = (TextView)rootView.findViewById(R.id.account_info_email);

        ePassword = (EditText) rootView.findViewById(R.id.account_info_password);
        editPassword = (ImageButton) rootView.findViewById(R.id.edit_password_button);

        eInstitute = (EditText) rootView.findViewById(R.id.account_info_institute);
        editInstitute = (ImageButton) rootView.findViewById(R.id.edit_institute_button);


        eQualificaton = (EditText) rootView.findViewById(R.id.account_info_qualification);
        editQualification = (ImageButton) rootView.findViewById(R.id.edit_qualification_button);


        passwordContainer = (LinearLayout)rootView.findViewById(R.id.account_info_container_password);

        ePassword.setEnabled(false);
        eQualificaton.setEnabled(false);
        eInstitute.setEnabled(false);


        oclEdit = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId())
                {
                    case R.id.edit_institute_button:
                        eInstitute.setEnabled(true);
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
                    case R.id.edit_institute_button:
                        changeAttribute(eInstitute,USER_EMAIL);
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
        editInstitute.setOnClickListener(oclEdit);

        editPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView newView = new TextView(getActivity());
                newView.setText("new password");
                passwordContainer.addView(newView);
                newView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                passwordContainer.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        });
    }

    private void changeAttribute(EditText e,String attr)
    {
        if(attr.equals(USER_INSTITUTE) || attr.equals(USER_FULLNAME))
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
