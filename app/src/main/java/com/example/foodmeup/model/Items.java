package com.example.foodmeup.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Items implements Parcelable {

    private String createdAt;

    private String visibility;

    private String prefix;

    private String width;

    private String id;

    private String suffix;

    private String height;

    private Items(Parcel in) {
        createdAt = in.readString();
        visibility = in.readString();
        prefix = in.readString();
        width = in.readString();
        id = in.readString();
        suffix = in.readString();
        height = in.readString();
    }

    public static final Creator<Items> CREATOR = new Creator<Items>() {
        @Override
        public Items createFromParcel(Parcel in) {
            return new Items(in);
        }

        @Override
        public Items[] newArray(int size) {
            return new Items[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(createdAt);
        dest.writeString(visibility);
        dest.writeString(prefix);
        dest.writeString(width);
        dest.writeString(id);
        dest.writeString(suffix);
        dest.writeString(height);
    }
}
