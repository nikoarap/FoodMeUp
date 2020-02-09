package com.example.foodmeup.model.repository;

import android.util.Log;

import com.example.foodmeup.model.ResponseModel;
import com.example.foodmeup.model.Venues;
import com.example.foodmeup.network.RetrofitRequestClass;
import com.example.foodmeup.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

public class FetchVenuesRepository {

    private static final String TAG = "DataRepository";

    private FetchVenuesRunnable fetchVenuesRunnable;
    private ArrayList<Venues> venuesList;

    public ArrayList<Venues> getVenues() {
        return venuesList;
    }

    //Server request for venues
    public void venuesRequest(String latlng, String categoryId, String limit,
                              String radius, String client_id, String client_secret, String date){
        if(fetchVenuesRunnable != null){
            fetchVenuesRunnable = null;
        }
        fetchVenuesRunnable = new FetchVenuesRunnable(latlng, categoryId, limit, radius, client_id, client_secret, date);
        final Future handler = AppExecutors.getInstance().getExec().submit(fetchVenuesRunnable);

        //stop the request after 3 seconds
        AppExecutors.getInstance().getExec().schedule(() -> {
            handler.cancel(true);
        }, Constants.NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    // background thread that will handle the request to the server
    private class FetchVenuesRunnable implements Runnable {

        private String latlng, categoryId, limit, radius, client_id, client_secret, date;

        private FetchVenuesRunnable(String latlng, String categoryId, String limit, String radius,
                                    String client_id, String client_secret, String date) {
            this.latlng = latlng;
            this.categoryId = categoryId;
            this.limit = limit;
            this.radius = radius;
            this.client_id = client_id;
            this.client_secret = client_secret;
            this.date = date;
        }

        @Override
        public void run() { // actual request happens inside the background thread
            try {
                Response<ResponseModel> response = fetchVenues(latlng,categoryId,limit,radius,client_id,client_secret,date).execute();
                if (response.code() == 200 && response.body() != null){
                    ResponseModel responseModel = response.body();
                    Venues[] venues1 = responseModel.getResponse().getVenues();
                    venuesList = new ArrayList<>(Arrays.asList(venues1));
                }
                else{
                    assert response.errorBody() != null;
                    String error = response.errorBody().string();
                    Log.e(TAG, "run: " + error);
                }
            } catch (IOException e) {
                e.printStackTrace();

            }
        }



        //the call to fetch the json data from the api
        private Call<ResponseModel> fetchVenues(String latlng, String categoryId, String limit, String radius,
                                                String client_id, String client_secret, String date) {

            return RetrofitRequestClass.fetchApi().getVenues(latlng,categoryId,limit,radius,client_id,client_secret,date);
        }
    }
}
