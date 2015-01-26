package in.tosc.studddin.fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.List;

import in.tosc.studddin.R;


public class AccountInfoFragment extends Fragment {

    private EditText ePassword,eQualificaton,eInstitute,eNewPassword,eConfirmPassword;//eInterests
    private TextView tEmail,tFullName;
    private ImageButton editPassword,editQualification,editInstitute; //editInterest
    private View.OnClickListener oclEdit,oclSubmit,oclPasswordEdit,oclPasswordSubmit;
    private View rootView;
    private LinearLayout passwordContainer;
    private View newpassFormContainer;

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

        newpassFormContainer = rootView.findViewById(R.id.new_password_form_container);

        eNewPassword = (EditText) rootView.findViewById(R.id.account_info_new_password);
        eConfirmPassword = (EditText) rootView.findViewById(R.id.account_info_confirm_password);


        passwordContainer = (LinearLayout)rootView.findViewById(R.id.account_info_container_password);

        ePassword.setEnabled(false);
        eQualificaton.setEnabled(false);
        eInstitute.setEnabled(false);
        eNewPassword.setEnabled(false);
        eConfirmPassword.setEnabled(false);
        eInstitute.setSelected(false);

        oclEdit = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId())
                {
                    case R.id.edit_institute_button:
                        eInstitute.setSelected(true);
                        eInstitute.setSelection(0,eInstitute.getText().length());
                        eInstitute.setFocusable(true);
                        eInstitute.setEnabled(true);
                        break;

                    case R.id.edit_qualification_button:
                        eQualificaton.setSelected(true);
                        eQualificaton.setFocusable(true);
                        eQualificaton.setEnabled(true);
                        break;
                }

                //TODO: change image on click
                //v.setBackground();
                ImageButton clicked = (ImageButton)rootView.findViewById(v.getId());
                clicked.setImageResource(R.drawable.tick);

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
                ImageButton clicked = (ImageButton)rootView.findViewById(v.getId());
                clicked.setImageResource(R.drawable.pencil);

                v.setOnClickListener(oclEdit);
            }
        };

        editQualification.setOnClickListener(oclEdit);
        editInstitute.setOnClickListener(oclEdit);

        oclPasswordEdit = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ePassword.setEnabled(true);
                eNewPassword.setEnabled(true);
                eConfirmPassword.setEnabled(true);
                ePassword.setFocusable(true);
                ePassword.setFocusableInTouchMode(true);
                ePassword.setHint(getActivity().getString(R.string.old_password));
                slide_down(getActivity(), newpassFormContainer);
                ImageButton clicked = (ImageButton)rootView.findViewById(v.getId());
                clicked.setImageResource(R.drawable.tick);
                editPassword.setOnClickListener(oclPasswordSubmit);
            }
        };

        oclPasswordSubmit = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    ePassword.setFocusable(false);
                    ePassword.setEnabled(false);
                    ePassword.setFocusableInTouchMode(false);
                    eNewPassword.setEnabled(false);
                    eConfirmPassword.setEnabled(false);
                    ePassword.setHint(getActivity().getString(R.string.password));
                    changePassword();
                    slide_up(getActivity(), newpassFormContainer);
                    ImageButton clicked = (ImageButton) rootView.findViewById(v.getId());
                    clicked.setImageResource(R.drawable.pencil);
                    editPassword.setOnClickListener(oclPasswordEdit);

            }
        };

        editPassword.setOnClickListener(oclPasswordEdit);

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
        e.setFocusable(false);
    }

    private static void slide_down(Context context,View v) {
        Animation a = AnimationUtils.loadAnimation(context,R.anim.slide_down);
        if (a != null)
            a.reset();
        if (v != null) {
            v.clearAnimation();
            v.setVisibility(View.VISIBLE);
            v.startAnimation(a);
        }
    }

    private static void slide_up(Context context,View v) {

        Animation a = AnimationUtils.loadAnimation(context,R.anim.slide_up);
        if (a != null)
            a.reset();
        if (v != null) {
            v.clearAnimation();
            v.startAnimation(a);
            v.setVisibility(View.INVISIBLE);
        }
    }

    private boolean changePassword() {
        String oldPassword,newPassword,confirmPassword;


        oldPassword = ePassword.getText().toString();
        newPassword = eNewPassword.getText().toString();
        confirmPassword = eConfirmPassword.getText().toString();

        if(oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {

            Toast.makeText(getActivity(),"Please enter the Old Password and the new password twice.",Toast.LENGTH_LONG).show();
            ePassword.setText("");
            eNewPassword.setText("");
            eConfirmPassword.setText("");
            return false;

        }

        if(!(newPassword.equals(confirmPassword))) {

            Toast.makeText(getActivity(),"New passwords don't match",Toast.LENGTH_LONG).show();

            eNewPassword.setText("");
            eConfirmPassword.setText("");
            return false;
        }

        ParseUser cu = ParseUser.getCurrentUser();
        authenticate(cu.getUsername(), oldPassword, newPassword);
        return true;
    }


    public void authenticate(String username,String oldPassword, final String newPassword) {
        ParseUser.logInInBackground(
                username,oldPassword,
                new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (parseUser != null) {
                            parseUser.setPassword(newPassword);
                            parseUser.saveEventually();
                        } else {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Authentication Failed")
                                    .setCancelable(true)
                                    .setMessage("Old password is Incorrect!! Password not changed.")
                                    .show();
                        }
                    }
                }
        );
    }

}

