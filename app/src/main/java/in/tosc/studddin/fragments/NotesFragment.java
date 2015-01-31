package in.tosc.studddin.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.tosc.studddin.R;
import in.tosc.studddin.fragments.notes.NotesSearchFragment;
import in.tosc.studddin.fragments.notes.NotesUploadFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotesFragment extends Fragment {


    ViewPager notesPager;
    FragmentStatePagerAdapter fragmentPagerAdapter;

    NotesUploadFragment notesUploadFragment;


    public NotesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_notes, container, false);


        fragmentPagerAdapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0: return (new NotesSearchFragment());
                    case 1:
                        notesUploadFragment = new NotesUploadFragment();
                        return notesUploadFragment;
                }
                return new NotesSearchFragment();
            }

            @Override
            public int getCount() {
                return 2;
            }

        };

        notesPager = (ViewPager) rootView.findViewById(R.id.notes_pager);
        notesPager.setAdapter(fragmentPagerAdapter);


        return rootView;

    }

    public void goToOtherFragment(int position) {
        notesPager.setCurrentItem(position);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Raghav", "Request = " + requestCode + "result = " + resultCode);
        String[]  paths = data.getStringArrayExtra("all_path");
        if(paths.length == 0)

                notesUploadFragment.setImagePaths(paths, false);


            notesUploadFragment.setImagePaths(paths, true);


    }
}
