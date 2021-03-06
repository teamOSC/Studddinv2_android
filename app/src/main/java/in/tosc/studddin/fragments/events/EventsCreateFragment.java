package in.tosc.studddin.fragments.events;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import in.tosc.studddin.R;
import in.tosc.studddin.externalapi.FacebookApi;
import in.tosc.studddin.externalapi.ParseTables;
import in.tosc.studddin.ui.ProgressBarCircular;
import in.tosc.studddin.utils.Utilities;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsCreateFragment extends Fragment {

    Button create;
    Button facebookEvents;
    static View v;
    private static HashMap<String, String> events;
    ImageButton setDate;
    ImageButton setTime;
    ImageButton uploadPicture;
    public static byte[] byteArray;
    public static String mCurrentPhotoPath;
    public static ImageView eventImage;
    ProgressBarCircular progressBarCircular;

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
        uploadPicture = (ImageButton) v.findViewById(R.id.upload_image);
        facebookEvents = (Button) v.findViewById(R.id.facebook_events);
        progressBarCircular = (ProgressBarCircular) v.findViewById(R.id.upload_progress);
        eventImage = (ImageView)v.findViewById(R.id.event_image);
        uploadPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
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
                progressBarCircular.setBackgroundColor(getResources().getColor(R.color.eventsColorPrimary));
                progressBarCircular.setVisibility(View.VISIBLE);
                create.setClickable(false);
                addInput();
                if (checkIfEmpty()) {
                    pushDataToParse();
                }
            }
        });
        facebookEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //createFbEvent();
            }
        });

        return v;
    }

    public void addInput() {
        events.put(ParseTables.Events.TITLE, ((EditText) v.findViewById(R.id.event_name)).getText() + "");
        events.put(ParseTables.Events.DESCRIPTION, ((EditText) v.findViewById(R.id.event_description)).getText() + "");
        events.put(ParseTables.Events.TYPE, ((EditText) v.findViewById(R.id.event_type)).getText() + "");
        events.put(ParseTables.Events.LOCATION, ((EditText) v.findViewById(R.id.event_location)).getText() + "");
        events.put(ParseTables.Events.USER, ParseUser.getCurrentUser().getString(ParseTables.Users.NAME));
        events.put(ParseTables.Events.URL, ((EditText) v.findViewById(R.id.event_link)).getText() + "");
        events.put(ParseTables.Events.CONTACT, ((EditText) v.findViewById(R.id.event_contact)).getText() + "");
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
        if (events.get(ParseTables.Events.CONTACT).isEmpty()) {
            Toast.makeText(getActivity().getApplicationContext(), "Please enter contact", Toast.LENGTH_LONG).show();
            return false;
        }
        if (events.get(ParseTables.Events.URL).isEmpty()) {
            Toast.makeText(getActivity().getApplicationContext(), "Please enter link", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void pushDataToParse() {
        ParseObject event = new ParseObject("Events");
        if (byteArray == null) {
            Drawable drawable = getResources().getDrawable(R.drawable.listings_placeholder);
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 25, stream);
            byteArray = stream.toByteArray();
        }
        ParseFile file = new ParseFile(ParseTables.Events.EVENT_PNG, byteArray);
        file.saveInBackground();
        event.put(ParseTables.Events.IMAGE, file);
        event.put(ParseTables.Events.TITLE, events.get(ParseTables.Events.TITLE));
        event.put(ParseTables.Events.DESCRIPTION, events.get(ParseTables.Events.DESCRIPTION));
        event.put(ParseTables.Events.TYPE, events.get(ParseTables.Events.TYPE));
        event.put(ParseTables.Events.LOCATION_DES, events.get(ParseTables.Events.LOCATION));
        event.put(ParseTables.Events.DATE, events.get(ParseTables.Events.DATE));
        event.put(ParseTables.Events.TIME, events.get(ParseTables.Events.TIME));
        event.put(ParseTables.Events.CREATED_BY, events.get(ParseTables.Events.USER));
        event.put(ParseTables.Events.URL, events.get(ParseTables.Events.URL));
        event.put(ParseTables.Events.CONTACT, events.get(ParseTables.Events.CONTACT));
        event.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                progressBarCircular.setVisibility(View.GONE);
                create.setClickable(true);
                Toast.makeText(getActivity().getApplicationContext(),
                        getString(R.string.event_created), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        @Override
        public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            monthOfYear++;
            String date = String.valueOf(dayOfMonth) + "/" + monthOfYear + "/" + year;
            events.put(ParseTables.Events.DATE, date);
            ((EditText)v.findViewById(R.id.event_date)).setText(date);
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
                time = String.valueOf(hourOfDay) + ":" + min + " pm";
            }else {
                time = String.valueOf(hourOfDay) + ":" + min + " am";
            }
            events.put(ParseTables.Events.TIME, time);
            ((EditText)v.findViewById(R.id.event_time)).setText(time);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        }

    }

    public void chooseImage(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {

        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(photoFile));
        }

        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        chooser.putExtra(Intent.EXTRA_INTENT, galleryIntent);
        chooser.putExtra(Intent.EXTRA_TITLE, "Upload Events Photo");

        Intent[] intentArray = {cameraIntent};
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
        startActivityForResult(chooser, 0);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void createFbEvent(){

        FacebookApi.getFacebookUserEvents(new FacebookApi.FbGotEventDataCallback() {
            @Override
            public void gotEventData(JSONArray jArray){
                CharSequence[] event_names = new CharSequence[jArray.length()];
                for(int i = 0; i < jArray.length(); i++){
                    try {
                        event_names[i] = jArray.getJSONObject(i).getString("name");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                selectEvent(event_names, jArray);
            }
        });

    }

    public void selectEvent(CharSequence[] charSequences, final JSONArray jsonArray){
        final int[] selectedItem = {-1};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select events");
        builder.setSingleChoiceItems(charSequences, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedItem[0] = which;
            }
        });
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    progressBarCircular.setVisibility(View.VISIBLE);
                    final JSONObject jsonObject = jsonArray.getJSONObject(selectedItem[0]);
                    events.put(ParseTables.Events.TITLE, jsonObject.getString("name"));
                    events.put(ParseTables.Events.DESCRIPTION, jsonObject.getString("description"));
                    events.put(ParseTables.Events.DATE, jsonObject.getString("start_time").substring(0, 10));
                    events.put(ParseTables.Events.TIME, jsonObject.getString("start_time").substring(11, 17));
                    if(jsonObject.has("ticket_uri")) {
                        events.put(ParseTables.Events.URL, jsonArray.getJSONObject(selectedItem[0]).getString("ticket_uri"));
                    } else{
                        events.put(ParseTables.Events.URL, "No url");
                    }
                    if(jsonObject.has("place")) {
                        events.put(ParseTables.Events.LOCATION, jsonObject.getString("place"));
                    } else{
                        events.put(ParseTables.Events.LOCATION, "No location");
                    }
                    events.put(ParseTables.Events.CONTACT, "none");
                    events.put(ParseTables.Events.TYPE, "none");
                    events.put(ParseTables.Events.USER, ParseUser.getCurrentUser().getString(ParseTables.Users.NAME));
                    if(jsonObject.has("cover")) {
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... params) {
                                Bitmap image = null;
                                try {
                                    image = Utilities.downloadBitmap(jsonObject.getJSONObject("cover").getString("source"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                image.compress(Bitmap.CompressFormat.PNG, 25, stream);
                                byteArray = stream.toByteArray();
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                pushDataToParse();
                            }
                        }.execute();
                    }else {
                        pushDataToParse();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.create().show();
    }

}
