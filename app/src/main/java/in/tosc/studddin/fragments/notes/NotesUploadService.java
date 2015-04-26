package in.tosc.studddin.fragments.notes;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class NotesUploadService extends Service {

    private ArrayList<ParseFile> parseFileList;

    public NotesUploadService() {
    }

    private static byte[] convertToByteArray(String imagePath) {
        File file = new File(imagePath);

        if (file.length() < 10485760) {
            byte[] imageByte = new byte[(int) file.length()];
            try {
                Log.d("Raghav", "File Found");
                FileInputStream fileInputStream = new FileInputStream(file);
                fileInputStream.read(imageByte);

            } catch (FileNotFoundException e) {
                Log.d("Raghav", "File Not Found.");

            } catch (IOException e1) {
                Log.d("Raghav", "Error Reading The File.");

            }
            return imageByte;

        } else {
            return null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final android.os.Handler mHandler = new android.os.Handler();
        parseFileList = new ArrayList<>();
        final String[] imagePaths = intent.getStringArrayExtra("imagePaths");
        final String userName = intent.getStringExtra("userName");
        final String subjectNameString = intent.getStringExtra("subjectName");
        final String branchNameString = intent.getStringExtra("branchName");
        final String topicNameString = intent.getStringExtra("topicName");


        final Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    for (int i = 0; i < imagePaths.length; i++) {
                        byte[] imageToBeUploaded = convertToByteArray(imagePaths[i]);
                        if (imageToBeUploaded == null) {
                            Toast.makeText(getApplicationContext(), "File size beyond limit", Toast.LENGTH_SHORT)
                                    .show();
                        }

                        ParseFile parseFile = new ParseFile("notes_images", imageToBeUploaded);

                        try {
                            parseFile.save();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        parseFileList.add(parseFile);
                    }

                    ParseObject uploadNotes = new ParseObject("Notes");

                    uploadNotes.addAll("notesImages", parseFileList);
                    uploadNotes.put("userName", ParseUser.getCurrentUser().getString("NAME"));
                    uploadNotes.put("subjectName", subjectNameString);
                    uploadNotes.put("topicName", topicNameString);
                    uploadNotes.put("branchName", branchNameString);
                    uploadNotes.put("collegeName", "DTU");

                    try {
                        uploadNotes.save();
                        mHandler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(NotesUploadService.this, "Notes Uploaded Successfully", Toast.LENGTH_LONG).show();
                            }
                        });


                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                } finally {

                }
            }
        };
        t.start();


        return START_STICKY;
        //return super.onStartCommand(intent, flags, startId);

    }
}
