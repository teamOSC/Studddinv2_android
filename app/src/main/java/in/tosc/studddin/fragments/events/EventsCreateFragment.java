package in.tosc.studddin.fragments.events;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.Calendar;
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
    Calendar calendar;
    ImageButton setDate;
    ImageButton setTime;

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
        setDate = (ImageButton) v.findViewById(R.id.date_picker);
        setTime = (ImageButton) v.findViewById(R.id.time_picker);
        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getActivity().getFragmentManager(), "Set Time");
            }
        });
        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePicker = new DatePickerFragment();
                datePicker.show(getActivity().getFragmentManager(), "Set Date");
            }
        });
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



    private class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        @Override
        public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String date = new StringBuilder().append(dayOfMonth).append("/").append(monthOfYear).append("/").append(year).toString();
            events.put("date", date);
            ((MaterialEditText)v.findViewById(R.id.event_date)).setText(date);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }
    }

    private class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String time = new StringBuilder().append(hourOfDay).append(":").append(minute).toString();
            events.put("time", time);
            ((MaterialEditText)v.findViewById(R.id.event_time)).setText(time);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        }
    }


}
