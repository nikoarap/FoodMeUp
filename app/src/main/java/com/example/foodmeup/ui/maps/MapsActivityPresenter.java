package com.example.foodmeup.ui.maps;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Looper;
import android.util.Log;

import com.example.foodmeup.R;
import com.example.foodmeup.model.Categories;
import com.example.foodmeup.model.ResponseModel;
import com.example.foodmeup.model.Venues;
import com.example.foodmeup.network.APIHandlingService;
import com.example.foodmeup.network.RetrofitRequestClass;
import com.example.foodmeup.ui.venueDetails.VenueDetailsActivity;
import com.example.foodmeup.ui.base.BasePresenter;
import com.example.foodmeup.utils.AddressObtainTask;
import com.example.foodmeup.utils.AlertDialogUtils;
import com.example.foodmeup.utils.Constants;
import com.example.foodmeup.utils.PopulateRecyclerView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.foodmeup.utils.Constants.MY_PERMISSIONS_REQUEST_LOCATION;

public class MapsActivityPresenter extends BasePresenter<MapsActivityView> implements GoogleMap.OnCameraIdleListener {

    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private List<Location> locationList;
    private LatLng latLng = new LatLng(0, 0);
    private boolean isFirstTime = true;
    private Marker mCurrLocationMarker;
    private String latlong;
    private ArrayList<Venues> venuesList = new ArrayList<>();
    private Venues[] venues;
    private MapsActivity mapsActivity;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private ReentrantLock addressObtainedLock;
    private AlertDialog lastAlertDialog;
    private PopulateRecyclerView populateRecyclerView;
    private boolean buttonIsPushed = false;

    //Instance of the view and repository to communicate with the presenter
    MapsActivityPresenter(MapsActivityView view, MapsActivity mapsActivity) {
        super(view);
        this.mapsActivity = mapsActivity;
    }

