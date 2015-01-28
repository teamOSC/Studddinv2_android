package in.tosc.studddin.fragments.events;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import in.tosc.studddin.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsCreateFragment extends Fragment {

    Button create;
    EditText title;
    EditText description;
    EditText date;
    EditText type;

    public EventsCreateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_events_create, container, false);
        create = (Button)v.findViewById(R.id.submit_button);
        title = (EditText)v.findViewById(R.id.event_name);
        description = (EditText)v.findViewById(R.id.event_description);
        type = (EditText)v.findViewById(R.id.event_type);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseObject event = new ParseObject("Events");
                event.put("title", title.getText().toString());
                event.put("description", description.getText().toString());
                event.put("type", type.getText().toString());
                event.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Toast.makeText(getActivity().getApplicationContext(),
                                getString(R.string.event_created), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        return v;
    }




}
