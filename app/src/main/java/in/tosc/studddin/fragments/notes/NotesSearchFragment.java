package in.tosc.studddin.fragments.notes;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import in.tosc.studddin.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotesSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotesSearchFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Button addNotesButton;
    EditText searchEdTxt;

    private ArrayList<String> notesCollegeName, notesBranchName, notesTopicName, notesSubjectName;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotesSearchFragment.
     */

    // TODO: Rename and change types and number of parameters
    public static NotesSearchFragment newInstance(String param1, String param2) {
        NotesSearchFragment fragment = new NotesSearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;

    }

    public NotesSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_notes_search, container, false);

        GridView notesGridView = (GridView)rootView.findViewById(R.id.notes_gridview);

        notesCollegeName = new ArrayList<String>();
        notesBranchName = new ArrayList<String>();
        notesSubjectName = new ArrayList<String>();
        notesTopicName = new ArrayList<String>();

        addNotesButton = (Button) rootView.findViewById(R.id.notes_button_add);
        searchEdTxt = (EditText) rootView.findViewById(R.id.notes_search);

        addNotesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                FragmentManager fragmentManager = getParentFragment().getChildFragmentManager();

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.notes_slide_entry,R.anim.notes_slide_exit);

                NotesUploadFragment newFragment = new NotesUploadFragment();

                fragmentTransaction.replace(R.id.notes_pager, newFragment).addToBackStack(null).commit();

                // Start the animated transition.

            }
        });

        searchEdTxt = (EditText) rootView.findViewById(R.id.notes_search);

        notesBranchName.add("MCE");
        notesTopicName.add("Sequences");
        notesSubjectName.add("RA");
        notesCollegeName.add("DTU");


        NotesCustomGridViewAdapter adapter = new NotesCustomGridViewAdapter(getActivity(), notesCollegeName, notesBranchName, notesTopicName, notesSubjectName);
        notesGridView = (GridView)rootView.findViewById(R.id.notes_gridview);
        notesGridView.setAdapter(adapter);

        notesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(getActivity(), "Chal raha hai", Toast.LENGTH_SHORT);

            }
        });



        addNotesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment viewPagerOfNotesFragment = getParentFragment();

                List<Fragment> currFragment = viewPagerOfNotesFragment.getChildFragmentManager().getFragments();
                FragmentTransaction fragmentTransaction = viewPagerOfNotesFragment.getFragmentManager().beginTransaction();
                fragmentTransaction.show(currFragment.get(0));

                fragmentTransaction.hide(currFragment.get(1));





            }

//                FragmentManager fragmentManager = getParentFragment().getChildFragmentManager();
////                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager(); //getFragmentManager().beginTransaction();
//
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.setCustomAnimations(R.anim.notes_slide_entry, R.anim.notes_slide_exit);
//
//                NotesUploadFragment newFragment = new NotesUploadFragment();
//
//                fragmentTransaction.replace(R.id.notes_pager, newFragment).addToBackStack(null).commit();

            // Start the animated transition.


        });

        return rootView;

    }


}
