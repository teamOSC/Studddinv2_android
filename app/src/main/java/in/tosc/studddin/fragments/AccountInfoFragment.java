package in.tosc.studddin.fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.HashMap;

import in.tosc.studddin.ApplicationWrapper;
import in.tosc.studddin.R;
import in.tosc.studddin.customview.MaterialEditText;
import in.tosc.studddin.externalapi.UserDataFields;


public class AccountInfoFragment extends Fragment {

    private static final String USER_UID = "USERNAME";
    private static final String USER_PASSWORD = "xxxxxxxxx";
    private static final String USER_QUALIFICATIONS = "QUALIFICATIONS";
    private static final String USER_AUTH = "authData";
    private static final String FB_APP_ID = "90313744064438";
    private EditText ePassword, eQualificaton, eInstitute, eNewPassword, eConfirmPassword;//eInterests
    private TextView tEmail, tFullName;
    private ImageButton editPassword, editQualification, editInstitute, canEditInstitute, canEditQualifiacaton, canEditPassword; //editInterest
    private View.OnClickListener oclEdit, oclSubmit, oclCancelEdit, oclPasswordEdit, oclPasswordSubmit, oclCancelPasswordEdit;
    private View rootView;
    private LinearLayout passwordContainer;
    private View newpassFormContainer;
    private ParseImageView imageProfile;
    private Bundle fbParams;
    private HashMap<String, String> userInfo;

    public AccountInfoFragment() {
        // Required empty public constructor
    }

    private static void slide_down(Context context, View v) {
        Animation a = AnimationUtils.loadAnimation(context, R.anim.slide_down);
        if (a != null)
            a.reset();
        if (v != null) {
            v.clearAnimation();
            v.setVisibility(View.VISIBLE);
            v.startAnimation(a);
        }
    }

    private static void slide_up(Context context, View v) {

        Animation a = AnimationUtils.loadAnimation(context, R.anim.slide_up);
        if (a != null)
            a.reset();
        if (v != null) {
            v.clearAnimation();
            v.startAnimation(a);
            v.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_account_info, container, false);
        int p = getActivity().getResources().getColor(R.color.accountColorPrimary);
        int s = getActivity().getResources().getColor(R.color.accountColorPrimaryDark);
        ApplicationWrapper.setCustomTheme((ActionBarActivity) getActivity(), p, s);
        userInfo = new HashMap<>();
        init();
        try {
            fetchInfoFromParse();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rootView;
    }

    private void fetchInfoFromParse() throws Exception {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            if (currentUser.get(UserDataFields.USER_EMAIL) != null)
                tEmail.setText(currentUser.getString(UserDataFields.USER_EMAIL));

            if (currentUser.get(UserDataFields.USER_INSTITUTE) != null) {
                eInstitute.setText(currentUser.getString(UserDataFields.USER_INSTITUTE));
            }

            if (currentUser.get(UserDataFields.USER_NAME) != null)
                tFullName.setText(currentUser.getString(UserDataFields.USER_NAME));

            if (currentUser.get(UserDataFields.USER_QUALIFICATIONS) != null)
                eQualificaton.setText(currentUser.getString(USER_QUALIFICATIONS));
        } else {
            //TODO: handle errors if any generated
        }

        ParseFile profileFile = currentUser.getParseFile(UserDataFields.USER_IMAGE);
        imageProfile.setParseFile(profileFile);
        imageProfile.loadInBackground();
    }

