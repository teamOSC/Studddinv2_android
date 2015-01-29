package in.tosc.studddin.fragments.signon;

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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import in.tosc.studddin.R;

/**
 * Created by omerjerk on 29/1/15.
 */
public class LocationSelectDialog extends DialogFragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;
    Context context;
    Location mLastLocation;
    GoogleMap googleMap;

    boolean googleApiConnected = false;
    boolean mapReady = false;

    private static final String TAG = "LocationSelectDialog";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        context = getActivity();
        LayoutInflater inflater = getActivity().getLayoutInflater();

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        RelativeLayout rootView = (RelativeLayout) inflater.inflate(R.layout.fragment_map, null);

        builder.setView(rootView)
                .setPositiveButton(R.string.set_location, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO:Pass the location back to parent activity
                    }
                });


        return builder.create();
    }

    public void renderMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager()
                .findFragmentById(R.id.map_dialog);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapReady = true;
        this.googleMap = googleMap;
        setMarker();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        googleApiConnected = true;
        setMarker();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "connection to Google API Client failed " + connectionResult.getErrorCode());
    }

    private void setMarker() {
        if (mapReady && googleApiConnected) {
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                    .title("My Location"));
        }
    }
}