    private FusedLocationProviderClient registerFusedLocationClient() {
        return LocationServices.getFusedLocationProviderClient(mapsActivity);
    }

    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    void obtainMapFragment(){
        SupportMapFragment mapFragment = (SupportMapFragment) mapsActivity.getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null)
            mapFragment.getMapAsync(mapsActivity);
    }

    void setUpMap(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter
        getLocationUpdates(mFusedLocationClient, mapsActivity, mLocationCallback);
    }

    private void getLocationUpdates(
            FusedLocationProviderClient mFusedLocationClient,
            MapsActivity mapsActivity, LocationCallback mLocationCallback) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    mapsActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                mFusedLocationClient.requestLocationUpdates(
                        mLocationRequest, mLocationCallback, Looper.myLooper());
            } else {
                checkLocationPermission();
            }
        }
        else {
            mFusedLocationClient.requestLocationUpdates(
                    mLocationRequest, mLocationCallback, Looper.myLooper());
        }
        mMap.setMyLocationEnabled(false);
    }

    private LocationCallback getLocation(MapsActivity mapsActivity) {
        return new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                locationList = locationResult.getLocations();

                if (locationList.size() > 0 ) {
                    if(mMap == null) return;

                    //Removing any marker that is retrieved when MapsActivity is resumed
                    if (mCurrLocationMarker != null){
                        mCurrLocationMarker.remove();
                    }

                    //Fetching the location of the GPS the first time
                    Location location = locationList.get(locationList.size() - 1);
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());

                    //Adding the marker in the user's current location the first time
                    mCurrLocationMarker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .anchor(0.5f, 1.0f)
                            .infoWindowAnchor(0.5f, -0.2f)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));

                    //When the user moves the camera, the marker anchors itself to the center of the screen
                    mMap.setOnCameraMoveListener(() -> {
                        view.hideVenuesShowAddress();
                        LatLng midLatLng = mMap.getCameraPosition().target;
                        if (mCurrLocationMarker!=null) mCurrLocationMarker.setPosition(midLatLng);
                        else Log.d("TAG","Marker is null");
                    });

                    //Triggers when the camera motion is stopped by the user.
                    //When the marker is fixed at a location on the map, the corresponding address is fetched
                    //and a new marker is created
                    mMap.setOnCameraIdleListener(() -> {

                        LatLng midLatLng = mMap.getCameraPosition().target;

                        //Converting coordinates to x.x, y.y form for the retrofit request
                        double lat = midLatLng.latitude;
                        double lng = midLatLng.longitude;
                        String lati = Double.toString(lat);
                        String longi = Double.toString(lng);
                        latlong = lati + ", " + longi;

                        if (mCurrLocationMarker != null){
                            mCurrLocationMarker.remove();
                        }

                        mCurrLocationMarker = mMap.addMarker(new MarkerOptions()
                                .position(midLatLng)
                                .anchor(0.5f, 1.0f)
                                .infoWindowAnchor(0.5f, -0.2f)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));

                        new AddressObtainTask(mapsActivity, mapsActivity).execute(midLatLng);
                    });

                    if (isFirstTime){ // Do not move the camera after the first time of locating
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                        isFirstTime = false;
                    }
                }
            }
        };
    }

    void checkPermissionRequestCode(
            MapsActivity mapsActivity, int requestCode, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(mapsActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
                    getLocationUpdates(
                            mFusedLocationClient, mapsActivity, mLocationCallback);
                }
            }
        }
    }

    private void registerReceivers(BroadcastReceiver connectionStateReceiver) {
        IntentFilter filterGps = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        filterGps.addAction(Intent.ACTION_PROVIDER_CHANGED);
        mapsActivity.registerReceiver(connectionStateReceiver, filterGps);

        IntentFilter filterNetwork = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filterNetwork.addAction(Intent.ACTION_PROVIDER_CHANGED);
        mapsActivity.registerReceiver(connectionStateReceiver, filterNetwork);
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(mapsActivity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(mapsActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION );
        }
    }

    void onStartHide(){
        view.hideVenues();
    }

    void onResume(){
        mFusedLocationClient = registerFusedLocationClient();
        obtainMapFragment();
        addressObtainedLock = new ReentrantLock();
        lastAlertDialog = AlertDialogUtils.alertCheckConnectivityAvailability(lastAlertDialog, mapsActivity);
        registerReceivers(mapsActivity.connectionStateReceiver);
        mLocationCallback = getLocation(mapsActivity);
    }

    void onPause(){
        if (mFusedLocationClient != null)
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            mapsActivity.unregisterReceiver(mapsActivity.connectionStateReceiver);
    }

    void onAddressObtained(String address){
        addressObtainedLock.lock();
        view.setAdressText(address);
        addressObtainedLock.unlock();
    }

    //Fetching the venues directly from the presenter and not from the repository for the moment
    private void fetchVenues() {
        APIHandlingService service = RetrofitRequestClass.fetchApi();
        Call<ResponseModel> call = service.getVenues(latlong, Constants.CATEGORY_ID,Constants.LIMIT,Constants.RADIUS,
                Constants.CLIENT_ID,Constants.CLIENT_SECRET,Constants.DATE);

        final ProgressDialog progressDialog = new ProgressDialog(mapsActivity,
                R.style.Theme_AppCompat_DayNight_Dialog);
        progressDialog.setMessage("Loading venues...");
        progressDialog.show();

        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(@NotNull Call<ResponseModel> call, @NotNull Response<ResponseModel> response) {
                if (response.body() != null) {
                    ResponseModel responseModel = response.body();
                    venues = responseModel.getResponse().getVenues();
                    venuesList.clear();
                    if(venues !=null && venues.length > 0){
                        mMap.clear();
                        for (Venues venues : venues) {
                            venuesList.add(venues);

                            double lat = Double.parseDouble(venues.getLocation().getLat());
                            double lng = Double.parseDouble(venues.getLocation().getLng());
                            LatLng latLng = new LatLng(lat, lng);

                            mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .anchor(0.5f, 1.0f)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                        }

                        populateRecyclerView = new PopulateRecyclerView(mapsActivity,mapsActivity.recView);
                        populateRecyclerView.populateSearchView(venuesList, mapsActivity.onVenueListener);
                    }
                    else{
                        view.hideVenuesShowAddress();
                        view.messageNoVenuesAvailable();
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(@NotNull Call<ResponseModel> call, @NotNull Throwable t) {
                Log.e("error", "onFailure: ", t);
                progressDialog.dismiss();
            }
        });
    }

    void onFabClick() {
        buttonIsPushed = true;
        fetchVenues();
        if(venuesList !=null && venuesList.size() > 0){
            view.hideAdressShowVenues();
            hideButtonWhenScrolling();
            mCurrLocationMarker.remove();
        }
        else{
            mMap.clear();
            fetchVenues();
            view.hideAdressShowVenues();
            hideButtonWhenScrolling();
        }
    }

    void onVenueClick(int position){
        Categories[] categories = venuesList.get(position).getCategories();

        String pre = categories[0].getIcon().getPrefix();
        String suff = "bg_88.png";
        String iconUrl = pre+suff;

        double lat = Double.parseDouble(venuesList.get(position).getLocation().getLat());
        double lng = Double.parseDouble(venuesList.get(position).getLocation().getLng());
        LatLng latLng = new LatLng(lat,lng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

        view.showVenueButton();

        mapsActivity.venueButton.setOnClickListener(v -> {
            Intent i = new Intent(mapsActivity, VenueDetailsActivity.class);
            i.putExtra("id",venuesList.get(position).getId());
            i.putExtra("img", iconUrl);
            i.putExtra("name", venuesList.get(position).getName());
            i.putExtra("category", categories[0].getName());
            i.putExtra("address", venuesList.get(position).getLocation().getAddress());
            mapsActivity.startActivity(i);
        });
    }

    void onBackPressed() {
        if(buttonIsPushed){
            view.hideVenuesShowAddress();
            buttonIsPushed = false;
        }
        else{
            if(Constants.backButtonCount >= 1) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                mapsActivity.startActivity(intent);
            }
            else {
                view.messagePressExit();
                Constants.backButtonCount++;
            }
        }
    }

    //Hiding the EnterVenue button when the user scrolls on the recyclerView
    private void hideButtonWhenScrolling(){
        mapsActivity.recView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                view.hideVenueButton();
            }
        });
    }

    @Override
    public void onCameraIdle() {
        new AddressObtainTask(mapsActivity, mapsActivity).execute(latLng);
    }
}
