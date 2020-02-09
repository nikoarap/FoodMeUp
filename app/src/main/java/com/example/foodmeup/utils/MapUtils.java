//package com.example.foodmeup.utils;
//
//import android.Manifest;
//import android.app.ProgressDialog;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.os.Build;
//import android.os.Looper;
//import android.util.Log;
//import android.view.View;
//import android.widget.Toast;
//
//import com.example.foodmeup.network.APIHandlingService;
//import com.example.foodmeup.network.RetrofitRequestClass;
//import com.example.foodmeup.model.ResponseModel;
//import com.example.foodmeup.model.Venues;
//import com.example.foodmeup.ui.maps.MapsActivity;
//import com.example.foodmeup.R;
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationCallback;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationResult;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//
//import org.jetbrains.annotations.NotNull;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import androidx.annotation.NonNull;
//import androidx.core.content.ContextCompat;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class MapUtils implements GoogleMap.OnCameraIdleListener {
//
//    static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
//
//    public static GoogleMap mMap;
//    private static LocationRequest mLocationRequest;
//    private static List<Location> locationList;
//    private static LatLng latLng = new LatLng(0, 0);
//    private static boolean isFirstTime = true;
//    public static Marker mCurrLocationMarker;
//    private final MapsActivity ma = new MapsActivity();
//    private static String latlong;
//    public static Venues[] venues;
//    public static ArrayList<Venues> venuesList = new ArrayList<>();
//    private static PopulateRecyclerView populateRecyclerView;
//
//    public static FusedLocationProviderClient registerFusedLocationClient(MapsActivity mapsActivity) {
//        return LocationServices.getFusedLocationProviderClient(mapsActivity);
//    }
//
//    public static void setUpMap(
//        GoogleMap googleMap, FusedLocationProviderClient mFusedLocationClient,
//        LocationCallback mLocationCallback, MapsActivity mapsActivity) {
//
//            mMap = googleMap;
//            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//            mMap.getUiSettings().setCompassEnabled(false);
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
//
//            mLocationRequest = new LocationRequest();
//            mLocationRequest.setInterval(2000);
//            mLocationRequest.setFastestInterval(1000);
//            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//            mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter
//            getLocationUpdates(mFusedLocationClient, mapsActivity, mLocationCallback);
//    }
//
//    private static void getLocationUpdates(
//            FusedLocationProviderClient mFusedLocationClient,
//            MapsActivity mapsActivity, LocationCallback mLocationCallback) {
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(
//                    mapsActivity, Manifest.permission.ACCESS_FINE_LOCATION)
//                    == PackageManager.PERMISSION_GRANTED) {
//
//                mFusedLocationClient.requestLocationUpdates(
//                        mLocationRequest, mLocationCallback, Looper.myLooper());
//            } else {
//                PermissionCheck.checkLocationPermission(mapsActivity);
//            }
//        }
//        else {
//            mFusedLocationClient.requestLocationUpdates(
//                    mLocationRequest, mLocationCallback, Looper.myLooper());
//        }
//
//        mMap.setMyLocationEnabled(false);
//
//    }
//
//    public static LocationCallback getLocation(MapsActivity mapsActivity) {
//        return new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                locationList = locationResult.getLocations();
//
//                if (locationList.size() > 0 ) {
//                    if(mMap == null) return;
//
//                    //Removing any marker that is retrieved when MapsActivity is resumed
//                    if (mCurrLocationMarker != null){
//                        mCurrLocationMarker.remove();
//                    }
//
//                    //Fetching the location of the GPS the first time
//                    Location location = locationList.get(locationList.size() - 1);
//                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
//
//                    //Adding the marker in the user's current location the first time
//                    mCurrLocationMarker = mMap.addMarker(new MarkerOptions()
//                            .position(latLng)
//                            .anchor(0.5f, 1.0f)
//                            .infoWindowAnchor(0.5f, -0.2f)
//                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
//
//
//                    //When the user moves the camera, the marker anchors itself to the center of the screen
//                    mMap.setOnCameraMoveListener(() -> {
//                        LatLng midLatLng = mMap.getCameraPosition().target;
//                        if (mCurrLocationMarker!=null) mCurrLocationMarker.setPosition(midLatLng);
//                        else Log.d("TAG","Marker is null");
//                    });
//
//                    //Triggers when the camera motion is stopped by the user.
//                    //When the marker is fixed at a location on the map, the corresponding address is fetched
//                    //and a new marker is created
//                    mMap.setOnCameraIdleListener(() -> {
//
//                        LatLng midLatLng = mMap.getCameraPosition().target;
//
//                        //Converting coordinates to x.x, y.y form for the retrofit request
//                        double lat = midLatLng.latitude;
//                        double lng = midLatLng.longitude;
//                        String lati = Double.toString(lat);
//                        String longi = Double.toString(lng);
//                        latlong = lati + ", " + longi;
//
//                        if (mCurrLocationMarker != null){
//                            mCurrLocationMarker.remove();
//                        }
//
//                        mCurrLocationMarker = mMap.addMarker(new MarkerOptions()
//                                .position(midLatLng)
//                                .anchor(0.5f, 1.0f)
//                                .infoWindowAnchor(0.5f, -0.2f)
//                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
//
//                        new AddressObtainTask(mapsActivity, mapsActivity).execute(midLatLng);
//                    });
//
//                    if (isFirstTime){ // Do not move the camera after the first time of locating
//                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
//                        isFirstTime = false;
//                    }
//                }
//            }
//        };
//    }
//
//    public static void checkPermissionRequestCode(
//        MapsActivity mapsActivity, int requestCode, @NonNull int[] grantResults,
//        FusedLocationProviderClient mFusedLocationClient, LocationCallback mLocationCallback) {
//        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
//            if (grantResults.length > 0 && grantResults[0] ==
//                    PackageManager.PERMISSION_GRANTED) {
//
//                if (ContextCompat.checkSelfPermission(mapsActivity,
//                        Manifest.permission.ACCESS_FINE_LOCATION) ==
//                        PackageManager.PERMISSION_GRANTED) {
//                    getLocationUpdates(
//                            mFusedLocationClient, mapsActivity, mLocationCallback);
//                }
//            } else {
//                mapsActivity.finish();
//            }
//        }
//    }
//
//    //Fetching the venues using retrofit
//    public static void fetchVenues(MapsActivity mapsActivity) {
//
//        APIHandlingService service = RetrofitRequestClass.fetchApi();
//        Call<ResponseModel> call = service.getVenues(latlong, Constants.CATEGORY_ID,Constants.LIMIT,Constants.RADIUS,
//                Constants.CLIENT_ID,Constants.CLIENT_SECRET,Constants.DATE);
//
//        final ProgressDialog progressDialog = new ProgressDialog(mapsActivity,
//                R.style.Theme_AppCompat_DayNight_Dialog);
//        progressDialog.setMessage("Loading venues...");
//        progressDialog.show();
//
//        call.enqueue(new Callback<ResponseModel>() {
//            @Override
//            public void onResponse(@NotNull Call<ResponseModel> call, @NotNull Response<ResponseModel> response) {
//                if (response.body() != null) {
//                    ResponseModel responseModel = response.body();
//                    venues = responseModel.getResponse().getVenues();
//                    venuesList.clear();
//                    if(venues !=null && venues.length > 0){
//                        mMap.clear();
//                        for (Venues venues : venues) {
//                            venuesList.add(venues);
//
//                            double lat = Double.parseDouble(venues.getLocation().getLat());
//                            double lng = Double.parseDouble(venues.getLocation().getLng());
//                            LatLng latLng = new LatLng(lat, lng);
//
//                            mMap.addMarker(new MarkerOptions()
//                                    .position(latLng)
//                                    .anchor(0.5f, 1.0f)
//                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
//
//                            populateRecyclerView = new PopulateRecyclerView(mapsActivity,MapsActivity.recView);
//                            populateRecyclerView.populateSearchView(venuesList, MapsActivity.onVenueListener);
//                        }
//                    }
//                    else{
//                        mMap.clear();
//                        MapsActivity.recView.setVisibility(View.GONE);
//                        Toast.makeText(mapsActivity,"No venues available",Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//
//                progressDialog.dismiss();
//            }
//
//            @Override
//            public void onFailure(@NotNull Call<ResponseModel> call, @NotNull Throwable t) {
//                Log.e("error", "onFailure: ", t);
//                progressDialog.dismiss();
//            }
//
//        });
//
//    }
//
//    @Override
//    public void onCameraIdle() {
//        new AddressObtainTask(ma, ma).execute(latLng);
//    }
//}
