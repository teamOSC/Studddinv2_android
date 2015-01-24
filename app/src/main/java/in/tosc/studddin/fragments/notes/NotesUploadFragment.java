package in.tosc.studddin.fragments.notes;


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






        return rootView;

    }


}
