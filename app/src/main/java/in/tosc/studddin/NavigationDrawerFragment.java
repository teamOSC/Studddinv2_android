package in.tosc.studddin;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseImageView;
import com.parse.ParseUser;

import java.lang.reflect.Array;

import in.tosc.studddin.externalapi.ParseTables;
import in.tosc.studddin.utils.ParseCircularImageView;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawerLinearLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;
    private ParseImageView mProfilePic;
    private ParseImageView mCoverPic;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    private Toolbar toolbar;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //toolbar = (Toolbar) mDrawerLinearLayout.findViewById(R.id.toolbar);


        mDrawerLinearLayout = (LinearLayout) inflater.inflate(
                R.layout.fragment_main_navdrawer, container, false);
        mDrawerListView = (ListView) mDrawerLinearLayout.findViewById(R.id.main_navdrawer_list);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
/*
        mDrawerListView.setAdapter(new ArrayAdapter<String>(
                getActivity().getApplicationContext(),
                R.layout.drawer_simple_list_item,
                R.id.texttext,
                new String[]{
                        "Feeds",
                        "Notes",
                        "People",
                        "Listings",
                        "Events",
                        "Account"
                }));
*/
        mDrawerListView.setAdapter(new NavigationDrawerAdapter(new String[]{
                "Feeds",
                "Notes",
                "People",
                "Listings",
                "Events",
                "Account"
        }));
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);


        mProfilePic = ((ParseCircularImageView) mDrawerLinearLayout.findViewById(R.id.nav_drawer_profile_pic));
        mProfilePic.setPlaceholder(getResources().getDrawable(R.drawable.com_facebook_profile_default_icon));
        mProfilePic.setParseFile(ParseUser.getCurrentUser().getParseFile(ParseTables.Users.USER_IMAGE));

        mCoverPic = ((ParseImageView) mDrawerLinearLayout.findViewById(R.id.nav_drawer_cover_picture));
        mCoverPic.setPlaceholder(getResources().getDrawable(R.drawable.ic_abstract));
        mCoverPic.setParseFile(ParseUser.getCurrentUser().getParseFile(ParseTables.Users.USER_COVER));

        return mDrawerLinearLayout;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ((ActionBarActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
               /* R.drawable.ic_drawer,   */          /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }
                mProfilePic.loadInBackground();
                mCoverPic.loadInBackground();
                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        //ActionBar actionBar = getActionBar();
        //actionBar.setDisplayShowTitleEnabled(true);
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        //actionBar.setTitle(R.string.app_name);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ActionBarActivity actionBarActivity = (ActionBarActivity) getActivity();
        actionBarActivity.getSupportActionBar();
        actionBarActivity.getSupportActionBar().setTitle(R.string.app_name);
        //toolbar.setTitle(R.string.app_name);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);

    }

    private class NavigationDrawerAdapter extends BaseAdapter {

        String[] mCategoryMap;

        public NavigationDrawerAdapter(String[] mCategoryMap) {

            this.mCategoryMap = mCategoryMap;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return Array.getLength(mCategoryMap);
        }

        @Override
        public String getItem(int pos) {
            // TODO Auto-generated method stub
            return mCategoryMap[pos];
        }

        @Override
        public long getItemId(int pos) {
            // TODO Auto-generated method stub
            return pos;
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {

            View rowView;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.row_navigation_drawer, null);
            } else {
                rowView = convertView;
            }

            ImageView navigationIcon = (ImageView) rowView.findViewById(R.id.icon_navigation);
            TextView navigationTitle = (TextView) rowView.findViewById(R.id.title_navigation);

            navigationTitle.setText(mCategoryMap[pos]);
            //navigationTitle.setTextColor(Color.WHITE);
            navigationIcon.setBackgroundResource(R.drawable.ic_launcher);

            String s = mCategoryMap[pos];
            if (s.equals("Feeds")) {
                navigationIcon.setBackgroundResource(R.drawable.feeds);

            } else if (s.equals("Notes")) {
                navigationIcon.setBackgroundResource(R.drawable.notes);

            } else if (s.equals("People")) {
                navigationIcon.setBackgroundResource(R.drawable.people);

            } else if (s.equals("Listings")) {
                navigationIcon.setBackgroundResource(R.drawable.listings);

            } else if (s.equals("Events")) {
                navigationIcon.setBackgroundResource(R.drawable.events);

            } else if (s.equals("Account")) {
                navigationIcon.setBackgroundResource(R.drawable.account);

            }
            return rowView;
        }
    }
    
}
