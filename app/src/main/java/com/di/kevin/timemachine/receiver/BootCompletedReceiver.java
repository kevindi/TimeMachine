package com.di.kevin.timemachine.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.di.kevin.timemachine.service.LocationService;

/**
 * Created by dike on 21/5/2015.
 */
public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent myIntent = new Intent(context, LocationService.class);
        context.startService(myIntent);
    }
}
