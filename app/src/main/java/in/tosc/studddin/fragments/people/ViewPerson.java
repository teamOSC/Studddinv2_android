package in.tosc.studddin.fragments.people;


import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import in.tosc.studddin.R;
import in.tosc.studddin.ui.CircularImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewPerson extends Fragment {

    TextView name, interests, qualifications, distance, institute ;
    String sname, sinterests, squalifications, sdistance, sinstitute , susername, sauthData;
    CircularImageView pic;
    byte[] data;

    Button contactButton ;


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
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(susername.contains("@gmail")){

                }
                else if(susername.contains("facebook")){

                }
                else if(susername.contains("@")){

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


}