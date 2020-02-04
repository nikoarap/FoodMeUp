package com.example.foodmeup.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

public class Meta implements Parcelable {

    private String code;

    private String requestId;

    private Meta(Parcel in) {
        code = in.readString();
        requestId = in.readString();
    }

    public static final Creator<Meta> CREATOR = new Creator<Meta>() {
        @Override
        public Meta createFromParcel(Parcel in) {
            return new Meta(in);
        }

        @Override
        public Meta[] newArray(int size) {
            return new Meta[size];
        }
    };

    @NotNull
    @Override
    public String toString()
    {
        return "ClassPojo [code = "+code+", requestId = "+requestId+"]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeString(requestId);
    }
}
