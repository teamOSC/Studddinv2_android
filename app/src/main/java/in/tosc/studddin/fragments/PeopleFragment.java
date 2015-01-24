package in.tosc.studddin.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import in.tosc.studddin.R;
import in.tosc.studddin.fragments.notes.NotesSearchFragment;
import in.tosc.studddin.fragments.notes.NotesUploadFragment;
import in.tosc.studddin.fragments.people.PeopleNearmeFragment;
import in.tosc.studddin.fragments.people.PeopleSameInstituteFragment;
import in.tosc.studddin.fragments.people.PeopleSameInterestsFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class PeopleFragment extends Fragment {


    ViewPager notesPager;
    FragmentPagerAdapter fragmentPagerAdapter;

    public PeopleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_people, container, false);


        fragmentPagerAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0: return (new PeopleNearmeFragment());
                    case 1: return (new PeopleSameInstituteFragment());
                    case 2: return (new PeopleSameInterestsFragment());

                }
                return new NotesSearchFragment();
            }

            @Override
            public int getCount() {
                return 2;
            }
        };

        notesPager = (ViewPager) view.findViewById(R.id.notes_pager);
        notesPager.setAdapter(fragmentPagerAdapter);

        return view;
    }


}
