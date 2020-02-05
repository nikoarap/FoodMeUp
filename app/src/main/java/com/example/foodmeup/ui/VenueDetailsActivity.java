package com.example.foodmeup.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.foodmeup.R;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;

public class VenueDetailsActivity extends AppCompatActivity {

    @BindView (R.id.venue_img) ImageView venueImg;
    @BindView (R.id.venue_name) TextView venueName;
    @BindView (R.id.venue_category) TextView venueCategory;
    @BindView (R.id.venue_address) TextView venueAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_det_layout);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(null);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_arrowz));
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        //gets intent from previous activity along with the passed values
        Intent i = getIntent();
        String iconUrl = i.getStringExtra("img");
        venueName.setText(i.getStringExtra("name"));
        venueCategory.setText(i.getStringExtra("category"));
        venueAddress.setText(i.getStringExtra("address"));

        Glide.with(this)
                .asBitmap()
                .load(iconUrl)
                .into(venueImg);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
