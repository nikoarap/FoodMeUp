package com.example.foodmeup;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, AddressObtainTask.Callback {

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private ReentrantLock addressObtainedLock;
    public AlertDialog lastAlertDialog;
    private BroadcastReceiver connectionStateReceiver = new ConnectionBroadcastReceiver(this);

    @BindView(R.id.address_txt) TextView addressText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //ButterKnife library binding all the necessary views
        ButterKnife.bind(this);

        //Custom Toolbar replacing the default one
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(null);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);

        FloatingActionButton fab = findViewById(R.id.fab);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mFusedLocationClient = MapUtils.registerFusedLocationClient(this);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);

        addressObtainedLock = new ReentrantLock();

        lastAlertDialog = AlertDialogUtils.alertCheckConnectivityAvailability(lastAlertDialog, MapsActivity.this);
        Utilities.registerReceivers(this, connectionStateReceiver);

        mLocationCallback = MapUtils.getLocation(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mFusedLocationClient != null)
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        this.unregisterReceiver(connectionStateReceiver);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapUtils.setUpMap(googleMap, mFusedLocationClient, mLocationCallback, this);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        MapUtils.checkPermissionRequestCode(
                this, requestCode, grantResults, mFusedLocationClient, mLocationCallback);
    }

    @Override
    public void onAddressObtained(@NonNull String address) {
        addressObtainedLock.lock();
        addressText.setText(address);
        addressObtainedLock.unlock();
    }

}
