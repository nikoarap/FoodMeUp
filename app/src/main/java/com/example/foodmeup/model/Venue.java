package com.example.foodmeup.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Venue implements Parcelable {

    private Photos photos;

    private String id;


    private Venue(Parcel in) {
        photos = in.readParcelable(Photos.class.getClassLoader());
        id = in.readString();
    }

    public static final Creator<Venue> CREATOR = new Creator<Venue>() {
        @Override
        public Venue createFromParcel(Parcel in) {
            return new Venue(in);
        }

        @Override
        public Venue[] newArray(int size) {
            return new Venue[size];
        }
    };

    public Photos getPhotos ()
    {
        return photos;
    }

    public void setPhotos (Photos photos)
    {
        this.photos = photos;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(photos, flags);
        dest.writeString(id);
    }
}