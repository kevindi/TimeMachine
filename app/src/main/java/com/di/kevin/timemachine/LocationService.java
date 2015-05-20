package com.di.kevin.timemachine;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

public class LocationService extends Service implements LocationListener{
    private LocationManager locationManager;

    private final long MIN_TIME_INTERVAL_BETWEEN_LOCATION_UPDATES = 60000L;
    private final float MIN_DISTANCE_BETWEEN_LOCATION_UPDATES = 100F;

    private final String ERROR_MSG_GPS_DISABLED = "GPS is disabled";
    private final String ERROR_MSG_LOCATION_MANAGER = "Something must have gone wrong with location manager.";
    private LocationChangedListener mLocationChangedListener;

    public LocationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(locationManager != null)
        {
            boolean gpsIsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean networkIsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(gpsIsEnabled)
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_INTERVAL_BETWEEN_LOCATION_UPDATES, MIN_DISTANCE_BETWEEN_LOCATION_UPDATES, this);
            }
            else if(networkIsEnabled)
            {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_INTERVAL_BETWEEN_LOCATION_UPDATES, MIN_DISTANCE_BETWEEN_LOCATION_UPDATES, this);
            }
            else
            {
                //Show an error dialog that GPS is disabled...
                Intent intent = new Intent(this, DialogActivity.class);
                intent.putExtra(DialogActivity.MSG_KEY, ERROR_MSG_GPS_DISABLED);
                startActivity(intent);
            }
        }
        else
        {
            //Show some generic error dialog because something must have gone wrong with location manager.
            Intent intent = new Intent(this, DialogActivity.class);
            intent.putExtra(DialogActivity.MSG_KEY, ERROR_MSG_LOCATION_MANAGER);
            startActivity(intent);
        }
    }

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    @Override
    public void onLocationChanged(Location location) {
        if (mLocationChangedListener != null) {
            mLocationChangedListener.onLocationChanged(location);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void setOnLocationChangedListener(LocationChangedListener listener) {
        this.mLocationChangedListener = listener;
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        LocationService getService() {
            // Return this instance of LocalService so clients can call public methods
            return LocationService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
