package in.tosc.studddin.fragments.listings;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.tosc.studddin.R;
import in.tosc.studddin.customview.MaterialEditText;
import in.tosc.studddin.utils.FloatingActionButton;
import in.tosc.studddin.utils.FloatingActionsMenu;

/**
 * Created by Prempal on 1/25/2015.
 */
public class ListingsUploadFragment extends Fragment implements View.OnClickListener {

    //private ImageView camera;
    //private ImageView upload;
    //private ImageView sdCard;
    private EditText listing;
    private EditText mobile;
    private Spinner category;
    private LinearLayout uploading;

    public static ImageView listing_image;
    public static byte[] byteArray;
    public static String mCurrentPhotoPath;
    
    private FloatingActionButton camera , gallery, upload;
    public ListingsUploadFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
            rootView = inflater.inflate(R.layout.fragment_listings_upload, container, false);
            FloatingActionsMenu myMenu = (FloatingActionsMenu) rootView.findViewById(R.id.multiple_actions);
        }else {
            rootView = inflater.inflate(R.layout.fragment_listing_upload_gingerbread, container, false);
        }

        
        listing = (MaterialEditText) rootView.findViewById(R.id.et_listing);
        mobile = (MaterialEditText) rootView.findViewById(R.id.et_mobile);
        listing_image = (ImageView) rootView.findViewById(R.id.listing_image);
        category = (Spinner) rootView.findViewById(R.id.listing_category);
        uploading = (LinearLayout) rootView.findViewById(R.id.listing_uploadLayout);
        List<String> categoryList = new ArrayList<String>();
        categoryList.add("Book");
        categoryList.add("Apparatus");
        categoryList.add("Misc.");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,categoryList);
        category.setAdapter(dataAdapter);

        //camera.setOnClickListener(this);
        //upload.setOnClickListener(this);
        //sdCard.setOnClickListener(this);
        
        camera = (FloatingActionButton) rootView.findViewById(R.id.camerafab);
        gallery = (FloatingActionButton) rootView.findViewById(R.id.galleryfab);
        upload  = (FloatingActionButton) rootView.findViewById(R.id.uploadfab);

        

        camera.setOnClickListener(this);
        gallery.setOnClickListener(this);


        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.camerafab:
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

            case R.id.galleryfab:
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                getActivity().startActivityForResult(i,1);
                break;

            case R.id.uploadfab:
                uploading.setVisibility(View.VISIBLE);
                ParseFile file = new ParseFile("listing.png", byteArray);

                file.saveInBackground();

                ParseObject upload = new ParseObject("Listings");
                ParseGeoPoint point = new ParseGeoPoint(28.7500749,77.11766519999992);

                upload.put("image", file);
                upload.put("ownerName", ParseUser.getCurrentUser().getString("NAME"));
                upload.put("listingName", listing.getText().toString());
                upload.put("mobile", mobile.getText().toString());
                upload.put("location", point);
                upload.put("category",category.getSelectedItem().toString());

                upload.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        uploading.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), getString(R.string.upload_complete),
                                Toast.LENGTH_SHORT).show();
                    }
                });
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
