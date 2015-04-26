package in.tosc.studddin.fragments.signon;

import android.graphics.Bitmap;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;

import in.tosc.studddin.externalapi.ParseTables;

/**
 * Created by omerjerk on 26/4/15.
 */
public class FetchUserPhotos extends Thread {

    private PhotosFetcher photosFetcher;

    private static final String TAG = "FetchUserPhotos";

    public FetchUserPhotos(PhotosFetcher pf) {
        photosFetcher = pf;
    }

    public interface PhotosFetcher {
        public Bitmap downloadCoverPhoto();
        public Bitmap downloadProfilePhoto();
    }

    @Override
    public void run() {
        try {
            Log.d(TAG, "starting download pictures thread");
            ParseUser currentUser = ParseUser.getCurrentUser();

            Bitmap coverPhoto = photosFetcher.downloadCoverPhoto();
            Log.d(TAG, "Downloaded cover photo");
            Bitmap profilePhoto = photosFetcher.downloadProfilePhoto();
            Log.d(TAG, "downloaded profile photo");

            // Compress image to lower quality scale 1 - 100
            if (coverPhoto != null) {
                ByteArrayOutputStream profilePhotoStream = new ByteArrayOutputStream();
                coverPhoto.compress(Bitmap.CompressFormat.PNG, 100, profilePhotoStream);
                byte[] profilePhotoBytes = profilePhotoStream.toByteArray();
                ParseFile profilePhotoFile = new ParseFile("profilePicture.png", profilePhotoBytes);
                profilePhotoFile.save();
                currentUser.put(ParseTables.Users.IMAGE, profilePhotoFile);
            }

            if (profilePhoto != null) {
                ByteArrayOutputStream coverPhotoStream = new ByteArrayOutputStream();
                profilePhoto.compress(Bitmap.CompressFormat.PNG, 100, coverPhotoStream);
                byte[] coverPhotoBytes = coverPhotoStream.toByteArray();
                ParseFile coverPhotoFile = new ParseFile("coverPicture.png", coverPhotoBytes);
                coverPhotoFile.save();
                currentUser.put(ParseTables.Users.COVER, coverPhotoFile);
            }

            currentUser.save();
            Log.d(TAG, "Done with everything");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
