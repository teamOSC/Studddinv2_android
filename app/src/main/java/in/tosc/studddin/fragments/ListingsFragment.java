package in.tosc.studddin.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.tosc.studddin.R;
import in.tosc.studddin.fragments.listings.ListingsSearchFragment;
import in.tosc.studddin.fragments.listings.ListingsUploadFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListingsFragment extends Fragment {


    ViewPager notesPager;
    FragmentPagerAdapter fragmentPagerAdapter;

    public ListingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_notes, container, false);

        fragmentPagerAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0: return (new ListingsSearchFragment());
                    case 1: return (new ListingsUploadFragment());
                }
                return new ListingsSearchFragment();
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






}
