package com.example.foodmeup.ui;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodmeup.R;
import com.example.foodmeup.adapters.CarouselAdapter;
import com.example.foodmeup.models.Categories;
import com.example.foodmeup.utils.AddressObtainTask;
import com.example.foodmeup.utils.AlertDialogUtils;
import com.example.foodmeup.utils.ConnectionBroadcastReceiver;
import com.example.foodmeup.utils.Constants;
import com.example.foodmeup.utils.MapUtils;
import com.example.foodmeup.utils.Utilities;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, AddressObtainTask.Callback {

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private ReentrantLock addressObtainedLock;
    public AlertDialog lastAlertDialog;
    private BroadcastReceiver connectionStateReceiver = new ConnectionBroadcastReceiver(this);
    private boolean buttonIsPushed = false;
    public static RecyclerView recView;
    public static CarouselAdapter.OnVenueListener onVenueListener;


    @BindView(R.id.address_txt) TextView addressText;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.address_layout) RelativeLayout addressLayout;
    @BindView(R.id.img_butt) Button venueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //ButterKnife library binding all the necessary views
        ButterKnife.bind(this);

        recView = findViewById((R.id.recView));
        fab = findViewById((R.id.fab));

        //Hiding the carousel and the button from th UI
        recView.setVisibility(View.GONE);
        venueButton.setVisibility(View.GONE);

        //Custom Toolbar replacing the default one
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(null);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);

        fab.setOnClickListener(v -> fabClick());

        onVenueListener = this::onVenueClick;
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
    public void onBackPressed() {
        if(buttonIsPushed){
            fab.show();
            addressLayout.setVisibility(View.VISIBLE);
            addressText.setVisibility(View.VISIBLE);
            recView.setVisibility(View.GONE);
            venueButton.setVisibility(View.GONE);
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

    private void fabClick(){
        buttonIsPushed = true;
        MapUtils.fetchVenues(this);

        if(MapUtils.venuesList !=null && MapUtils.venuesList.size() > 0){

            fab.hide();
            addressLayout.setVisibility(View.GONE);
            addressText.setVisibility(View.GONE);
            recView.setVisibility(View.VISIBLE);

            hideButtonWhenScrolling();
            MapUtils.mCurrLocationMarker.remove();
        }
        else{
            MapUtils.mMap.clear();
            MapUtils.fetchVenues(this);
            fab.hide();
            addressLayout.setVisibility(View.GONE);
            addressText.setVisibility(View.GONE);
            recView.setVisibility(View.VISIBLE);
            hideButtonWhenScrolling();
        }
    }

    private void onVenueClick(int position){

        Categories[] categories = MapUtils.venuesList.get(position).getCategories();

        String pre = categories[0].getIcon().getPrefix();
        String suff = "bg_88.png";
        String iconUrl = pre+suff;

        double lat = Double.parseDouble(MapUtils.venuesList.get(position).getLocation().getLat());
        double lng = Double.parseDouble(MapUtils.venuesList.get(position).getLocation().getLng());
        LatLng latLng = new LatLng(lat,lng);
        MapUtils.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

        venueButton.setVisibility(View.VISIBLE);
        venueButton.setOnClickListener(v -> {
            Intent i = new Intent(MapsActivity.this, VenueDetailsActivity.class);
            i.putExtra("img", iconUrl);
            i.putExtra("name", MapUtils.venuesList.get(position).getName());
            i.putExtra("category", categories[0].getName());
            i.putExtra("address", MapUtils.venuesList.get(position).getLocation().getAddress());
            startActivity(i);
        });
    }

    //Hiding the EnterVenue button when the user scrolls on the recyclerView
    private void hideButtonWhenScrolling(){
        recView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                venueButton.setVisibility(View.GONE);
            }
        });
    }

}
