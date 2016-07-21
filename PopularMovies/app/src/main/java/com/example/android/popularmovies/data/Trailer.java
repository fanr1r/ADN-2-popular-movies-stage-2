package com.example.android.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Eisdrachl on 21.07.2016.
 */
public class Trailer implements Parcelable {
    String mName;
    String mUrl;

    public Trailer( String id, String url) {
        mName = id;
        mUrl = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mUrl);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>() {
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Trailer(Parcel in) {
        mName = in.readString();
        mUrl = in.readString();
    }

    public String getName() {
        return mName;
    }

    public String getUrl() {
        return mUrl;
    }
}