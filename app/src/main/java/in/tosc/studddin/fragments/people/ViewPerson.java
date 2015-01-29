package in.tosc.studddin.fragments.people;


        import android.content.Intent;
        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Toast;

        import in.tosc.studddin.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewPerson extends Fragment {

    TextView name , interests , qualifications , distance , institute;
    String sname , sinterests , squalifications , sdistance , sinstitute;



    public ViewPerson() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.view_person, container, false);


        Bundle i = getArguments();
        if (i  != null )
        {
            sname = i.getString("name");
            sinstitute = i.getString("institute");
            sinterests = i.getString("interests");
            squalifications = i.getString("qualifications");
            sdistance = i.getString("distance");
        }

        name = (TextView)rootView.findViewById(R.id.person_name);
        institute = (TextView)rootView.findViewById(R.id.person_institute);
        interests = (TextView)rootView.findViewById(R.id.person_interests);
        qualifications = (TextView)rootView.findViewById(R.id.person_qualifications);
        distance = (TextView)rootView.findViewById(R.id.person_area);

        name.setText(" " + sname);
        interests.setText(" " + sinterests);
        institute.setText(" " + sinstitute);
        qualifications.setText(" " + squalifications);
        distance.setText(" " + sdistance);

        return rootView;

    }


}