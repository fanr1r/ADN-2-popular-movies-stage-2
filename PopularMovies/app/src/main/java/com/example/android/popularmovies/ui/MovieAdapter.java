package com.example.android.popularmovies.ui;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends ArrayAdapter<Movie> {
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_TWO_COL = 0;
    private static final int VIEW_TYPE_THREE_COL = 1;

    // Flag to determine if we want to use a separate view for "details".
    private boolean mUseTwoColumnLayout = true;

    Activity mContext;

    public MovieAdapter(Activity context, List<Movie> movies) {
        super(context, 0, movies);
        mContext = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.movie_item, parent, false);
        }

        if (!mUseTwoColumnLayout) {
            //set GridView numColumns to 3
        }

        ImageView iconView = (ImageView) convertView.findViewById(R.id.movie_image);
        iconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailActivity.class)
                        .putExtra(mContext.getString(R.string.intent_extra_movie), movie);
                mContext.startActivity(intent);
            }
        });

        final String FORECAST_BASE_URL = "http://image.tmdb.org/t/p/";
        String imageSize = "w500";
        String url = FORECAST_BASE_URL + imageSize + "/" + movie.getPosterPath();
        Picasso.with(getContext()).load(url).into(iconView);

        return convertView;
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTwoColumnLayout = useTodayLayout;
    }
}
