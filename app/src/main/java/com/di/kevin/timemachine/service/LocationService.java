package com.di.kevin.timemachine.service;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.di.kevin.timemachine.data.LocationDataSource;
import com.di.kevin.timemachine.data.TimeLogDataSource;
import com.di.kevin.timemachine.listener.LocationChangedListener;
import com.di.kevin.timemachine.bean.MyLocation;
import com.di.kevin.timemachine.bean.TimeLog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Collection;
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

    private LocationChangedListener mMyLocationChangedListener;
    private GoogleApiClient mGoogleApiClient;

    private ConcurrentHashMap<Long, MyLocation> myLocations;
    private ConcurrentHashMap<Long, TimeLog> mCurrentLocationTimeTable;

    @Override
    public void onCreate() {

        Log.d(TAG, "onCreate");
        super.onCreate();

        myLocations = new ConcurrentHashMap<>();
        mCurrentLocationTimeTable = new ConcurrentHashMap<>();

        loadMyLocationsFromDatabase();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    public ConcurrentHashMap<Long, TimeLog> getCurrentLocationTimeTable() {
        return mCurrentLocationTimeTable;
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
                int locationScope = cursor.getInt(cursor.getColumnIndex(LocationDataSource.COLUMN_TABLE_LOCATION_LOCATION_SCOPE));
                double locationLat = cursor.getDouble(cursor.getColumnIndex(LocationDataSource.COLUMN_TABLE_LOCATION_LOCATION_LAT));
                double locationLng = cursor.getDouble(cursor.getColumnIndex(LocationDataSource.COLUMN_TABLE_LOCATION_LOCATION_LNG));
                int imageRes = cursor.getInt(cursor.getColumnIndex(LocationDataSource.COLUMN_TABLE_LOCATION_LOCATION_IMAGE));

                myLocation.setLocationId(locationId);
                myLocation.setLocationName(locationName);
                myLocation.setScope(locationScope);
                myLocation.setLocationLat(locationLat);
                myLocation.setLocationLng(locationLng);
                myLocation.setImageRes(imageRes);
                myLocations.put(new Long(locationId), myLocation);
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

        HashMap<Long, MyLocation> currentLocations = findNearestLocations(location);

        if (!currentLocations.values().isEmpty()) {
            Iterator<MyLocation> iterator = currentLocations.values().iterator();

            while (iterator.hasNext()) {
                MyLocation curLocation = iterator.next();

                if (mCurrentLocationTimeTable.values().isEmpty() || mCurrentLocationTimeTable.get(curLocation.getLocationId()) == null) {

                    TimeLog timeLog = new TimeLog();
                    timeLog.setMyLocationId(curLocation.getLocationId());
                    timeLog.setEnterTime(new Date());

                    mCurrentLocationTimeTable.put(new Long(curLocation.getLocationId()), timeLog);
                }
            }

            Set<TimeLog> removingTimeLogs = new HashSet<>();

            if (!mCurrentLocationTimeTable.values().isEmpty()) {
                Iterator<TimeLog> timeLogIterator = mCurrentLocationTimeTable.values().iterator();

                while (timeLogIterator.hasNext()) {
                    TimeLog timeLog = timeLogIterator.next();

                    if (currentLocations.get(timeLog.getMyLocationId()) == null) {
                        timeLog.setLeaveTime(new Date());
                        removingTimeLogs.add(timeLog);
                    }
                }
            }

            if (!removingTimeLogs.isEmpty()) {
                removeTimeLogsFromTimeLogTable(removingTimeLogs);
            }
        } else {
            //Clear all the items in current time table
            removeTimeLogsFromTimeLogTable(mCurrentLocationTimeTable.values());
        }

        if (mMyLocationChangedListener != null) {
            mMyLocationChangedListener.onLocationChanged(location);
        }
    }

    public void removeTimeLogsFromTimeLogTable(Collection<TimeLog> removingTimeLogs) {
        if (!mCurrentLocationTimeTable.values().isEmpty()) {

            if (!removingTimeLogs.isEmpty()) {
                Iterator<TimeLog> iterator = removingTimeLogs.iterator();

                while (iterator.hasNext()) {
                    TimeLog timeLog = iterator.next();

                    timeLog.setLeaveTime(new Date());
                    TimeLogDataSource dataSource = new TimeLogDataSource(this);
                    dataSource.open();
                    dataSource.insertTimeLog(timeLog);

                    Log.d(TAG, "insert time log - loc_id: " + timeLog.getMyLocationId() + " enter_time: " + timeLog.getEnterTime() + " leave_time: " + timeLog.getLeaveTime());

                    dataSource.close();
                    mCurrentLocationTimeTable.remove(timeLog.getMyLocationId());
                }
            }
        }
    }

    private HashMap<Long, MyLocation> findNearestLocations(Location curLocation) {

        HashMap<Long, MyLocation> result = new HashMap<>();

        if (!myLocations.values().isEmpty()) {

            Iterator<MyLocation> iterator = myLocations.values().iterator();
            while (iterator.hasNext()) {
                MyLocation loc = iterator.next();

                Location temp = new Location("temp");
                temp.setLatitude(loc.getLocationLat());
                temp.setLongitude(loc.getLocationLng());
                float distanceToMe = curLocation.distanceTo(temp);

                if (distanceToMe < loc.getScope()) {
                    //TODO current location fall into one of myLocations
                    loc.setDistanceToMe(distanceToMe);
                    result.put(new Long(loc.getLocationId()), loc);
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
        public LocationService getService() {
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
