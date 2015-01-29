package in.tosc.studddin.fragments.notes;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.luminous.pick.Action;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
    String zipFileName = "images";


    public void setImagePaths(String[] paths, Boolean isSelected) throws IOException {
        if(isSelected)
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
        Log.d("Raghav", "ID = "+getTag());

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

                } else {
                    if(imagePaths.length != 0){
                        try {
                            ZipOutputStream zipFile = createZip(imagePaths, zipFileName);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                }
            }
        });

        imageView = (ImageView) rootView.findViewById(R.id.notes_selected_image);
        return rootView;
    }


    public static ZipOutputStream createZip(String[] paths, String filename) throws IOException {
        int BUFFER = 2048;

        String[] _files = paths;
        String _zipFile = filename;
        try  {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(_zipFile);

            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

            byte data[] = new byte[BUFFER];

            for(int i=0; i < _files.length; i++) {
                Log.v("Compress", "Adding: " + _files[i]);
                FileInputStream fi = new FileInputStream(_files[i]);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(_files[i].substring(_files[i].lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
            return out;
        } catch(Exception e) {

            throw e;

        }

    }
}
