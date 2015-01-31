package in.tosc.studddin.fragments.signon;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.LocationCallback;

import in.tosc.studddin.R;

/**
 * Created by omerjerk on 29/1/15.
 */
public class LocationSelectDialog extends DialogFragment implements OnMapReadyCallback {

    LatLng currentLocation;
    GoogleMap googleMap;

    private static final String TAG = "LocationSelectDialog";

    LocationSetCallback callback;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        Bundle b = getArguments();
        currentLocation = new LatLng(b.getDouble("lat"), b.getDouble("lon"));
        Log.d(TAG, "location = " + currentLocation);

        RelativeLayout rootView = (RelativeLayout) inflater.inflate(R.layout.fragment_map, null);

        builder.setView(rootView)
                .setPositiveButton(R.string.set_location, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO:Pass the location back to parent activity
                    }
                });
        setMapAsync();
        return builder.create();
    }

    public void setMapAsync() {
        SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);
        else
            Log.e(TAG, "mapFragment is null");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        setMarker();
        setOnClickListener();
    }

    private void setMarker() {
        googleMap.addMarker(new MarkerOptions()
                .position(currentLocation)
                .title("My Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
    }

    private void setOnClickListener() {
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("My Location"));
                currentLocation = latLng;
                callback.gotLocation(currentLocation);
            }
        });
    }

    public void setLocationSetCallback(LocationSetCallback callback) {
        this.callback = callback;
    }

    public interface LocationSetCallback {
        public void gotLocation(LatLng latLng);
    }
}
