package in.tosc.studddin.fragments.signon;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;

import in.tosc.studddin.externalapi.ParseTables;
import in.tosc.studddin.utils.Utilities;

/**
 * Created by omerjerk on 26/4/15.
 */
public class FetchUserPhotos extends Thread {

    private PhotosFetcher photosFetcher;
    private Context context;

    private static final String TAG = "FetchUserPhotos";

    public FetchUserPhotos(PhotosFetcher pf, Context context) {
        photosFetcher = pf;
        this.context = context;
    }

    public interface PhotosFetcher {
        public String downloadCoverPhoto();
        public String downloadProfilePhoto();
    }

    @Override
    public void run() {
        try {
            Log.d(TAG, "starting download pictures thread");
            ParseUser currentUser = ParseUser.getCurrentUser();

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();

            String coverPhotoUrl = photosFetcher.downloadCoverPhoto();
            editor.putString(ParseTables.KEY_COVER_URL, coverPhotoUrl);
            Bitmap coverPhoto = Utilities.downloadBitmap(coverPhotoUrl);
            Log.d(TAG, "Downloaded cover photo");

            String profilePhotoUrl = photosFetcher.downloadProfilePhoto();
            editor.putString(ParseTables.KEY_IMAGE_URL, profilePhotoUrl);
            Bitmap profilePhoto = Utilities.downloadBitmap(profilePhotoUrl);
            Log.d(TAG, "downloaded profile photo");

            // Compress image to lower quality scale 1 - 100
            if (coverPhoto != null) {
                ByteArrayOutputStream coverPhotoStream = new ByteArrayOutputStream();
                coverPhoto.compress(Bitmap.CompressFormat.PNG, 80, coverPhotoStream);
                byte[] profilePhotoBytes = coverPhotoStream.toByteArray();
                ParseFile profilePhotoFile = new ParseFile("coverPicture.png", profilePhotoBytes);
                profilePhotoFile.save();
                currentUser.put(ParseTables.Users.COVER, profilePhotoFile);
            }

            if (profilePhoto != null) {
                ByteArrayOutputStream profilePhotoStream = new ByteArrayOutputStream();
                profilePhoto.compress(Bitmap.CompressFormat.PNG, 80, profilePhotoStream);
                byte[] coverPhotoBytes = profilePhotoStream.toByteArray();
                ParseFile coverPhotoFile = new ParseFile("profilePicture.png", coverPhotoBytes);
                coverPhotoFile.save();
                currentUser.put(ParseTables.Users.IMAGE, coverPhotoFile);
            }

            currentUser.save();
            editor.commit();
            Log.d(TAG, "Done with everything");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
