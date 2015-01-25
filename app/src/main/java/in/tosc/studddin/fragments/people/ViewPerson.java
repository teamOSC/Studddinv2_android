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

    TextView name , interests , qualifications , area , institute;



    public ViewPerson() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.view_person, container, false);

        name = (TextView)rootView.findViewById(R.id.person_name);
        institute = (TextView)rootView.findViewById(R.id.person_institute);
        interests = (TextView)rootView.findViewById(R.id.person_interests);
        qualifications = (TextView)rootView.findViewById(R.id.person_qualifications);
        area = (TextView)rootView.findViewById(R.id.person_area);

        return rootView;

    }


}