package com.di.kevin.timemachine;

import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.di.kevin.timemachine.object.MyLocation;
import com.di.kevin.timemachine.view.CreateLocationDialog;
import com.di.kevin.timemachine.view.TimeLogDialog;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class MapsActivity extends FragmentActivity implements LocationChangedListener,
        CreateLocationDialog.LocationCreateConfirmListener, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private final String TAG = MapsActivity.class.getSimpleName();
    private boolean mInitCameraPos = true;
    private Location myLocation;
    private LocationChangedListener mMyLocationChangedListener;

    private HashMap<String, Long> myMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_maps);

        myMarkers = new HashMap<>();

        setUpMapIfNeeded();

        findViewById(R.id.btn_add_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LatLng latLng = null;
                if (myLocation != null) {
                    latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                }
                showCreateLocationDialog(latLng, false);
            }
        });
    }



    private void showCreateLocationDialog(LatLng latLng, boolean fixLatLng) {
        CreateLocationDialog dialog = new CreateLocationDialog();
        dialog.setCreateLocationListener(MapsActivity.this);

        if (latLng != null) {
            Bundle bundle = new Bundle();
            bundle.putDouble(CreateLocationDialog.KEY_LAT, latLng.latitude);
            bundle.putDouble(CreateLocationDialog.KEY_LNG, latLng.longitude);
            bundle.putBoolean(CreateLocationDialog.KEY_FIX_LAT_LNG, fixLatLng);
            dialog.setArguments(bundle);
        }

        setLocationChangedListener(dialog);
        dialog.show(getFragmentManager(), CreateLocationDialog.class.getSimpleName());
    }

    public void setLocationChangedListener(LocationChangedListener listener) {
        this.mMyLocationChangedListener = listener;
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        mInitCameraPos = true;
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, wekee
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapLongClickListener(this);

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                if (mInitCameraPos) {
                    mInitCameraPos = false;
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13);
                    mMap.animateCamera(cameraUpdate);
                }
            }
        });

    }

    private void setUpMarkers() {

        Log.d(TAG, "mBound: " + mBound + "is service empty: " + mService.getMyLocations().isEmpty());

        if (mBound && !mService.getMyLocations().isEmpty()) {

            Collection<MyLocation> locations = mService.getMyLocations().values();

            Log.d(TAG, "location size: " + locations.size());

            Iterator iterator = locations.iterator();
            while (iterator.hasNext()) {
                MyLocation location = (MyLocation) iterator.next();

                addNewMarker(location);
            }
        }
    }

    LocationService mService;
    boolean mBound = false;

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");
        // Bind to LocalService
        Intent intent = new Intent(this, LocationService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d(TAG, "onStop");
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.d(TAG, "onServiceConnected ComponentName: " + className.toString() + " service: " + service.toString() );

            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            mService = binder.getService();
            mService.setOnLocationChangedListener(MapsActivity.this);
            mBound = true;
            setUpMarkers();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

            Log.d(TAG, "onServiceDisconnected ComponentName: " + arg0.toString());
            mService.setOnLocationChangedListener(null);
            mBound = false;
        }
    };

    @Override
    public void onLocationChanged(Location newLocation) {
        Log.d(TAG, "onLocationChanged lat: " + newLocation.getLatitude() + " lng: " + newLocation.getLongitude());

        this.myLocation = newLocation;
        if (mMyLocationChangedListener != null) {
            mMyLocationChangedListener.onLocationChanged(newLocation);
        }
    }

    @Override
    public void onCreateConfirm(MyLocation location) {
        Toast.makeText(this, "location created : " + location.getLocationName(), Toast.LENGTH_SHORT).show();

        if (mBound) {
            mService.putMyLocation(location.getLocationId(), location);
            addNewMarker(location);
        }
    }

    private void addNewMarker(MyLocation location) {
        if (mMap != null) {
            Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLocationLat(), location.getLocationLng()))
                    .title(location.getLocationName()));

            myMarkers.put(marker.getId(), location.getLocationId());
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        Long locationId = myMarkers.get(marker.getId());
        if (locationId != null) {

            if (mBound) {
                MyLocation location = mService.getMyLocations().get(locationId);

                if (location != null) {

                    TimeLogDialog dialog = new TimeLogDialog();
                    Bundle bundle = new Bundle();
                    bundle.putLong(TimeLogDialog.KEY_LOCATION_ID, location.getLocationId());
                    dialog.setArguments(bundle);

                    dialog.show(getFragmentManager(), TimeLogDialog.class.getSimpleName());

                    Toast.makeText(this, "Click " + location.getLocationName(), Toast.LENGTH_LONG).show();
                }
            }
        }
        return true;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        Log.d(TAG, "onMapLongClick lat: " + latLng.latitude + " lng: " + latLng.longitude);

        showCreateLocationDialog(latLng, true);
    }
}
