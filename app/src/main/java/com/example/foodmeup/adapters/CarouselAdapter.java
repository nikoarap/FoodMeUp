package com.example.foodmeup.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.foodmeup.R;
import com.example.foodmeup.model.Categories;
import com.example.foodmeup.model.Venues;
import com.github.islamkhsh.CardSliderAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class CarouselAdapter extends CardSliderAdapter<CarouselAdapter.CarouselViewHolder> {

    private ArrayList<Venues> venues;
    private Context context;
    private OnVenueListener onVenueListener;
    private static final String TAG = "CarouselAdapter";

    public CarouselAdapter(ArrayList<Venues> venues, OnVenueListener onVenueListener, Context cont){
        this.venues = venues;
        this.onVenueListener = onVenueListener;
        context = cont;
    }

    @Override
    public int getItemCount() {
        if (null == venues) return 0;
        return venues.size();

    }

    @NonNull
    @Override
    public CarouselViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.venue_list_item_layout, parent, false);
        return new CarouselViewHolder(view, onVenueListener);
    }


    @Override
    public void bindVH(@NotNull CarouselViewHolder viewHolder, int position) {

        Categories[] categories = venues.get(position).getCategories();
        String pre = categories[0].getIcon().getPrefix();
        String suff = "bg_88.png";
        String iconUrl = pre+suff;


        Glide.with(context)
                .asBitmap()
                .load(iconUrl)
                .into(viewHolder.venueImg);

        viewHolder.venueName.setText(venues.get(position).getName());
        viewHolder.venueCategory.setText(categories[0].getName());
        viewHolder.venueAddress.setText(venues.get(position).getLocation().getAddress());

    }

    class CarouselViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnVenueListener onVenueListener;
        ImageView venueImg;
        TextView venueName;
        TextView venueCategory;
        TextView venueAddress;

        private CarouselViewHolder(View v, OnVenueListener venueListener){
            super(v);
            venueImg = v.findViewById(R.id.venue_img);
            venueName = v.findViewById(R.id.venue_name);
            venueCategory = v.findViewById(R.id.venue_category);
            venueAddress = v.findViewById(R.id.venue_address);
            onVenueListener = venueListener;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "Item number: " + getAdapterPosition());
            onVenueListener.onVenueClick(getAdapterPosition());
        }
    }

        public interface OnVenueListener {
            void onVenueClick(int position);
    }
}
