package in.tosc.studddin.fragments;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.ByteArrayOutputStream;

import in.tosc.studddin.ApplicationWrapper;
import in.tosc.studddin.R;
import in.tosc.studddin.fragments.events.EventsCreateFragment;
import in.tosc.studddin.fragments.events.EventsListFragment;
import in.tosc.studddin.ui.SlidingTabLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsFragment extends Fragment {

    ViewPager eventsPager;
    //FragmentPagerAdapter fragmentPagerAdapter;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;


    public EventsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_events, container, false);
        int p = getActivity().getResources().getColor(R.color.colorPrimary);
        int s = getActivity().getResources().getColor(R.color.colorPrimaryDark);
        ApplicationWrapper.setCustomTheme((ActionBarActivity) getActivity(), p, s);


        eventsPager = (ViewPager) view.findViewById(R.id.events_pager);
        adapter  =  new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        eventsPager.setAdapter(adapter);
        eventsPager.setOffscreenPageLimit(3);
        tabs = (SlidingTabLayout) view.findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.pink);
            }
        });
        tabs.setViewPager(eventsPager);
        /*fragmentPagerAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return EventsListFragment.newInstance(false);
                    case 1:
                        return (new EventsCreateFragment());
                    case 2:
                        return EventsListFragment.newInstance(true);

                }
                return new NotesSearchFragment();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return "Events";
                    case 1:
                        return "Create event";
                    case 2:
                        return "My Events";
                }

                return null;
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
*/
        return view;
    }

    public void goToOtherFragment(int position) {
        eventsPager.setCurrentItem(position);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 0) && resultCode == -1) {
            if (data == null) {
                compressImage(EventsCreateFragment.mCurrentPhotoPath);
            } else {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getActivity().getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                compressImage(picturePath);
            }
        }
    }

    private void compressImage(String path) {
        int targetW = 200;
        int targetH = 200;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
        EventsCreateFragment.eventImage.setImageBitmap(bitmap);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        EventsCreateFragment.byteArray = stream.toByteArray();
    }

    public class ViewPagerAdapter extends FragmentStatePagerAdapter {

        CharSequence TAB_TITLES[]={"Events","Create event", "My Events"};
        int NUM_TAB =3;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {


            if(position == 0)
            {
//                EventsCreateFragment eventsCreateFragment = new EventsCreateFragment();
                return EventsListFragment.newInstance(false);
            }
            else if(position == 1)
            {
//                EventsListFragment eventsListFragment = new EventsListFragment();
                return (new EventsCreateFragment());
            }
            else {
//                EventsListFragment eventsListFragment = new EventsListFragment();
                return EventsListFragment.newInstance(true);
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
