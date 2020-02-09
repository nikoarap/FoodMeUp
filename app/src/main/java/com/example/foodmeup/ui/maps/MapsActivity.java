package com.example.foodmeup.ui.maps;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodmeup.R;
import com.example.foodmeup.adapters.CarouselAdapter;
import com.example.foodmeup.ui.base.BaseActivity;
import com.example.foodmeup.utils.AddressObtainTask;
import com.example.foodmeup.utils.ConnectionBroadcastReceiver;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MapsActivity extends BaseActivity<MapsActivityPresenter> implements MapsActivityView,
        OnMapReadyCallback, AddressObtainTask.Callback {

    public CarouselAdapter.OnVenueListener onVenueListener;
    BroadcastReceiver connectionStateReceiver = new ConnectionBroadcastReceiver(this);
    public AlertDialog lastAlertDialog;

    @BindView(R.id.address_txt) TextView addressText;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.address_layout) RelativeLayout addressLayout;
    @BindView(R.id.img_butt) Button venueButton;
    @BindView(R.id.recView) RecyclerView recView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        presenter.onStartHide();
        presenter.obtainMapFragment();

        //Custom Toolbar replacing the default one
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(null);

        presenter.obtainMapFragment();

        //Floating Action Button listener
        fab.setOnClickListener(v -> presenter.onFabClick());

        //RecyclerView item listener
        onVenueListener = this::onVenueClick;
    }

    //Presenter of the activity
    @NonNull
    @Override
    protected MapsActivityPresenter createPresenter() {
        return new MapsActivityPresenter(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        presenter.setUpMap(googleMap);
    }

    @Override
    public void onAddressObtained(@NonNull String address) {
        presenter.onAddressObtained(address);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        presenter.checkPermissionRequestCode(this, requestCode, grantResults);
    }

    @Override
    public void onBackPressed() {
        presenter.onBackPressed();
    }

    private void onVenueClick(int position){
        presenter.onVenueClick(position);
    }

    //View interfacee implementations
    @Override
    public void hideAdressShowVenues() {
        fab.hide();
        addressLayout.setVisibility(View.GONE);
        addressText.setVisibility(View.GONE);
        recView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideVenuesShowAddress() {
        fab.show();
        addressLayout.setVisibility(View.VISIBLE);
        addressText.setVisibility(View.VISIBLE);
        recView.setVisibility(View.GONE);
        venueButton.setVisibility(View.GONE);
    }

    @Override
    public void hideVenues() {
        recView.setVisibility(View.GONE);
        venueButton.setVisibility(View.GONE);
    }

    @Override
    public void setAdressText(String address) {
        addressText.setText(address);
    }

    @Override
    public void messageNoVenuesAvailable() {
        Toast.makeText(this,"No venues available",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideVenueButton() {
        venueButton.setVisibility(View.GONE);
    }

    @Override
    public void messagePressExit() {
        Toast.makeText(MapsActivity.this, R.string.press_back, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showVenueButton() {
        venueButton.setVisibility(View.VISIBLE);
    }
}
