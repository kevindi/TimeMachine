package com.di.kevin.timemachine.application;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.di.kevin.timemachine.service.LocationService;

/**
 * Created by dike on 21/5/2015.
 */
public class LocationApplication extends Application {

    private final static String TAG = LocationApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        startService(new Intent(getApplicationContext(), LocationService.class));
    }
}
