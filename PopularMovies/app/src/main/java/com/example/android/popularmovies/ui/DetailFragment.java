package com.example.android.popularmovies.ui;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movie;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment {
    static final String DETAIL_URI = "URI";

    private Movie mMovie;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        if (intent != null && intent.hasExtra(getString(R.string.intent_extra_movie))) {
            mMovie = intent.getParcelableExtra(getString(R.string.intent_extra_movie));
        }

        TextView titleTextView = (TextView) rootView.findViewById(R.id.title);
        titleTextView.setText(mMovie.getTitle());

        ImageView thumbnailImageView = (ImageView) rootView.findViewById(R.id.thumbnail);
        final String FORECAST_BASE_URL = "http://image.tmdb.org/t/p/";
        String imageSize = "w500";
        String url = FORECAST_BASE_URL + imageSize + "/" + mMovie.getPosterPath();
        Picasso.with(getContext()).load(url).into(thumbnailImageView);

        TextView synopsisTextView = (TextView) rootView.findViewById(R.id.synopsis);
        synopsisTextView.setText(mMovie.getPlotSynopsis());

        TextView rateTextView = (TextView) rootView.findViewById(R.id.rateValue);
        rateTextView.setText(mMovie.getUserRating() + "/10");

        TextView releaseDateTextView = (TextView) rootView.findViewById(R.id.release_date_text_view);
        releaseDateTextView.setText(mMovie.getReleaseDate());

        return rootView;
    }

}
