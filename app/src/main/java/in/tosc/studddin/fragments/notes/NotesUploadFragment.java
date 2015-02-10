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
import java.util.ArrayList;

import in.tosc.studddin.R;
import in.tosc.studddin.ui.MaterialEditText;
import in.tosc.studddin.ui.FloatingActionButton;
import in.tosc.studddin.ui.ProgressBarCircular;

/**
 * NotesUploadFragment
 */
public class NotesUploadFragment extends Fragment {



    static String[] imagePaths = new String[0];

    private ProgressBarCircular uploadingNotes;
    private EditText topicNameEdTxt, branchNameEdTxt, subjectNameEdTxt;
    private ArrayList<ParseFile> parseFileList;
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

        View rootView = inflater.inflate(R.layout.fragment_notes_upload, container, false);


        Button attachButton = (Button) rootView.findViewById(R.id.notes_attach);
        FloatingActionButton uploadButton = (FloatingActionButton) rootView.findViewById(R.id.notes_upload);
        topicNameEdTxt = (MaterialEditText) rootView.findViewById(R.id.notes_topic);
        branchNameEdTxt = (MaterialEditText) rootView.findViewById(R.id.notes_branch);
        subjectNameEdTxt = (MaterialEditText) rootView.findViewById(R.id.notes_subject);
        uploadingNotes = (ProgressBarCircular) rootView.findViewById(R.id.notes_upload_progress);

        parseFileList = new ArrayList<>();

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

                        final ProgressDialog notesUploadProgress = new ProgressDialog(getActivity());
                        notesUploadProgress.setMessage(getString(R.string.notes_uploading));
                        notesUploadProgress.setCancelable(false);
                        notesUploadProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        notesUploadProgress.show();
                        uploadingNotes.setVisibility(View.VISIBLE);

                        for (int i = 0; i < imagePaths.length; i++) {
                            byte[] imageToBeUploaded = convertToByteArray(imagePaths[i]);
                            if (imageToBeUploaded == null) {
                                Toast.makeText(getActivity(), "File size beyond limit", Toast.LENGTH_SHORT)
                                        .show();
                            }

                            ParseFile parseFile = new ParseFile("notes_images", imageToBeUploaded);

                            try {
                                parseFile.save();
                            } catch (ParseException e) {
                                Log.d("Raghav", "Error in parsefile");
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

                        uploadNotes.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                // uploading.setVisibility(View.GONE);
                                Log.d("Raghav", "File Uploaded");
                                notesUploadProgress.dismiss();
                                uploadingNotes.setVisibility(View.GONE);
                                Toast.makeText(getActivity(), getString(R.string.upload_complete),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {

                        Toast.makeText(getActivity(), "Please select an Image to upload", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return rootView;
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

}
