package in.tosc.studddin.fragments.notes;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import in.tosc.studddin.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotesUploadFragment extends Fragment {


    Button attachButton, uploadButton;
    EditText topicNameEdTxt, branchNameEdTxt, subjectNameEdTxt;

    public NotesUploadFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_notes_upload, container, false);

        attachButton = (Button)rootView.findViewById(R.id.notes_attach);
        uploadButton = (Button)rootView.findViewById(R.id.notes_upload);
        topicNameEdTxt = (EditText)rootView.findViewById(R.id.notes_topic);
        branchNameEdTxt = (EditText)rootView.findViewById(R.id.notes_branch);
        subjectNameEdTxt = (EditText)rootView.findViewById(R.id.notes_subject);


        attachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });






        return rootView;

    }


}
