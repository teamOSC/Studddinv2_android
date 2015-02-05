package in.tosc.studddin.fragments.events;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Calendar;
import java.util.HashMap;

import in.tosc.studddin.R;
import in.tosc.studddin.customview.MaterialEditText;
import in.tosc.studddin.externalapi.ParseTables;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsCreateFragment extends Fragment {

    Button create;
    static View v;
    private static HashMap<String, String> events;
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
                timePickerFragment.show(getActivity().getSupportFragmentManager(), "Set Time");
            }
        });
        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePicker = new DatePickerFragment();
                datePicker.show(getActivity().getSupportFragmentManager(), "Set Date");
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
        events.put(ParseTables.Events.TITLE, ((MaterialEditText) v.findViewById(R.id.event_name)).getText() + "");
        events.put(ParseTables.Events.DESCRIPTION, ((MaterialEditText) v.findViewById(R.id.event_description)).getText() + "");
        events.put(ParseTables.Events.TYPE, ((MaterialEditText) v.findViewById(R.id.event_type)).getText() + "");
        events.put(ParseTables.Events.LOCATION, ((MaterialEditText) v.findViewById(R.id.event_location)).getText() + "");
        events.put(ParseTables.Events.USER, ParseUser.getCurrentUser().getString(ParseTables.Users.USER_NAME));
    }

    private boolean checkIfEmpty() {
        if (events.get(ParseTables.Events.TITLE).isEmpty()) {
            Toast.makeText(getActivity().getApplicationContext(), "Please enter title", Toast.LENGTH_LONG).show();
            return false;
        }
        if (events.get(ParseTables.Events.DESCRIPTION).isEmpty()) {
            Toast.makeText(getActivity().getApplicationContext(), "Please enter description", Toast.LENGTH_LONG).show();
            return false;
        }
        if (events.get(ParseTables.Events.TYPE).isEmpty()) {
            Toast.makeText(getActivity().getApplicationContext(), "Please enter type", Toast.LENGTH_LONG).show();
            return false;
        }
        if (events.get(ParseTables.Events.LOCATION).isEmpty()) {
            Toast.makeText(getActivity().getApplicationContext(), "Please enter location", Toast.LENGTH_LONG).show();
            return false;
        }
        if(!events.containsKey(ParseTables.Events.DATE)){
            Toast.makeText(getActivity().getApplicationContext(), "Please enter date", Toast.LENGTH_LONG).show();
            return false;
        }
        if(!events.containsKey(ParseTables.Events.TIME)){
            Toast.makeText(getActivity().getApplicationContext(), "Please enter time", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void pushDataToParse() {
        ParseObject event = new ParseObject("Events");
        event.put(ParseTables.Events.TITLE, events.get("title"));
        event.put(ParseTables.Events.DESCRIPTION, events.get("description"));
        event.put(ParseTables.Events.TYPE, events.get("type"));
        event.put(ParseTables.Events.LOCATION_DES, events.get("location"));
        event.put(ParseTables.Events.DATE, events.get("date"));
        event.put(ParseTables.Events.TIME, events.get("time"));
        event.put(ParseTables.Events.CREATED_BY, events.get("user"));
        event.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(getActivity().getApplicationContext(),
                        getString(R.string.event_created), Toast.LENGTH_SHORT).show();
            }
        });
    }



    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        @Override
        public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String date = new StringBuilder().append(dayOfMonth).append("/").append(monthOfYear).append("/").append(year).toString();
            events.put(ParseTables.Events.DATE, date);
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

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String time;
            String min = Integer.toString(minute);
            if(minute < 10){
                min = "0" +Integer.toString(minute);
            }
            if(hourOfDay > 12){
                hourOfDay = hourOfDay - 12;
                time = new StringBuilder().append(hourOfDay).append(":").append(min).append(" pm").toString();
            }else {
                time = new StringBuilder().append(hourOfDay).append(":").append(min).append(" am").toString();
            }
            events.put(ParseTables.Events.TIME, time);
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