    private void init() {

        tFullName = (TextView) rootView.findViewById(R.id.account_info_fullname);
        tEmail = (TextView) rootView.findViewById(R.id.account_info_email);

        ePassword = (MaterialEditText) rootView.findViewById(R.id.account_info_password);
        editPassword = (ImageButton) rootView.findViewById(R.id.edit_password_button);

        eInstitute = (MaterialEditText) rootView.findViewById(R.id.account_info_institute);
        editInstitute = (ImageButton) rootView.findViewById(R.id.edit_institute_button);

        eQualificaton = (MaterialEditText) rootView.findViewById(R.id.account_info_qualification);
        editQualification = (ImageButton) rootView.findViewById(R.id.edit_qualification_button);

        newpassFormContainer = rootView.findViewById(R.id.new_password_form_container);

        eNewPassword = (MaterialEditText) rootView.findViewById(R.id.account_info_new_password);
        eConfirmPassword = (MaterialEditText) rootView.findViewById(R.id.account_info_confirm_password);


        passwordContainer = (LinearLayout) rootView.findViewById(R.id.account_info_container_password);

        canEditInstitute = (ImageButton) rootView.findViewById(R.id.cancel_edit_institute_button);
        canEditQualifiacaton = (ImageButton) rootView.findViewById(R.id.cancel_edit_qualification_button);
        canEditPassword = (ImageButton) rootView.findViewById(R.id.cancel_edit_password_button);

        ePassword.setEnabled(false);
        eQualificaton.setEnabled(false);
        eInstitute.setEnabled(false);
        eNewPassword.setEnabled(false);
        eConfirmPassword.setEnabled(false);
        eInstitute.setSelected(false);

        imageProfile = (ParseImageView) rootView.findViewById(R.id.account_info_picture);

        oclEdit = new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.edit_institute_button:
                        eInstitute.setSelected(true);
                        eInstitute.setSelection(0, eInstitute.getText().length());
                        eInstitute.setFocusable(true);
                        eInstitute.setEnabled(true);
                        eInstitute.setFocusableInTouchMode(true);
                        canEditInstitute.setVisibility(View.VISIBLE);
                        break;

                    case R.id.edit_qualification_button:
                        eQualificaton.setSelected(true);
                        eQualificaton.setSelection(0, eQualificaton.getText().length());
                        eQualificaton.setFocusable(true);
                        eQualificaton.setEnabled(true);
                        eQualificaton.setFocusableInTouchMode(true);
                        canEditQualifiacaton.setVisibility(View.VISIBLE);

                        break;
                }


                //v.setBackground();
                ((ImageButton) v).setImageResource(R.drawable.tick);

