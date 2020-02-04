package com.example.foodmeup.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import com.example.foodmeup.adapters.CarouselAdapter;
import com.example.foodmeup.models.Venues;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

@SuppressLint("ViewConstructor")
public class PopulateRecyclerView extends RecyclerView {

    private RecyclerView recView;

    public PopulateRecyclerView(@NonNull Context context, RecyclerView recView) {
        super(context);
        this.recView = recView;
    }

    public void populateSearchView(ArrayList<Venues> venueList, CarouselAdapter.OnVenueListener recViewListener){
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        recView.setLayoutManager(linearLayoutManager);
        CarouselAdapter recAdapter = new CarouselAdapter(venueList, recViewListener, getContext());
        recView.setAdapter(recAdapter);
        recAdapter.notifyDataSetChanged();
        recView.scheduleLayoutAnimation();
    }
}
