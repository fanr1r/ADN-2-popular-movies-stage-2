package com.example.android.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Eisdrachl on 21.07.2016.
 */
public class Review implements Parcelable {
    String mAuthor;
    String mContent;
    String mUrl;

    public Review(String author, String content, String url) {
        mAuthor = author;
        mContent = content;
        mUrl = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAuthor);
        dest.writeString(mContent);
        dest.writeString(mUrl);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Review(Parcel in) {
        mAuthor = in.readString();
        mContent = in.readString();
        mUrl = in.readString();
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getContent() {
        return mContent;
    }

    public String getUrl() {
        return mUrl;
    }
}
