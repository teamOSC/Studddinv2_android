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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.ByteArrayOutputStream;

import in.tosc.studddin.ApplicationWrapper;
import in.tosc.studddin.R;
import in.tosc.studddin.fragments.listings.ListingsSearchFragment;
import in.tosc.studddin.fragments.listings.ListingsUploadFragment;
import in.tosc.studddin.fragments.listings.MyListingsFragment;
import in.tosc.studddin.ui.SlidingTabLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListingsFragment extends Fragment {
    public static final String TAG = "ListingsFragment";
    public static final boolean DEBUG = ApplicationWrapper.LOG_DEBUG;
    public static final boolean INFO = ApplicationWrapper.LOG_INFO;


    ViewPager listingsPager;
    //FragmentStatePagerAdapter fragmentPagerAdapter;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;

    public ListingsFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_listings, container, false);
        int p = getActivity().getResources().getColor(R.color.listingsColorPrimary);
        int s = getActivity().getResources().getColor(R.color.listingsColorPrimaryDark);
        ApplicationWrapper.setCustomTheme((ActionBarActivity) getActivity(), p, s);

/*        fragmentPagerAdapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Log.d(TAG,"getItem called");
                switch (position) {
                    case 0:
                        return new ListingsSearchFragment();
                    case 1:
                        return new ListingsUploadFragment();
                    case 2:
                        return new MyListingsFragment();
                }
                return new ListingsSearchFragment();
            }

            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 1:
                        return "Create Listing";
                    case 2:
                        return "My Listings";
                    case 0:
                    default:
                        return "Listings";
                }
            }
        };*/

        listingsPager = (ViewPager) rootView.findViewById(R.id.listings_pager);




        try {
            adapter  =  new ViewPagerAdapter(getActivity().getSupportFragmentManager());
            listingsPager.setAdapter(adapter);
//          listingsPager.setOffscreenPageLimit(2);
            tabs = (SlidingTabLayout) rootView.findViewById(R.id.tabs);
            tabs.setDistributeEvenly(true);
            tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                @Override
                public int getIndicatorColor(int position) {
                    return getResources().getColor(R.color.peopleColorPrimary);
                }
            });
            tabs.setViewPager(listingsPager);
        } catch (NullPointerException e) {
            if (listingsPager == null) Log.e("Listings", "listingsPager = null", e);
            if (adapter == null) Log.e("Listings", "fragmentPagerAdapter = null", e);
        }

        return rootView;
    }

    public void goToOtherFragment(int position) {
        listingsPager.setCurrentItem(position);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (DEBUG) Log.d(TAG, "onActivityResult call ho raha hai" + resultCode + "   " + requestCode);
        if ((requestCode == 0) && resultCode == -1) {
            if (data == null) {
                if (DEBUG) Log.d(TAG, "onActivityResult camera");
                compressImage(ListingsUploadFragment.mCurrentPhotoPath);
            } else {
                if (DEBUG) Log.d(TAG, "onActivityResult docs");
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
        int targetW = 150;
        int targetH = 150;

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
        ListingsUploadFragment.listing_image.setImageBitmap(bitmap);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, stream);
        ListingsUploadFragment.byteArray = stream.toByteArray();
    }

    public class ViewPagerAdapter extends FragmentStatePagerAdapter {

        CharSequence TAB_TITLES[]={"Listings","post an item", "My listings"};
        int NUM_TAB =3;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {


            switch (position) {
                case 0:
                    return new ListingsSearchFragment();
                case 1:
                    return new ListingsUploadFragment();
                case 2:
                    return new MyListingsFragment();
            }
            return new ListingsSearchFragment();

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
