package com.example.foodmeup.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

public class ResponseModel implements Parcelable {

    @SerializedName("meta")
    @Expose
    private Meta meta;

    @SerializedName("response")
    @Expose
    private Response response;

    private ResponseModel(Parcel in) {
        meta = in.readParcelable(Meta.class.getClassLoader());
        response = in.readParcelable(Response.class.getClassLoader());
    }

    public static final Creator<ResponseModel> CREATOR = new Creator<ResponseModel>() {
        @Override
        public ResponseModel createFromParcel(Parcel in) {
            return new ResponseModel(in);
        }

        @Override
        public ResponseModel[] newArray(int size) {
            return new ResponseModel[size];
        }
    };


    public Response getResponse ()
    {
        return response;
    }

    @NotNull
    @Override
    public String toString()
    {
        return "ClassPojo [meta = "+meta+", response = "+response+"]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(meta, flags);
        dest.writeParcelable(response, flags);
    }
}
