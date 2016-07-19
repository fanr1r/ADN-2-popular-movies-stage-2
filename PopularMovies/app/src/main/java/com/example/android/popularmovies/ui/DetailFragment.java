package com.example.android.popularmovies.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.sync.UpdateFavoriteFlagTask;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    static final String DETAIL_URI = "URI";

    private Uri mUri;

    private static final int DETAIL_LOADER = 0;

    private static final String[] DETAIL_COLUMNS = {
            MovieEntry.TABLE_NAME + "." + MovieEntry._ID,
            MovieEntry.COLUMN_MOVIE_LIST_SETTING,
            MovieEntry.COLUMN_TITLE,
            MovieEntry.COLUMN_POSTER_PATH,
            MovieEntry.COLUMN_PLOT_SYNOPSIS,
            MovieEntry.COLUMN_USER_RATING,
            MovieEntry.COLUMN_RELEASE_DATE,
            MovieEntry.COLUMN_FAVORITE
    };

    // These indices are tied to MOVIE_COLUMNS. If MOVIE_COLUMNS changes, these
    // must change.
    static final int COL_MOVIE_ID = 0;
    static final int COL_MOVIE_LIST_SETTING = 1;
    static final int COL_MOVIE_TITLE = 2;
    static final int COL_MOVIE_POSTER_PATH = 3;
    static final int COL_MOVIE_PLOT_SYNOPSIS = 4;
    static final int COL_MOVIE_USER_RATING = 5;
    static final int COL_MOVIE_RELEASE_DATE = 6;
    static final int COL_MOVIE_FAVORITE = 7;

    private ScrollView mContentScrollView;
    private ImageView mThumbnailImageView;
    private TextView mTitleTextView;
    private TextView mSynopsisTextView;
    private TextView mRateTextView;
    private TextView mReleaseDateTextView;
    private Button mFavoriteButton;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }

        mContentScrollView = (ScrollView) rootView.findViewById(R.id.detail_fragment_content);
        mTitleTextView = (TextView) rootView.findViewById(R.id.title);
        mThumbnailImageView = (ImageView) rootView.findViewById(R.id.thumbnail);
        mSynopsisTextView = (TextView) rootView.findViewById(R.id.synopsis);
        mRateTextView = (TextView) rootView.findViewById(R.id.rateValue);
        mReleaseDateTextView = (TextView) rootView.findViewById(R.id.release_date_text_view);
        mFavoriteButton = (Button) rootView.findViewById(R.id.fav_button);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

            mTitleTextView.setText(data.getString(COL_MOVIE_TITLE));

            final String FORECAST_BASE_URL = "http://image.tmdb.org/t/p/";
            String imageSize = "w500";
            String url = FORECAST_BASE_URL + imageSize + "/" + data.getString(COL_MOVIE_POSTER_PATH);
            Picasso.with(getContext()).load(url).into(mThumbnailImageView);

            mSynopsisTextView.setText(data.getString(COL_MOVIE_PLOT_SYNOPSIS));

            mRateTextView.setText(data.getString(COL_MOVIE_USER_RATING) + "/10");

            mReleaseDateTextView.setText(data.getString(COL_MOVIE_RELEASE_DATE));

            final String title = data.getString(COL_MOVIE_TITLE);
            mFavoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UpdateFavoriteFlagTask updateFavoriteFlagTask = new UpdateFavoriteFlagTask(getContext(), title);
                    updateFavoriteFlagTask.execute();
                }
            });
            if (data.getInt(COL_MOVIE_FAVORITE) == 1) {
                mFavoriteButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                mFavoriteButton.setTextColor(getResources().getColor(R.color.white));
            } else {
                mFavoriteButton.setBackgroundColor(getResources().getColor(R.color.gray));
                mFavoriteButton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            }

            mContentScrollView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

}
