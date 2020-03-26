package com.example.foodmeup.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class Groups implements Parcelable {

    private String name;

    private String count;

    private String type;

    private Items[] items;

    private Groups(Parcel in) {
        name = in.readString();
        count = in.readString();
        type = in.readString();
        items = in.createTypedArray(Items.CREATOR);
    }

    public static final Creator<Groups> CREATOR = new Creator<Groups>() {
        @Override
        public Groups createFromParcel(Parcel in) {
            return new Groups(in);
        }

        @Override
        public Groups[] newArray(int size) {
            return new Groups[size];
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

    public String getCount ()
    {
        return count;
    }

    public void setCount (String count)
    {
        this.count = count;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    public Items[] getItems ()
    {
        return items;
    }

    public void setItems (Items[] items)
    {
        this.items = items;
    }

    @NotNull
    @Override
    public String toString()
    {
        return "ClassPojo [name = "+name+", count = "+count+", type = "+type+", items = "+ Arrays.toString(items) +"]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(count);
        dest.writeString(type);
        dest.writeTypedArray(items, flags);
    }
}
