package com.example.foodmeup.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

public class Categories implements Parcelable {

    private String pluralName;

    private String name;

    private Icon icon;

    private String id;

    private String shortName;

    private String primary;

    private Categories(Parcel in) {
        pluralName = in.readString();
        name = in.readString();
        id = in.readString();
        shortName = in.readString();
        primary = in.readString();
    }

    public static final Creator<Categories> CREATOR = new Creator<Categories>() {
        @Override
        public Categories createFromParcel(Parcel in) {
            return new Categories(in);
        }

        @Override
        public Categories[] newArray(int size) {
            return new Categories[size];
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

    public Icon getIcon ()
    {
        return icon;
    }

    @NotNull
    @Override
    public String toString()
    {
        return "ClassPojo [pluralName = "+pluralName+", name = "+name+", icon = "+icon+", id = "+id+", shortName = "+shortName+", primary = "+primary+"]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pluralName);
        dest.writeString(name);
        dest.writeString(id);
        dest.writeString(shortName);
        dest.writeString(primary);
    }

}
