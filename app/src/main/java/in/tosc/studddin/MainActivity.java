package in.tosc.studddin;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.ByteArrayOutputStream;

import in.tosc.studddin.fragments.AccountInfoFragment;
import in.tosc.studddin.fragments.EventsFragment;
import in.tosc.studddin.fragments.FeedFragment;
import in.tosc.studddin.fragments.ListingsFragment;
import in.tosc.studddin.fragments.NotesFragment;
import in.tosc.studddin.fragments.PeopleFragment;
import in.tosc.studddin.fragments.listings.ListingsUploadFragment;
import in.tosc.studddin.fragments.notes.NotesUploadFragment;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private Toolbar toolbar;
    private String myTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        myTitle = getString(R.string.test_feeds);
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
        mTitle = getTitle();

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
                fragmentManager.beginTransaction()
                        .replace(R.id.container, FeedFragment.newInstance())
                        .commit();
            break;
            case 1:
                Log.d("Studdd.in", "notes fragment");
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new NotesFragment(), "NOTES")
                        .commit();
            break;
            case 2:
                Log.d("Studdd.in", "feed fragment");
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new PeopleFragment())
                        .commit();
                break;

            case 3:
                Log.d("Studdd.in", "feed fragment");
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new ListingsFragment())
                        .commit();
                break;
            case 4:
                Log.d("Studdd.in", "feed fragment");
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new EventsFragment())
                        .commit();
                break;
            case 5:
                Log.d("Studd.in","account info");
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
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
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
        if ((requestCode == 0) && resultCode == RESULT_OK) {
            int targetW = ListingsUploadFragment.listing_image.getWidth();
            int targetH = ListingsUploadFragment.listing_image.getHeight();

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(ListingsUploadFragment.mCurrentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(ListingsUploadFragment.mCurrentPhotoPath, bmOptions);
            ListingsUploadFragment.listing_image.setImageBitmap(bitmap);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            ListingsUploadFragment.byteArray = stream.toByteArray();
        }
        else if((requestCode == 1) && resultCode == RESULT_OK) {

            Uri selectedImage = data.getData();
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            String picturePath = c.getString(columnIndex);
            c.close();
            Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
            ListingsUploadFragment.listing_image.setImageBitmap(thumbnail);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.PNG, 100, stream);
            ListingsUploadFragment.byteArray = stream.toByteArray();

        }


        if (requestCode == 5 && resultCode == Activity.RESULT_OK) {
            String[] all_path = data.getStringArrayExtra("all_path");

            NotesUploadFragment notesUploadFragment = (NotesUploadFragment) getSupportFragmentManager().findFragmentByTag("NOTES");

            if(notesUploadFragment != null)
                notesUploadFragment.setImagePaths(all_path);

//            viewSwitcher.setDisplayedChild(0);
//            adapter.addAll(dataT);

        }
    }

}
