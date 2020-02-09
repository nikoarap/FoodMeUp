package com.example.foodmeup.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class Location implements Parcelable {

    private String cc;

    private String country;

    private String address;

    private LabeledLatLngs[] labeledLatLngs;

    private String lng;

    private String distance;

    private String[] formattedAddress;

    private String city;

    private String state;

    private String lat;

    private Location(Parcel in) {
        cc = in.readString();
        country = in.readString();
        address = in.readString();
        labeledLatLngs = in.createTypedArray(LabeledLatLngs.CREATOR);
        lng = in.readString();
        distance = in.readString();
        formattedAddress = in.createStringArray();
        city = in.readString();
        state = in.readString();
        lat = in.readString();
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    public String getAddress ()
    {
        return address;
    }

    public String getLng ()
    {
        return lng;
    }

    public String getLat ()
    {
        return lat;
    }

    @NotNull
    @Override
    public String toString()
    {
        return "ClassPojo [cc = "+cc+", country = "+country+", address = "+address+", labeledLatLngs = "+ Arrays.toString(labeledLatLngs) +", lng = "+lng+", distance = "+distance+", formattedAddress = "+ Arrays.toString(formattedAddress) +", city = "+city+", state = "+state+", lat = "+lat+"]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cc);
        dest.writeString(country);
        dest.writeString(address);
        dest.writeTypedArray(labeledLatLngs, flags);
        dest.writeString(lng);
        dest.writeString(distance);
        dest.writeStringArray(formattedAddress);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeString(lat);
    }
}
