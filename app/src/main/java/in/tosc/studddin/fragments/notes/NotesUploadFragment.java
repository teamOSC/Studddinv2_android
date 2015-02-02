package in.tosc.studddin.fragments.notes;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.luminous.pick.Action;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import in.tosc.studddin.R;
import in.tosc.studddin.customview.MaterialEditText;
import in.tosc.studddin.utils.FloatingActionButton;
import in.tosc.studddin.utils.MakeZip;

/**
 * NotesUploadFragment
 */
public class NotesUploadFragment extends Fragment {


    static byte[] byteArray;
    static String[] imagePaths = new String[0];
    String zipFileName = "/mnt/sdcard/noteszipfile.zip";
    private Button attachButton;
    private FloatingActionButton uploadButton;
    private EditText topicNameEdTxt, branchNameEdTxt, subjectNameEdTxt;
    private String topicNameString = "", branchNameString = "", subjectNameString = "";

    public NotesUploadFragment() {

        // Required empty public constructor

    }

    public void setImagePaths(String[] paths, Boolean isSelected) {
        if (isSelected)
            Toast.makeText(getActivity(), getString(R.string.notes_files_selected), Toast.LENGTH_SHORT)
                    .show();

        else
            Toast.makeText(getActivity(), getString(R.string.notes_files_not_selected), Toast.LENGTH_SHORT)
                    .show();

        imagePaths = paths;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String[] paths = data.getStringArrayExtra("all_path");
        Log.d("Raghav", "Req = " + requestCode + "res = " + resultCode + "NotesUploadFragment");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_notes_upload, container, false);
        //Log.d("Raghav", "ID = " + getTag());

        attachButton = (Button) rootView.findViewById(R.id.notes_attach);
        uploadButton = (FloatingActionButton) rootView.findViewById(R.id.notes_upload);
        topicNameEdTxt = (MaterialEditText) rootView.findViewById(R.id.notes_topic);
        branchNameEdTxt = (MaterialEditText) rootView.findViewById(R.id.notes_branch);
        subjectNameEdTxt = (MaterialEditText) rootView.findViewById(R.id.notes_subject);

        attachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
                startActivityForResult(i, 5);

            }

        });


        uploadButton = (FloatingActionButton) rootView.findViewById(R.id.notes_upload);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topicNameString = topicNameEdTxt.getText().toString();
                branchNameString = branchNameEdTxt.getText().toString();
                subjectNameString = subjectNameEdTxt.getText().toString();


                if (topicNameString.length() < 1) {
                    Toast.makeText(getActivity(), getString(R.string.enter_topic_name),
                            Toast.LENGTH_SHORT).show();
                } else if (branchNameString.length() < 1) {
                    Toast.makeText(getActivity(), getString(R.string.enter_subject_name),
                            Toast.LENGTH_SHORT).show();
                } else if (subjectNameString.length() < 1) {
                    Toast.makeText(getActivity(), getString(R.string.enter_branch_name),
                            Toast.LENGTH_SHORT).show();

                } else {
                    if (imagePaths.length != 0) {

                        MakeZip makeZip = new MakeZip(imagePaths, zipFileName);

                        makeZip.zip();

                        File file = new File("/mnt/sdcard/noteszipfile.zip");

                        if(file.length() < 10485760) {
                            byte[] zipByte = new byte[(int) file.length()];
                            try {
                                Log.d("Raghav", "File Found");
                                FileInputStream fileInputStream = new FileInputStream(file);
                                fileInputStream.read(zipByte);

                            } catch (FileNotFoundException e) {
                                Log.d("Raghav", "File Not Found.");

                            } catch (IOException e1) {
                                Log.d("Raghav", "Error Reading The File.");

                            }

                            ParseFile parseFile = new ParseFile("notes.zip", zipByte);
                            parseFile.saveInBackground();

                            final ProgressDialog notesUploadProgress = new ProgressDialog(getActivity());
                            notesUploadProgress.setMessage(getString(R.string.notes_uploading));
                            notesUploadProgress.setCancelable(true);
                            notesUploadProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            notesUploadProgress.show();


                            ParseObject uploadNotes = new ParseObject("Notes");

                            uploadNotes.put("imageZip", parseFile);
                            uploadNotes.put("userName", ParseUser.getCurrentUser().getString("NAME"));
                            uploadNotes.put("subjectName", subjectNameString);
                            uploadNotes.put("topicName", topicNameString);
                            uploadNotes.put("branchName", branchNameString);
                            uploadNotes.put("collegeName", "DTU");

                            uploadNotes.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    // uploading.setVisibility(View.GONE);
                                    Log.d("Raghav", "File Uploaded");
                                    notesUploadProgress.dismiss();
                                    Toast.makeText(getActivity(), getString(R.string.upload_complete),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                           Toast.makeText(getActivity(), "File size beyond limit", Toast.LENGTH_SHORT)
                                   .show();
                        }
                    } else {

                        Toast.makeText(getActivity(), "Please select an Image to upload", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return rootView;
    }

}
