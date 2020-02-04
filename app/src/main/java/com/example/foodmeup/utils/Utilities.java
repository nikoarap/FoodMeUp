package com.example.foodmeup.utils;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;

import com.example.foodmeup.ui.MapsActivity;

public class Utilities {

    /**
     * Register the Broadcast Receivers for the MapActivity
     * @param mapsActivity The Activity that register the receivers
     * @param connectionStateReceiver Instance of the Broadcast Receiver
     */
    public static void registerReceivers(MapsActivity mapsActivity, BroadcastReceiver connectionStateReceiver) {

        IntentFilter filterGps = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        filterGps.addAction(Intent.ACTION_PROVIDER_CHANGED);
        mapsActivity.registerReceiver(connectionStateReceiver, filterGps);

        IntentFilter filterNetwork = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filterNetwork.addAction(Intent.ACTION_PROVIDER_CHANGED);
        mapsActivity.registerReceiver(connectionStateReceiver, filterNetwork);
    }
}