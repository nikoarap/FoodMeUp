package com.example.foodmeup.network;

import com.example.foodmeup.model.ResponseModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIHandlingService {

    //Get nearby venues
    @GET("search")
    Call<ResponseModel> getVenues(
            @Query("ll") String latlng,
            @Query("categoryId") String categoryId,
            @Query("limit") String limit,
            @Query("radius") String radius,
            @Query("client_id") String client_id,
            @Query("client_secret") String client_secret,
            @Query("v") String date
    );

}
