package in.tosc.studddin.fragments.listings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.tosc.studddin.R;
import in.tosc.studddin.ui.MaterialEditText;
import in.tosc.studddin.externalapi.ParseTables;
import in.tosc.studddin.ui.FloatingActionButton;
import in.tosc.studddin.ui.ProgressBarCircular;

/**
 * Created by Prempal on 1/25/2015.
 */
public class ListingsUploadFragment extends Fragment implements View.OnClickListener {

    public static ImageView listing_image;
    public static byte[] byteArray;
    public static String mCurrentPhotoPath;
    //private ImageView upload;
    private MaterialEditText listing;
    private MaterialEditText mobile;
    private Spinner category;
    private ProgressBarCircular uploading;
    private MaterialEditText listing_desc;
    private FloatingActionButton upload;

    public ListingsUploadFragment() {
        // Required empty public constructor
    }

    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView;
        rootView = inflater.inflate(R.layout.fragment_listings_upload, container, false);

        listing = (MaterialEditText) rootView.findViewById(R.id.et_listing);
        mobile = (MaterialEditText) rootView.findViewById(R.id.et_mobile);
        listing_desc = (MaterialEditText) rootView.findViewById(R.id.listing_desc);
        listing_image = (ImageView) rootView.findViewById(R.id.listing_image);
        category = (Spinner) rootView.findViewById(R.id.listing_category);
        uploading = (ProgressBarCircular) rootView.findViewById(R.id.upload_progress);
        uploading.setBackgroundColor(getResources().getColor(R.color.listingsColorPrimaryDark));
        upload = (FloatingActionButton) rootView.findViewById(R.id.listing_upload);
        List<String> categoryList = new ArrayList<>();
        categoryList.add("Book");
        categoryList.add("Apparatus");
        categoryList.add("Misc.");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, categoryList);
        category.setAdapter(dataAdapter);

        upload.setOnClickListener(this);
        listing_image.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.listing_image:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {

                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                }

                Intent chooser = new Intent(Intent.ACTION_CHOOSER);
                chooser.putExtra(Intent.EXTRA_INTENT, galleryIntent);
                chooser.putExtra(Intent.EXTRA_TITLE, "Upload Listing Photo");

                Intent[] intentArray = {cameraIntent};
                chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                startActivityForResult(chooser, 0);
                break;

            case R.id.listing_upload:
                boolean validate = validateInput();

                if (validate) {
                    uploading.setVisibility(View.VISIBLE);
                    ParseObject upload = new ParseObject("Listings");
                    ParseGeoPoint point = ParseUser.getCurrentUser().getParseGeoPoint(ParseTables.Users.LOCATION);
                    if (byteArray == null) {
                        Drawable drawable = getResources().getDrawable(R.drawable.listing_placeholder);
                        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, stream);
                        byteArray = stream.toByteArray();
                    }
                    ParseFile file = new ParseFile(ParseTables.Listings.LISTING_PNG, byteArray);
                    file.saveInBackground();
                    upload.put(ParseTables.Listings.IMAGE, file);
                    upload.put(ParseTables.Listings.OWNER_NAME, ParseUser.getCurrentUser().getString("NAME"));
                    upload.put(ParseTables.Listings.LISTING_NAME, listing.getText().toString());
                    upload.put(ParseTables.Listings.LISTING_DESC, listing_desc.getText().toString());
                    upload.put(ParseTables.Listings.MOBILE, mobile.getText().toString());
                    if(point!=null)
                        upload.put(ParseTables.Listings.LOCATION, point);
                    upload.put(ParseTables.Listings.CATEGORY, category.getSelectedItem().toString());

                    upload.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            uploading.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), getString(R.string.upload_complete),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private boolean validateInput() {
        boolean validate = true;
        if (listing.getText().toString().isEmpty() || listing_desc.getText().toString().isEmpty() || mobile.getText().toString().isEmpty()) {
            validate = false;
            Toast.makeText(getActivity(), "All fields are mandatory", Toast.LENGTH_SHORT).show();
        }
        return validate;
    }
}
