package com.example.foodmeup;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.provider.Settings;

import com.google.android.gms.location.LocationListener;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;

public class GPSTracker extends Service implements LocationListener {

    private Context mContext;
    private boolean isGpsEnabled = false;
    private boolean isNetworkEnabled = false;
    private boolean gpsOrWifiEnabled = false;
    private Location mLocation;
    private double mLatitude;
    private double mLongitude;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATE = 10;
    private static final long MIN_TIME_FOR_UPDATE = 60000;
    private LocationManager mLocationManager;

    public GPSTracker(Context mContext) {
        this.mContext = mContext;
        getLocation();
    }

    public Location getLocation() {
        try {
            mLocationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            /*getting status of the gps*/
            isGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            /*getting status of network provider*/
            isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGpsEnabled && !isNetworkEnabled) {

                /*no location provider enabled*/
            } else {

                this.gpsOrWifiEnabled = true;

                /*getting location from network provider*/
                if (isNetworkEnabled) {

                    if(android.os.Build.VERSION.SDK_INT >= 23) {
                        if (ContextCompat.checkSelfPermission(GPSTracker.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_DENIED &&
                                ContextCompat.checkSelfPermission(GPSTracker.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                                        != PackageManager.PERMISSION_DENIED)
                        {
                            mLocationManager.requestLocationUpdates(
                                    LocationManager.NETWORK_PROVIDER,
                                    MIN_TIME_FOR_UPDATE,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATE,
                                    (android.location.LocationListener) this);
                        }
                    }

                    if (mLocationManager != null) {
                        mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if (mLocation != null) {
                            mLatitude = mLocation.getLatitude();
                            mLongitude = mLocation.getLongitude();
                        }
                    }
                    /*if gps is enabled then get location using gps*/
                    if (isGpsEnabled) {
                        if (mLocation == null) {
                            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                    MIN_TIME_FOR_UPDATE,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATE,
                                    (android.location.LocationListener) this);

                            if (mLocationManager != null) {
                                mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                                if (mLocation != null) {
                                    mLatitude = mLocation.getLatitude();
                                    mLongitude = mLocation.getLongitude();
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mLocation;
    }


    public void stopUsingGps() {
        if (mLocationManager != null) {
            mLocationManager.removeUpdates((android.location.LocationListener) GPSTracker.this);
        }
    }

    public double getLatitude() {
        if (mLocation != null) {
            mLatitude = mLocation.getLatitude();
        }
        return mLatitude;
    }


    public double getLongitude() {
        if (mLocation != null) {
            mLongitude = mLocation.getLongitude();
        }
        return mLongitude;
    }

    public boolean canConnect() {
        return this.gpsOrWifiEnabled;
    }

    public void showSettingsAlert() {

        AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(new ContextThemeWrapper(mContext, R.style.AppTheme));
        mAlertDialog.setTitle("Gps Disabled");
        mAlertDialog.setMessage("Please enable your device's GPS for the application to work properly.");

        mAlertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Intent mIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(mIntent);
            }
        });

        mAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.cancel();

            }
        });

        final AlertDialog mcreateDialog = mAlertDialog.create();
        mcreateDialog.show();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }
}
