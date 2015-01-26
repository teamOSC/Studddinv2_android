package in.tosc.studddin.fragments.listings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import in.tosc.studddin.R;

/**
 * Created by Prempal on 1/25/2015.
 */
public class ListingsUploadFragment extends Fragment implements View.OnClickListener {

    private ImageView camera;
    private ImageView upload;
    private ImageView sdCard;
    private EditText listing;
    private EditText mobile;
    private EditText name;

    public static ImageView listing_image;

    public static String mCurrentPhotoPath;


    public ListingsUploadFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_listings_upload, container, false);
        camera = (ImageView) rootView.findViewById(R.id.listing_camera);
        upload = (ImageView) rootView.findViewById(R.id.listing_upload);
        sdCard = (ImageView) rootView.findViewById(R.id.listing_sdcard);
        listing = (EditText) rootView.findViewById(R.id.et_listing);
        mobile = (EditText) rootView.findViewById(R.id.et_mobile);
        name = (EditText) rootView.findViewById(R.id.et_name);
        listing_image = (ImageView) rootView.findViewById(R.id.listing_image);

        camera.setOnClickListener(this);
        upload.setOnClickListener(this);
        sdCard.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.listing_image:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {

                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                    getActivity().startActivityForResult(takePictureIntent, 0);
                }
                break;

            case R.id.distance_image:
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                getActivity().startActivityForResult(i,1);
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
}
