package in.tosc.studddin.fragments.signon;

import android.graphics.Bitmap;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;

import in.tosc.studddin.externalapi.ParseTables;

/**
 * Created by omerjerk on 26/4/15.
 */
public class FetchUserPhotos extends Thread{

    private PhotosFetcher photosFetcher;

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
            ParseUser currentUser = ParseUser.getCurrentUser();

            Bitmap coverPhoto = photosFetcher.downloadCoverPhoto();
            Bitmap profilePhoto = photosFetcher.downloadProfilePhoto();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // Compress image to lower quality scale 1 - 100
            coverPhoto.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] profilePhotoBytes = stream.toByteArray();
            ParseFile profilePhotoFile = new ParseFile("profilePicture.png", profilePhotoBytes);
            profilePhotoFile.save();
            currentUser.put(ParseTables.Users.IMAGE, profilePhotoFile);

            ByteArrayOutputStream coverPhotoStream = new ByteArrayOutputStream();
            profilePhoto.compress(Bitmap.CompressFormat.PNG, 100, coverPhotoStream);
            byte[] coverPhotoBytes = coverPhotoStream.toByteArray();
            ParseFile coverPhotoFile = new ParseFile("coverPicture.png", coverPhotoBytes);
            coverPhotoFile.save();

            currentUser.put(ParseTables.Users.COVER, coverPhotoFile);
            currentUser.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
