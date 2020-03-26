package com.example.foodmeup.ui.venueDetails;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.foodmeup.R;
import com.example.foodmeup.ui.base.BaseActivity;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;

public class VenueDetailsActivity extends BaseActivity<VenueDetailsActivityPresenter> implements VenueDetailsActivityView {

    public String venueId;
    public String imgUrl;


    @BindView (R.id.venue_img) ImageView venueImg;
    @BindView (R.id.venue_name) TextView venueName;
    @BindView (R.id.venue_category) TextView venueCategory;
    @BindView (R.id.venue_address) TextView venueAddress;
    @BindView (R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_det_layout);
        ButterKnife.bind(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(null);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_arrowz));
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        presenter.getIntent();
        presenter.setVenuePhoto();

    }

    @NonNull
    @Override
    protected VenueDetailsActivityPresenter createPresenter() {
        return new VenueDetailsActivityPresenter(this, this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
