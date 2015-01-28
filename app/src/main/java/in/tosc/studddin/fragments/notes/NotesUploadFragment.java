package in.tosc.studddin.fragments.notes;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    ImageView imageView;
    String notesImagePath;
    Uri imageSelectedUri;

    static String[] imagePaths;

    public void setImagePaths(String[] paths) {
        imagePaths = paths;
    }

    public NotesUploadFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_notes_upload, container, false);

        attachButton = (Button) rootView.findViewById(R.id.notes_attach);
        uploadButton = (FloatingActionButton) rootView.findViewById(R.id.notes_upload);
        topicNameEdTxt = (EditText) rootView.findViewById(R.id.notes_topic);
        branchNameEdTxt = (EditText) rootView.findViewById(R.id.notes_branch);
        subjectNameEdTxt = (EditText) rootView.findViewById(R.id.notes_subject);

        attachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Toast.makeText(getActivity(), "Button pressed", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
                startActivityForResult(i,5);
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);

            }

        });


        uploadButton = (FloatingActionButton)rootView.findViewById(R.id.notes_upload);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(topicNameEdTxt.length() < 1){
                    Toast.makeText(getActivity(), getString(R.string.enter_topic_name),
                            Toast.LENGTH_SHORT).show();
                } else if (subjectNameEdTxt.length() < 1){
                    Toast.makeText(getActivity(), getString(R.string.enter_subject_name),
                            Toast.LENGTH_SHORT).show();
                } else if(branchNameEdTxt.length() < 1){
                    Toast.makeText(getActivity(), getString(R.string.enter_branch_name),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        imageView = (ImageView) rootView.findViewById(R.id.notes_selected_image);
        return rootView;
    }
}
