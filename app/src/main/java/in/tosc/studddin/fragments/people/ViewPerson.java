package in.tosc.studddin.fragments.people;


import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.ParseTwitterUtils;

import org.json.JSONException;
import org.json.JSONObject;

import in.tosc.studddin.R;
import in.tosc.studddin.ui.CircularImageView;
import in.tosc.studddin.ui.FloatingActionButton;
import in.tosc.studddin.utils.Utilities;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewPerson extends Fragment {

    TextView name, interests, qualifications, distance, institute;
    String sname, sinterests, squalifications, sdistance, sinstitute, susername, sauthData;
    CircularImageView pic;
    byte[] data;
    LinearLayout contactsView;

    Button contactButton;
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

        contactButton = (Button) rootView.findViewById(R.id.contactPerson);

        final LinearLayout hiddenLayout = (LinearLayout) rootView.findViewById(R.id.hiddenId);
        final LinearLayout myLayout = (LinearLayout) rootView.findViewById(R.id.contactsLayout);
        final View addView = getActivity().getLayoutInflater().inflate(R.layout.contact_buttons_people, myLayout, false);

        mail = (FloatingActionButton) addView.findViewById(R.id.signon_button_google);
        facebook = (FloatingActionButton) addView.findViewById(R.id.signon_button_facebook);
        twitter = (FloatingActionButton) addView.findViewById(R.id.signon_button_twitter);


        if (susername.contains("@")) {
            mail.setVisibility(View.VISIBLE);
        }
        if (makeMeRequest() && Utilities.isNetworkAvailable(getActivity())) {
            facebook.setVisibility(View.VISIBLE);
        }
        if (ParseTwitterUtils.getTwitter().getUserId() != null) {
            twitter.setVisibility(View.VISIBLE);
        }


        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (hiddenLayout == null) {

                    myLayout.removeAllViews();
                    myLayout.addView(addView);
                } else {
                    myLayout.removeAllViews();
                }
            }
        });

        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent send = new Intent(Intent.ACTION_SENDTO);
                String uriText = "mailto:" + Uri.encode(susername) +
                        "?subject=" + Uri.encode("Connect through LearnHut") +
                        "&body=" + Uri.encode("Hi,\n" +
                        "I saw your profile on LearnHut and I would like to .........");
                Uri uri = Uri.parse(uriText);

                send.setData(uri);
                startActivity(Intent.createChooser(send, "Send mail..."));

            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/" + facebookId));
                    startActivity(intent);
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/" + facebookId)));
                }
            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + ParseTwitterUtils.getTwitter().getUserId())));
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/#!/" + ParseTwitterUtils.getTwitter().getScreenName())));
                }
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

    private boolean makeMeRequest() {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        try {
                            facebook.setVisibility(View.VISIBLE);

                       if(object!=null)
                           facebookId = object.getString("id");

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
        request.executeAsync();
        return false;
    }
}
