package com.example.android.popularmovies.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.R;
import com.squareup.picasso.Picasso;

public class MovieAdapter extends CursorAdapter {
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    // Flag to determine if we want to use a separate view for "details".
    private boolean mUsePhoneLayout = true;

    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.movie_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        ImageView iconView = (ImageView) view.findViewById(R.id.movie_image);

        final String FORECAST_BASE_URL = "http://image.tmdb.org/t/p/";
        String imageSize;
        if (mUsePhoneLayout) {
            imageSize = "w500";
        } else {
            imageSize = "w185";
        }
        String url = FORECAST_BASE_URL + imageSize + "/" + cursor.getString(MainActivityFragment.COL_MOVIE_POSTER_PATH);
        Picasso.with(context).load(url).into(iconView);
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUsePhoneLayout = useTodayLayout;
    }
}