                v.setOnClickListener(oclSubmit);
            }
        };

        oclSubmit = new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.edit_institute_button:
                        changeAttribute(eInstitute, UserDataFields.USER_INSTITUTE);
                        canEditInstitute.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.edit_qualification_button:
                        changeAttribute(eQualificaton, USER_QUALIFICATIONS);
                        canEditQualifiacaton.setVisibility(View.INVISIBLE);
                        break;
                }

                //v.setBackground
                ((ImageButton) v).setImageResource(R.drawable.pencil);

                v.setOnClickListener(oclEdit);
            }
        };

        oclCancelEdit = new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.INVISIBLE);
                switch (v.getId()) {
                    case R.id.cancel_edit_institute_button:
                        editInstitute.setImageResource(R.drawable.pencil);
                        editInstitute.setOnClickListener(oclEdit);
                        eInstitute.setEnabled(false);
                        eInstitute.setFocusable(false);
                        break;
                    case R.id.cancel_edit_qualification_button:
                        editQualification.setImageResource(R.drawable.pencil);
                        editQualification.setOnClickListener(oclEdit);
                        eQualificaton.setEnabled(false);
                        eQualificaton.setFocusable(false);
                        break;
                }
            }
        };


        editQualification.setOnClickListener(oclEdit);
        editInstitute.setOnClickListener(oclEdit);
        canEditQualifiacaton.setOnClickListener(oclCancelEdit);
        canEditInstitute.setOnClickListener(oclCancelEdit);

        oclPasswordEdit = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ePassword.setEnabled(true);
                eNewPassword.setEnabled(true);
                eConfirmPassword.setEnabled(true);
                ePassword.setFocusable(true);
                ePassword.setFocusableInTouchMode(true);
                ePassword.setHint(getActivity().getString(R.string.old_password));
                canEditPassword.setVisibility(View.VISIBLE);
                slide_down(getActivity(), newpassFormContainer);
                ImageButton clicked = (ImageButton) rootView.findViewById(v.getId());
                clicked.setImageResource(R.drawable.tick);
                editPassword.setOnClickListener(oclPasswordSubmit);
            }
        };

        oclPasswordSubmit = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canEditPassword.setVisibility(View.INVISIBLE);
                ePassword.setFocusable(false);
                ePassword.setEnabled(false);
                ePassword.setFocusableInTouchMode(false);
                eNewPassword.setEnabled(false);
                eConfirmPassword.setEnabled(false);
                ePassword.setHint(getActivity().getString(R.string.password));
                changePassword();
                slide_up(getActivity(), newpassFormContainer);
                ((ImageButton) v).setImageResource(R.drawable.pencil);
                editPassword.setOnClickListener(oclPasswordEdit);

            }
        };

        oclCancelPasswordEdit = new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.INVISIBLE);
                editPassword.setImageResource(R.drawable.pencil);
                ePassword.setFocusable(false);
                ePassword.setEnabled(false);
                ePassword.setFocusableInTouchMode(false);
                eNewPassword.setEnabled(false);
                eConfirmPassword.setEnabled(false);
                ePassword.setHint(getActivity().getString(R.string.password));
                slide_up(getActivity(), newpassFormContainer);
                editPassword.setImageResource(R.drawable.pencil);
                editPassword.setOnClickListener(oclPasswordEdit);

            }
        };

        editPassword.setOnClickListener(oclPasswordEdit);
        canEditPassword.setOnClickListener(oclCancelPasswordEdit);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll()
                .penaltyLog()
                .build();
        StrictMode.setThreadPolicy(policy);

    }

    private void changeAttribute(EditText e, final String attr) {
        if (attr.equals(UserDataFields.USER_INSTITUTE) || attr.equals(UserDataFields.USER_NAME)) {
            if (e.getText().toString().isEmpty())
                Toast.makeText(getActivity(), attr + "cannot be empty", Toast.LENGTH_LONG).show();
        }

        ParseUser cu = ParseUser.getCurrentUser();
        cu.put(attr, e.getText().toString());
        cu.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(getActivity(), "Updated " + attr.toLowerCase() + " successfully.", Toast.LENGTH_LONG).show();

            }
        });
        e.setEnabled(false);
        e.setFocusable(false);
    }

    private boolean changePassword() {
        String oldPassword, newPassword, confirmPassword;


        oldPassword = ePassword.getText().toString();
        newPassword = eNewPassword.getText().toString();
        confirmPassword = eConfirmPassword.getText().toString();

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {

            Toast.makeText(getActivity(), "Please enter the Old Password and the new password twice.", Toast.LENGTH_LONG).show();
            ePassword.setText("");
            eNewPassword.setText("");
            eConfirmPassword.setText("");
            return false;

        }

        if (!(newPassword.equals(confirmPassword))) {

            Toast.makeText(getActivity(), "New passwords don't match", Toast.LENGTH_LONG).show();

            eNewPassword.setText("");
            eConfirmPassword.setText("");
            return false;
        }

        ParseUser cu = ParseUser.getCurrentUser();
        authenticate(cu.getUsername(), oldPassword, newPassword);
        return true;
    }


    public void authenticate(String username, String oldPassword, final String newPassword) {
        ParseUser.logInInBackground(
                username, oldPassword,
                new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (parseUser != null) {
                            parseUser.setPassword(newPassword);
                            parseUser.saveEventually(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Toast.makeText(getActivity(), "Updated Password Successfully", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getActivity(), "Unable to update password : " + e.getMessage(),
                                                Toast.LENGTH_LONG);
                                    }
                                }
                            });
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





