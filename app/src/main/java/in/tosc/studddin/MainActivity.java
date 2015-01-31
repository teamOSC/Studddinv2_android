package in.tosc.studddin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import in.tosc.studddin.fragments.AccountInfoFragment;
import in.tosc.studddin.fragments.EventsFragment;
import in.tosc.studddin.fragments.FeedFragment;
import in.tosc.studddin.fragments.ListingsFragment;
import in.tosc.studddin.fragments.NotesFragment;
import in.tosc.studddin.fragments.PeopleFragment;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    String[] paths;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    Toolbar toolbar;
    private String myTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        myTitle = getString(R.string.app_name);
        if(toolbar==null){
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            if(toolbar!=null){
                setSupportActionBar(toolbar);
                toolbar.setTitle(myTitle);
                toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            }
        }
        

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getResources().getString(R.string.test_feeds);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));


    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {
            case 0:
            default:
                Log.d("Studdd.in", "feed fragment");
                mTitle = "Feeds";
                fragmentManager.beginTransaction()
                        .replace(R.id.container, FeedFragment.newInstance())
                        .commit();
            break;
            case 1:
                Log.d("Studdd.in", "notes fragment");
                mTitle = "Notes";
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new NotesFragment())
                        .commit();
            break;
            case 2:
                Log.d("Studdd.in", "feed fragment");
                mTitle = "People";
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new PeopleFragment())
                        .commit();
                break;

            case 3:
                Log.d("Studdd.in", "feed fragment");
                mTitle = "Listings";
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new ListingsFragment())
                        .commit();
                break;
            case 4:
                Log.d("Studdd.in", "feed fragment");
                mTitle = "Events";
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new EventsFragment())
                        .commit();
                break;
            case 5:
                Log.d("Studd.in","account info");
                mTitle = "Account";
                fragmentManager.beginTransaction()
                        .replace(R.id.container,new AccountInfoFragment())
                        .commit(); 
                break;

        }

    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.test_feeds);
                break;
            case 2:
                mTitle = getString(R.string.test_notes);
                break;
            case 3:
                mTitle = getString(R.string.test_people);
                break;
            case 4:
                mTitle = "Listings";
                break;
            case 5:
                mTitle = "Events";
        }
    }

    public void restoreActionBar() {
        //ActionBar actionBar = getSupportActionBar();
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        //actionBar.setDisplayShowTitleEnabled(true);
        //actionBar.setTitle(mTitle);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.toolbar.setTitle(mTitle);
        this.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 131077 && resultCode == Activity.RESULT_OK) {
            paths = data.getStringArrayExtra("all_path");
        }
        super.onActivityResult(requestCode, resultCode, data);

    }
}
