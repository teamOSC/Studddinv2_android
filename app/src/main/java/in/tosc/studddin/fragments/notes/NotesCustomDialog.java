package in.tosc.studddin.fragments.notes;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import in.tosc.studddin.R;

/**
 * Created by raghav on 21/07/15.
 */
public class NotesCustomDialog extends Dialog {


    TextView dialogCollegeName, dialogBranchName, dialogTopicName, dialogSubjectName, dialogUploadedBy;
    Button downloadNotes;
    int position;
    Context c;
    private ArrayList<String> notesCollegeName, notesBranchName, notesTopicName, notesSubjectName;

    public NotesCustomDialog(Activity activity, ArrayList<String> notesCollegeName, ArrayList<String> notesBranchName, ArrayList<String> notesTopicName, ArrayList<String> notesSubjectName) {
        super(activity);

        this.notesBranchName = notesBranchName;
        this.notesCollegeName = notesCollegeName;
        this.notesSubjectName = notesSubjectName;
        this.notesTopicName = notesTopicName;
        this.c = activity;
        this.position = position;
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

        dialogBranchName.setText(notesBranchName.get(position));
        dialogCollegeName.setText(notesCollegeName.get(position));
        dialogSubjectName.setText(notesSubjectName.get(position));
        dialogTopicName.setText(notesTopicName.get(position));


        downloadNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //write the code to download all the notes

            }
        });

    }

}
