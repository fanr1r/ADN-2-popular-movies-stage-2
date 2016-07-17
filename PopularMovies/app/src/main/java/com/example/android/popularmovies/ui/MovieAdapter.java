package com.example.android.popularmovies.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;

public class MovieAdapter extends CursorAdapter {
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_item, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        ImageView iconView = (ImageView) view.findViewById(R.id.movie_image);
        final String title = cursor.getString(MainActivityFragment.COL_MOVIE_TITLE);
        iconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class)
                        .setData(MovieEntry.buildMovieUri(title));
                mContext.startActivity(intent);
            }
        });

        final String FORECAST_BASE_URL = "http://image.tmdb.org/t/p/";
        String imageSize = "w500";
        String url = FORECAST_BASE_URL + imageSize + "/" + cursor.getString(MainActivityFragment.COL_MOVIE_POSTER_PATH);
        Picasso.with(context).load(url).into(iconView);
    }
}
