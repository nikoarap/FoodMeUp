package com.example.foodmeup.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

public class LabeledLatLngs implements Parcelable {

    private String lng;

    private String label;

    private String lat;

    private LabeledLatLngs(Parcel in) {
        lng = in.readString();
        label = in.readString();
        lat = in.readString();
    }

    public static final Creator<LabeledLatLngs> CREATOR = new Creator<LabeledLatLngs>() {
        @Override
        public LabeledLatLngs createFromParcel(Parcel in) {
            return new LabeledLatLngs(in);
        }

        @Override
        public LabeledLatLngs[] newArray(int size) {
            return new LabeledLatLngs[size];
        }
    };


    @NotNull
    @Override
    public String toString()
    {
        return "ClassPojo [lng = "+lng+", label = "+label+", lat = "+lat+"]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(lng);
        dest.writeString(label);
        dest.writeString(lat);
    }
}
