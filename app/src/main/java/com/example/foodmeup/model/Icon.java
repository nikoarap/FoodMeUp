package com.example.foodmeup.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

public class Icon implements Parcelable {

    private String prefix;

    private String suffix;

    private Icon(Parcel in) {
        prefix = in.readString();
        suffix = in.readString();
    }

    public static final Creator<Icon> CREATOR = new Creator<Icon>() {
        @Override
        public Icon createFromParcel(Parcel in) {
            return new Icon(in);
        }

        @Override
        public Icon[] newArray(int size) {
            return new Icon[size];
        }
    };

    public String getPrefix ()
    {
        return prefix;
    }

    @NotNull
    @Override
    public String toString()
    {
        return "ClassPojo [prefix = "+prefix+", suffix = "+suffix+"]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(prefix);
        dest.writeString(suffix);
    }
}
