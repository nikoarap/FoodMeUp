package com.example.foodmeup;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

class PermissionCheck {

    /**
     * Checks if the GPS permission has been accepted
     * @param mapsActivity The host Activity that holds the dialog
     */
    static void checkLocationPermission(final MapsActivity mapsActivity) {
        if (ContextCompat.checkSelfPermission(mapsActivity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(mapsActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MapUtils.MY_PERMISSIONS_REQUEST_LOCATION );

        }
    }

}
