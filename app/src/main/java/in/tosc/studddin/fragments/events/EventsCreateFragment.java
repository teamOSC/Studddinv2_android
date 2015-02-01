package in.tosc.studddin.fragments.events;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.HashMap;

import in.tosc.studddin.R;
import in.tosc.studddin.customview.MaterialEditText;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsCreateFragment extends Fragment {

    Button create;
    View v;
    private HashMap<String, String> events;

    public EventsCreateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        events = new HashMap<>();
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_events_create, container, false);
        create = (Button) v.findViewById(R.id.submit_button);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addInput();
                if (checkIfEmpty()) {
                    pushDataToParse();
                }
            }
        });
        return v;
    }

    public void addInput() {
        events.put("title", ((MaterialEditText) v.findViewById(R.id.event_name)).getText() + "");
        events.put("description", ((MaterialEditText) v.findViewById(R.id.event_description)).getText() + "");
        events.put("type", ((MaterialEditText) v.findViewById(R.id.event_type)).getText() + "");
    }

    private boolean checkIfEmpty() {
        if (events.get("title").isEmpty()) {
            Toast.makeText(getActivity().getApplicationContext(), "Please enter title", Toast.LENGTH_LONG).show();
            return false;
        }
        if (events.get("description").isEmpty()) {
            Toast.makeText(getActivity().getApplicationContext(), "Please enter description", Toast.LENGTH_LONG).show();
            return false;
        }
        if (events.get("type").isEmpty()) {
            Toast.makeText(getActivity().getApplicationContext(), "Please enter type", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void pushDataToParse() {
        ParseObject event = new ParseObject("Events");
        event.put("title", events.get("title"));
        event.put("description", events.get("description"));
        event.put("type", events.get("type"));
        event.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(getActivity().getApplicationContext(),
                        getString(R.string.event_created), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
