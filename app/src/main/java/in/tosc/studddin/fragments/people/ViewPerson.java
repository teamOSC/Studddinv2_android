package in.tosc.studddin.fragments.people;


import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.parse.Parse;
import com.parse.ParseUser;

import in.tosc.studddin.R;
import in.tosc.studddin.ui.CircularImageView;
import in.tosc.studddin.ui.FloatingActionButton;
import in.tosc.studddin.utils.Utilities;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewPerson extends Fragment {

    TextView name, interests, qualifications, distance, institute ;
    String sname, sinterests, squalifications, sdistance, sinstitute , susername, sauthData;
    CircularImageView pic;
    byte[] data;
    LinearLayout contactsView;

    Button contactButton ;
    String facebookId;
    FloatingActionButton mail;
    FloatingActionButton facebook;
    FloatingActionButton twitter;

    public ViewPerson() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.view_person, container, false);


        Bundle i = getArguments();
        if (i != null) {
            sname = i.getString("name");
            sinstitute = i.getString("institute");
            sinterests = i.getString("interests");
            squalifications = i.getString("qualifications");
            susername = i.getString("username");
            sdistance = i.getString("distance");
            sauthData = i.getString("authData");

            data = i.getByteArray("pic");
            Log.e("pic", String.valueOf(data));
        }

        Toast.makeText(getActivity(), sauthData, Toast.LENGTH_LONG).show();

//        contactsView = (LinearLayout)rootView.findViewById(R.id.contactsLayout);

        contactButton = (Button) rootView.findViewById(R.id.contactPerson);
//
//        LayoutInflater layoutInflater =
//                (LayoutInflater) getActivity().getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        final View addView = layoutInflater.inflate(R.layout.contact_buttons_people, null);

        final LinearLayout hiddenLayout = (LinearLayout) rootView.findViewById(R.id.hiddenId);
        final LinearLayout myLayout = (LinearLayout) rootView.findViewById(R.id.contactsLayout);
        final View addView = getActivity().getLayoutInflater().inflate(R.layout.contact_buttons_people, myLayout, false);

        //Check if the Layout already exists




            mail = (FloatingActionButton)addView.findViewById(R.id.signon_button_google);
         facebook = (FloatingActionButton)addView.findViewById(R.id.signon_button_facebook);
         twitter = (FloatingActionButton)addView.findViewById(R.id.signon_button_twitter);



        if(susername.contains("@")){
            mail.setVisibility(View.VISIBLE);
        }
        if(susername.contains("facebook")){
            facebook.setVisibility(View.VISIBLE);
        }


        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (hiddenLayout == null) {

                    myLayout.removeAllViews();
                    myLayout.addView(addView);
                }
                else
                {
                    myLayout.removeAllViews();
                }
                }
        });

        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent()

            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        pic = (CircularImageView) rootView.findViewById(R.id.person_image);
        name = (TextView) rootView.findViewById(R.id.person_name);
        institute = (TextView) rootView.findViewById(R.id.person_institute);
        interests = (TextView) rootView.findViewById(R.id.person_interests);
        qualifications = (TextView) rootView.findViewById(R.id.person_qualifications);
        distance = (TextView) rootView.findViewById(R.id.person_area);

        pic.setImageBitmap(BitmapFactory
                .decodeByteArray(
                        data, 0,
                        data.length));

        name.setText(" " + sname);
        interests.setText(" " + sinterests);
        institute.setText(" " + sinstitute);
        qualifications.setText(" " + squalifications);
        distance.setText(" " + sdistance);

        return rootView;

    }

    private void makeMeRequest(final Session session) {
        Request request = Request.newMeRequest(session,
                new Request.GraphUserCallback() {

                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        // If the response is successful
                        if (session == Session.getActiveSession()) {
                            if (user != null) {
                                facebook.setVisibility(View.VISIBLE);
                                 facebookId = user.getId();
                            }
                        }
                        if (response.getError() != null) {
                            // Handle error
                        }
                    }
                });
        request.executeAsync();
    }

}