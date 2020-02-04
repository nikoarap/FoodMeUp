package com.example.foodmeup.ui;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodmeup.R;
import com.example.foodmeup.adapters.CarouselAdapter;
import com.example.foodmeup.models.Venues;
import com.example.foodmeup.utils.AddressObtainTask;
import com.example.foodmeup.utils.AlertDialogUtils;
import com.example.foodmeup.utils.ConnectionBroadcastReceiver;
import com.example.foodmeup.utils.Constants;
import com.example.foodmeup.utils.MapUtils;
import com.example.foodmeup.utils.PopulateRecyclerView;
import com.example.foodmeup.utils.Utilities;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, AddressObtainTask.Callback,
        CarouselAdapter.OnVenueListener {

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private ReentrantLock addressObtainedLock;
    public AlertDialog lastAlertDialog;
    private BroadcastReceiver connectionStateReceiver = new ConnectionBroadcastReceiver(this);
    private PopulateRecyclerView populateRecyclerView;
    public static ArrayList<Venues> venuesList = new ArrayList<>();
    private boolean buttonIsPushed = false;

    @BindView(R.id.address_txt) TextView addressText;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.address_layout) RelativeLayout addressLayout;
    @BindView(R.id.recView) RecyclerView recView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //ButterKnife library binding all the necessary views
        ButterKnife.bind(this);

        //Hiding the carousel from th UI
        recView.setVisibility(View.GONE);

        //Custom Toolbar replacing the default one
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(null);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);

        fab.setOnClickListener(v -> {
            venuesList.clear();
            buttonIsPushed = true;
            MapUtils.fetchVenues(this);

            if(venuesList !=null &&venuesList.size() > 0){
                fab.hide();
                populateRecyclerView = new PopulateRecyclerView(Objects.requireNonNull(this),recView);
                populateRecyclerView.populateSearchView(venuesList,this);
                addressLayout.setVisibility(View.GONE);
                addressText.setVisibility(View.GONE);
                recView.setVisibility(View.VISIBLE);
                MapUtils.mCurrLocationMarker.remove();
            }
            else{
                Toast.makeText(MapsActivity.this,"No venues available",Toast.LENGTH_SHORT).show();
            }

        });

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

    @Override
    public void onVenueClick(int position) {

        double lat = Double.parseDouble(venuesList.get(position).getLocation().getLat());
        double lng = Double.parseDouble(venuesList.get(position).getLocation().getLng());
        LatLng latLng = new LatLng(lat,lng);
        MapUtils.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

    }


    @Override
    public void onBackPressed() {
        if(buttonIsPushed){
            fab.show();
            addressLayout.setVisibility(View.VISIBLE);
            addressText.setVisibility(View.VISIBLE);
            recView.setVisibility(View.GONE);
            buttonIsPushed = false;
        }
        else{
            if(Constants.backButtonCount >= 1) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            }
            else {
                Toast.makeText(MapsActivity.this,
                        R.string.press_back, Toast.LENGTH_SHORT).show();
                Constants.backButtonCount++;
            }
        }

    }

}
