package com.example.foodmeup.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;

public class AddressObtainTask extends AsyncTask<LatLng, Void, String> {

    private final WeakReference<Context> contextWeakReference;
    private final WeakReference<Callback> callbackWeakReference;

    AddressObtainTask(Context context, Callback callback) {
        this.contextWeakReference = new WeakReference<>(context);
        this.callbackWeakReference = new WeakReference<>(callback);
    }

    @Override
    protected String doInBackground(LatLng... latLngs) {
        if(latLngs.length == 0)
            return null;

        LatLng latLng = latLngs[0];

        final Context context = contextWeakReference.get();
        if(context == null)
            return null;

        List<Address> addresses = null;
        try {
            addresses = new Geocoder(context, Locale.getDefault())
                    .getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null && !addresses.isEmpty()) {
            //Getting the Street and Number only
            String address = addresses.get(0).getAddressLine(0);
            address = address.split(",")[0];
            return address;
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        final Callback callback = callbackWeakReference.get();
        if(result != null && callback != null)
            callback.onAddressObtained(result);
    }

    public interface Callback {
        void onAddressObtained(@NonNull String address);
    }
}