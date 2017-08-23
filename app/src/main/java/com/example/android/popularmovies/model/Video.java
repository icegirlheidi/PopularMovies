package com.example.android.popularmovies.model;


import android.os.Parcel;
import android.os.Parcelable;

public class Video implements Parcelable {

    private String mId;
    private String mKey;
    private String mSite;
    private String mType;

    public Video(String id, String key) {
        this.mId = id;
        this.mKey = key;
    }

    protected Video(Parcel in) {
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

    public void setSite(String site) {
        this.mSite = site;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public String getId() {
        return mId;
    }

    public String getKey() {
        return mKey;
    }

    public String getSite() {
        return mSite;
    }

    public String getType() {
        return mType;
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
