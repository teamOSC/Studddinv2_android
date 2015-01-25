package in.tosc.studddin.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import in.tosc.studddin.R;


public class AccountInfoFragment extends Fragment {

    private Button editInfo;
    private EditText eName,ePassword,eEmail,eInterests;
    private View rootView;

    public AccountInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_account_info, container, false);

        init();

        return rootView;
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
