package com.example.foodmeup.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class Venues implements Parcelable {

    @SerializedName("hasPerk")
    @Expose
    private String hasPerk;

    @SerializedName("referralId")
    @Expose
    private String referralId;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("location")
    @Expose
    private Location location;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("categories")
    @Expose
    private Categories[] categories;

    private Venues(Parcel in) {
        hasPerk = in.readString();
        referralId = in.readString();
        name = in.readString();
        location = in.readParcelable(Location.class.getClassLoader());
        id = in.readString();
        categories = in.createTypedArray(Categories.CREATOR);
    }

    public static final Creator<Venues> CREATOR = new Creator<Venues>() {
        @Override
        public Venues createFromParcel(Parcel in) {
            return new Venues(in);
        }

        @Override
        public Venues[] newArray(int size) {
            return new Venues[size];
        }
    };

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public Location getLocation ()
    {
        return location;
    }

    public Categories[] getCategories ()
    {
        return categories;
    }


    @NotNull
    @Override
    public String toString()
    {
        return "ClassPojo [hasPerk = "+hasPerk+", referralId = "+referralId+", name = "+name+", location = "+location+", id = "+id+", categories = "+ Arrays.toString(categories) +"]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(hasPerk);
        dest.writeString(referralId);
        dest.writeString(name);
        dest.writeParcelable(location, flags);
        dest.writeString(id);
        dest.writeTypedArray(categories, flags);
    }
}
