package com.di.kevin.timemachine;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.di.kevin.timemachine.object.MyLocation;
import com.di.kevin.timemachine.object.TimeLog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private final static String TAG = LocationService.class.getSimpleName();

    private final long MIN_INTERVAL_BETWEEN_LOCATION_UPDATES = 500L;
    private final long INTERVAL_BETWEEN_LOCATION_UPDATES = 1000L;

    private final static float DEFAULT_MIN_DISTANCE = 50F;

    private LocationChangedListener mMyLocationChangedListener;
    private GoogleApiClient mGoogleApiClient;

    private ConcurrentHashMap<Long, MyLocation> myLocations;

    @Override
    public void onCreate() {

        Log.d(TAG, "onCreate");
        super.onCreate();

        myLocations = new ConcurrentHashMap<>();
        loadMyLocationsFromDatabase();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    public ConcurrentHashMap<Long, MyLocation> getMyLocations() {
        return myLocations;
    }

    public void putMyLocation(long locationId, MyLocation location) {
        myLocations.put(locationId, location);
    }

    private void loadMyLocationsFromDatabase() {
        LocationDataSource dataSource = new LocationDataSource(this);
        dataSource.open();
        Cursor cursor = dataSource.getAllMyLocations();

        if (cursor.moveToFirst()){
            do{
                MyLocation myLocation = new MyLocation();

                int locationId = cursor.getInt(cursor.getColumnIndex(LocationDataSource.COLUMN_TABLE_LOCATION_LOCATION_ID));
                String locationName = cursor.getString(cursor.getColumnIndex(LocationDataSource.COLUMN_TABLE_LOCATION_LOCATION_NAME));
                double locationLat = cursor.getDouble(cursor.getColumnIndex(LocationDataSource.COLUMN_TABLE_LOCATION_LOCATION_LAT));
                double locationLng = cursor.getDouble(cursor.getColumnIndex(LocationDataSource.COLUMN_TABLE_LOCATION_LOCATION_LNG));

                myLocation.setLocationId(locationId);
                myLocation.setLocationName(locationName);
                myLocation.setLocationLat(locationLat);
                myLocation.setLocationLng(locationLng);
                myLocations.put(new Long(locationId), myLocation);
                // do what ever you want here
            }while(cursor.moveToNext());
        }
        cursor.close();
        dataSource.close();
    }

    private void startGPSTracker() {
        Log.d(TAG, "startGPSTracker");

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL_BETWEEN_LOCATION_UPDATES);
        mLocationRequest.setFastestInterval(MIN_INTERVAL_BETWEEN_LOCATION_UPDATES);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return START_STICKY;
    }

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    public void setOnLocationChangedListener(LocationChangedListener listener) {
        this.mMyLocationChangedListener = listener;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");
        startGPSTracker();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended i: " + i);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed connectionResult: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged lat: " + location.getLatitude() + " lng: " + location.getLongitude());

        Set<MyLocation> currentLocations = findNearestLocations(location);

        if (!currentLocations.isEmpty()) {
            Iterator<MyLocation> iterator = currentLocations.iterator();

            while (iterator.hasNext()) {
                MyLocation myLocation = iterator.next();
                Log.d(TAG, "current location: " + myLocation.getLocationName() + " distance to me: " + myLocation.getDistanceToMe());

                TimeLog timeLog = new TimeLog();
                timeLog.setMyLocationId(myLocation.getLocationId());
                timeLog.setLogTime(new Date());

                TimeLogDataSource dataSource = new TimeLogDataSource(this);
                dataSource.open();
                dataSource.insertTimeLog(timeLog);
                dataSource.close();
            }
        }

        mMyLocationChangedListener.onLocationChanged(location);
    }

    private Set<MyLocation> findNearestLocations(Location curLocation) {

        Set<MyLocation> result = new HashSet<>();

        if (!myLocations.values().isEmpty()) {

            Iterator<MyLocation> iterator = myLocations.values().iterator();
            while (iterator.hasNext()) {
                MyLocation loc = iterator.next();

                Location temp = new Location("temp");
                temp.setLatitude(loc.getLocationLat());
                temp.setLongitude(loc.getLocationLng());
                float distanceToMe = curLocation.distanceTo(temp);
                if (distanceToMe < DEFAULT_MIN_DISTANCE) {
                    //TODO current location fall into one of myLocations
                    loc.setDistanceToMe(distanceToMe);
                    result.add(loc);
                }
            }
        }
        return result;
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

    @Override
    public void onDestroy() {

        Log.d(TAG, "onDestroy");

        super.onDestroy();
    }
}
