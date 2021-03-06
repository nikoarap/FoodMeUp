package com.example.foodmeup.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Response implements Parcelable {

    @SerializedName("venues")
    @Expose
    private Venues[] venues;

    @SerializedName("venue")
    @Expose
    private Venue venue;


    private Response(Parcel in) {
        venues = in.createTypedArray(Venues.CREATOR);
        venue = in.readParcelable(Venue.class.getClassLoader());
    }

    public static final Creator<Response> CREATOR = new Creator<Response>() {
        @Override
        public Response createFromParcel(Parcel in) {
            return new Response(in);
        }

        @Override
        public Response[] newArray(int size) {
            return new Response[size];
        }
    };

    public Venues[] getVenues ()
    {
        return venues;
    }

    public Venue getVenue ()
    {
        return venue;
    }

    public void setVenue (Venue venue)
    {
        this.venue = venue;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(venues, flags);
        dest.writeParcelable(venue, flags);
    }
}
