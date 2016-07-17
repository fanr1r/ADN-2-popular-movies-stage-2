package com.example.android.popularmovies.ui;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.TextViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FilterQueryProvider;
import android.widget.GridView;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.Utility;
import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.data.MovieDbHelper;
import com.example.android.popularmovies.sync.PopularMoviesSyncAdapter;

/**
 * A fragment containing the list view of Movies.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Adapter for the movies
     */
    private MovieAdapter mMovieAdapter;

    private GridView mGridView;
    private int mPosition = GridView.INVALID_POSITION;
    private boolean mUsePhoneLayout;

    private static final String SELECTED_KEY = "selected_position";

    private static final int MOVIE_LOADER = 0;
    // For the movie view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] MOVIE_COLUMNS = {
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

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMovieAdapter = new MovieAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        mGridView = (GridView) rootView.findViewById(R.id.movies_grid);
        View emptyView = rootView.findViewById(R.id.empty_movies_grid_text_view);
        mGridView.setEmptyView(emptyView);
        mGridView.setAdapter(mMovieAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    // since we read the location when we create the loader, all we need to do is restart things
    void onMovieListChanged() {
        updateMovies();
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }

    private void updateMovies() {
        PopularMoviesSyncAdapter.syncImmediately(getActivity());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri inventoryUri = MovieEntry.buildMovieUri();

        return new CursorLoader(getActivity(),
                inventoryUri,
                MOVIE_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        String movieListSetting = Utility.getPreferredMovies(getContext());
        if (movieListSetting.equals(getString(R.string.pref_show_movies_by_favorite))) {
            MovieDbHelper dbHelper = new MovieDbHelper(getContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cur = db.query(
                    MovieEntry.TABLE_NAME,  // Table to Query
                    null, // all columns
                    MovieEntry.COLUMN_FAVORITE + " == ?", // Columns for the "where" clause
                    new String[] {"1"}, // Values for the "where" clause
                    null, // columns to group by
                    null, // columns to filter by row groups
                    null // sort order
            );
            if (cur.getColumnCount() > 0) {
                mMovieAdapter.swapCursor(cur);
            } else {
                mMovieAdapter.swapCursor(null);
            }
        } else {
            MovieDbHelper dbHelper = new MovieDbHelper(getContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cur = db.query(
                    MovieEntry.TABLE_NAME,  // Table to Query
                    null, // all columns
                    MovieEntry.COLUMN_MOVIE_LIST_SETTING + " == ?", // Columns for the "where" clause
                    new String[] {Utility.getPreferredMovies(getContext())}, // Values for the "where" clause
                    null, // columns to group by
                    null, // columns to filter by row groups
                    null // sort order
            );
            mMovieAdapter.swapCursor(cur);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mMovieAdapter.swapCursor(null);
    }

    public void setUsePhoneLayout(boolean usePhoneLayout) {
        mUsePhoneLayout = usePhoneLayout;
        if (mMovieAdapter != null) {
            mMovieAdapter.setUseTodayLayout(mUsePhoneLayout);
        }

        if (!mUsePhoneLayout) {
            mGridView.setNumColumns(3);
        }
    }
}