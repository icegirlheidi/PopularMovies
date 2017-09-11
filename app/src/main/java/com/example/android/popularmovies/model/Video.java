package com.example.android.popularmovies.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Video implements Parcelable {

    @SerializedName("id")
    private String mId;

    @SerializedName("key")
    private String mKey;

    @SerializedName("site")
    private String mSite;

    @SerializedName("type")
    private String mType;

    private Video(Parcel in) {
        mId = in.readString();
        mKey = in.readString();
        mSite = in.readString();
        mType = in.readString();
    }

    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };


    public String getId() {
        return mId;
    }

    public String getKey() {
        return mKey;
    }

    public String getSite() {
        return mSite;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(mId);
        parcel.writeString(mKey);
        parcel.writeString(mSite);
        parcel.writeString(mType);
    }
}
