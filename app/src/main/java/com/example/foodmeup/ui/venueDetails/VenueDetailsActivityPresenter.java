package com.example.foodmeup.ui.venueDetails;

import android.content.Intent;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.example.foodmeup.model.Groups;
import com.example.foodmeup.model.Items;
import com.example.foodmeup.model.ResponseModel;
import com.example.foodmeup.network.APIHandlingService;
import com.example.foodmeup.network.RetrofitRequestClass;
import com.example.foodmeup.ui.base.BasePresenter;
import com.example.foodmeup.utils.Constants;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VenueDetailsActivityPresenter extends BasePresenter<VenueDetailsActivityView> {

    private VenueDetailsActivity venueDetailsActivity;

    VenueDetailsActivityPresenter(VenueDetailsActivityView view, VenueDetailsActivity venueDetailsActivity) {
        super(view);
        this.venueDetailsActivity = venueDetailsActivity;
    }

    void getIntent(){
        Intent i = venueDetailsActivity.getIntent();
        venueDetailsActivity.venueId = i.getStringExtra("id");
        venueDetailsActivity.imgUrl = i.getStringExtra("img");
        venueDetailsActivity.venueName.setText(i.getStringExtra("name"));
        venueDetailsActivity.venueCategory.setText(i.getStringExtra("category"));
        venueDetailsActivity.venueAddress.setText(i.getStringExtra("address"));
    }

    void setVenuePhoto(){
        APIHandlingService service = RetrofitRequestClass.fetchApi();
        Call<ResponseModel> call = service.getVenuesPhoto(venueDetailsActivity.venueId, Constants.CLIENT_ID,Constants.CLIENT_SECRET,Constants.DATE);

        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(@NotNull Call<ResponseModel> call, @NotNull Response<ResponseModel> response) {
                if (response.body() != null) {
                    ResponseModel responseModel = response.body();
                    Groups[] groups  = responseModel.getResponse().getVenue().getPhotos().getGroups();
                    if(groups.length <= 0){
                        Glide.with(venueDetailsActivity)
                                .asBitmap()
                                .load(venueDetailsActivity.imgUrl)
                                .into(venueDetailsActivity.venueImg);
                    }
                    else{
                        Items[] items = groups[0].getItems();
                        String prefix = items[0].getPrefix();
                        String suffix = items[0].getSuffix();
                        String width = items[0].getWidth();
                        String height = items[0].getHeight();
                        String imgUrl = prefix+width+"x"+height+suffix;

                        Glide.with(venueDetailsActivity)
                                .asBitmap()
                                .centerCrop()
                                .load(imgUrl)
                                .into(venueDetailsActivity.venueImg);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseModel> call, @NotNull Throwable t) {
                Log.e("error", "onFailure: ", t);
            }
        });
    }
}
