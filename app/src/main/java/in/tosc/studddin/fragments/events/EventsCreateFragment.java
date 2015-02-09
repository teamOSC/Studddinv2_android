package in.tosc.studddin.fragments.events;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import in.tosc.studddin.R;
import in.tosc.studddin.ui.MaterialEditText;
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
    ImageButton uploadPicture;
    public static byte[] byteArray;
    public static String mCurrentPhotoPath;
    public static ImageView eventImage;

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
        events.put(ParseTables.Events.USER, ParseUser.getCurrentUser().getString(ParseTables.Users.NAME));
        events.put(ParseTables.Events.URL, ((MaterialEditText) v.findViewById(R.id.event_link)).getText() + "");
        events.put(ParseTables.Events.CONTACT, ((MaterialEditText) v.findViewById(R.id.event_contact)).getText() + "");
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
            Drawable drawable = getResources().getDrawable(R.drawable.listing_placeholder);
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
                time = String.valueOf(hourOfDay) + ":" + min + " pm";
            }else {
                time = String.valueOf(hourOfDay) + ":" + min + " am";
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

}
