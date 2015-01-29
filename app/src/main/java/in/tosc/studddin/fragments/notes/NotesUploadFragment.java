package in.tosc.studddin.fragments.notes;


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

import in.tosc.studddin.R;
import in.tosc.studddin.utils.FloatingActionButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotesUploadFragment extends Fragment {


    Button attachButton;
    FloatingActionButton uploadButton;
    EditText topicNameEdTxt, branchNameEdTxt, subjectNameEdTxt;
    String topicNameString = "", branchNameString = "", subjectNameString = "";


    static String[] imagePaths;

    String zipFileName = "/mnt/sdcard/noteszipfile.zip";


    public void setImagePaths(String[] paths, Boolean isSelected) {
        if (isSelected)
            Toast.makeText(getActivity(), getString(R.string.notes_files_selected), Toast.LENGTH_SHORT)
                    .show();
        else
            Toast.makeText(getActivity(), getString(R.string.notes_files_not_selected), Toast.LENGTH_SHORT)
                    .show();

        imagePaths = paths;
    }


    public NotesUploadFragment() {
        // Required empty public constructor
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
        Log.d("Raghav", "ID = " + getTag());

        attachButton = (Button) rootView.findViewById(R.id.notes_attach);
        uploadButton = (FloatingActionButton) rootView.findViewById(R.id.notes_upload);
        topicNameEdTxt = (EditText) rootView.findViewById(R.id.notes_topic);
        branchNameEdTxt = (EditText) rootView.findViewById(R.id.notes_branch);
        subjectNameEdTxt = (EditText) rootView.findViewById(R.id.notes_subject);

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

                topicNameEdTxt.setText(imagePaths[0]);

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
                    }
                }
            }
        });


        return rootView;
    }


}
