package in.tosc.studddin.fragments.notes;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;

import in.tosc.studddin.R;

/**
 * Created by raghav on 21/07/15.
 */
public class NotesCustomDialog extends Dialog {


    TextView dialogCollegeName, dialogBranchName, dialogTopicName, dialogSubjectName, dialogUploadedBy;
    Button downloadNotes;
    int position;
    private DownloadManager downloadManager;
    Context c;
    private ArrayList<String> notesCollegeName, notesBranchName, notesTopicName, notesSubjectName, uploadedBy;

    public NotesCustomDialog(Activity activity, ArrayList<String> notesCollegeName,
                             ArrayList<String> notesBranchName, ArrayList<String> notesTopicName,
                             ArrayList<String> notesSubjectName, int position, ArrayList<String> uploadedBy) {
        super(activity);

        this.notesBranchName = notesBranchName;
        this.notesCollegeName = notesCollegeName;
        this.notesSubjectName = notesSubjectName;
        this.notesTopicName = notesTopicName;
        this.c = activity;
        this.position = position;
        this.uploadedBy = uploadedBy;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_notes_custom_dialog);


        dialogBranchName = (TextView) findViewById(R.id.notes_details_branchname);
        dialogSubjectName = (TextView) findViewById(R.id.notes_details_subjectname);
        dialogTopicName = (TextView) findViewById(R.id.notes_details_topicname);
        dialogCollegeName = (TextView) findViewById(R.id.notes_details_collegename);
        dialogUploadedBy = (TextView) findViewById(R.id.notes_details_uploadedby);
        downloadNotes = (Button) findViewById(R.id.notes_button_download);

        downloadManager =  (DownloadManager) this.c.getSystemService(Context.DOWNLOAD_SERVICE);

        dialogUploadedBy.setText(uploadedBy.get(position));
        dialogBranchName.setText(notesBranchName.get(position));
        dialogCollegeName.setText(notesCollegeName.get(position));
        dialogSubjectName.setText(notesSubjectName.get(position));
        dialogTopicName.setText(notesTopicName.get(position));


        downloadNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO write the code to download all the notes


                Log.d("Raghav", "" + position + notesSubjectName.get(position) + notesTopicName.get(position) + notesCollegeName.get(position) +
                        notesBranchName.get(position) );
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Notes");
                query.whereEqualTo("subjectName", notesSubjectName.get(position));
                query.whereEqualTo("topicName", notesTopicName.get(position));
                query.whereEqualTo("collegeName", notesCollegeName.get(position));
                query.whereEqualTo("branchName", notesBranchName.get(position));

                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject notesZipParseObject, ParseException e) {
                        ParseFile notesZipParseFile = (ParseFile) notesZipParseObject.get("imageZip");

                        String zipFileURL = notesZipParseFile.getUrl();
                        Log.d("Raghav", ""+ zipFileURL);
//                        File myFile = new File("/mnt/sdcard/noteszipfile " + notesSubjectName.get(position) +" "+
//                                notesTopicName.get(position) + " "+
//                                notesCollegeName.get(position) + " " +
//                                notesBranchName.get(position) +".zip");
                        Uri uri = Uri.parse(zipFileURL);
                        DownloadManager.Request dr = new DownloadManager.Request(uri);
                        dr.setTitle("Notes: " + notesTopicName.get(position) +" "+
                                notesSubjectName.get(position) + " "+
                                notesBranchName.get(position) +".zip");
                        dr.setDescription("");

                        dr.setDestinationInExternalPublicDir("/LearnHut_Notes/", "noteszipfile " + notesSubjectName.get(position) +" "+
                                notesTopicName.get(position) + " "+
                                notesCollegeName.get(position) + " " +
                                notesBranchName.get(position) +".zip");
                        downloadManager.enqueue(dr);
                        Toast.makeText(getContext(), "Download Start", Toast.LENGTH_SHORT).show();


                    }
                });
                new Thread(new Runnable() {
                    public void run() {
                        while (true) {
                            String unzipLocation = "/mnt/sdcard/LearnHut_Notes/";
                            String zipFile1 = "/mnt/sdcard/noteszipfile.zip";

                            String zipFile = "/mnt/sdcard/LearnHut_Notes/noteszipfile " + notesSubjectName.get(position) +" "+
                                    notesTopicName.get(position) + " "+
                                    notesCollegeName.get(position) + " " +
                                    notesBranchName.get(position) +".zip";
                            NotesUnzip d = new NotesUnzip(zipFile, unzipLocation);
                            d.unzip();
                            Log.i("Raghav", "Unzipping Running parallelly");
                        }
                    }
                }).start();


            }
        });

    }

}
