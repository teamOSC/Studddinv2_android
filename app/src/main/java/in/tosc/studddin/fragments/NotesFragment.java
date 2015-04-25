package in.tosc.studddin.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.tosc.studddin.ApplicationWrapper;
import in.tosc.studddin.R;
import in.tosc.studddin.fragments.notes.NotesSearchFragment;
import in.tosc.studddin.fragments.notes.NotesUploadFragment;
import in.tosc.studddin.ui.SlidingTabLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotesFragment extends Fragment {
    public static final String TAG = "NotesFragment";
    public static final boolean DEBUG = ApplicationWrapper.LOG_DEBUG;
    public static final boolean INFO = ApplicationWrapper.LOG_INFO;


    ViewPager notesPager;
    FragmentStatePagerAdapter fragmentPagerAdapter;
    int p,s;
    NotesUploadFragment notesUploadFragment;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;


    public NotesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_notes, container, false);
        p = getActivity().getResources().getColor(R.color.colorPrimary);
        s = getActivity().getResources().getColor(R.color.colorPrimaryDark);
        ApplicationWrapper.setCustomTheme((ActionBarActivity) getActivity(),p,s);

/*        fragmentPagerAdapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return (new NotesSearchFragment());
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

        };*/

        notesPager = (ViewPager) rootView.findViewById(R.id.notes_pager);
        adapter  =  new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        notesPager.setAdapter(adapter);
        tabs = (SlidingTabLayout) rootView.findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabstripColor);
            }
        });
        tabs.setViewPager(notesPager);


        return rootView;

    }

    public void goToOtherFragment(int position) {
        notesPager.setCurrentItem(position);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (DEBUG) Log.d(TAG, "Request = " + requestCode + "result = " + resultCode);
        String[] paths = data.getStringArrayExtra("all_path");
        if (paths.length == 0)

            notesUploadFragment.setImagePaths(paths, false);


        notesUploadFragment.setImagePaths(paths, true);

    }

    public class ViewPagerAdapter extends FragmentStatePagerAdapter {

        CharSequence TAB_TITLES[]={"Notes","Upload Notes"};
        int NUM_TAB =2;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {


            if(position == 0)
            {
                return (new NotesSearchFragment());
            }
            else {
                return (new NotesUploadFragment());
            }

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TAB_TITLES[position];
        }

        @Override
        public int getCount() {
            return NUM_TAB;
        }
    }
    
}
