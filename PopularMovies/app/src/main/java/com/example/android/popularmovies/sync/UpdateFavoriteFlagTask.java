package com.example.android.popularmovies.sync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.data.MovieDbHelper;
import com.example.android.popularmovies.ui.DetailActivity;
import com.example.android.popularmovies.ui.DetailFragment;
import com.example.android.popularmovies.ui.MainActivity;

/**
 * Created by Eisdrachl on 17.07.2016.
 */
public class UpdateFavoriteFlagTask extends AsyncTask<String, Void, Void> {
    public final String LOG_TAG = UpdateFavoriteFlagTask.class.getSimpleName();

    private static final int LOADER = 0;

    private final Context mContext;
    private String mTitle;

    public UpdateFavoriteFlagTask(Context context, String title) {
        mContext = context;
        mTitle = title;
    }

    /**
     * Helper method to handle update of a product in the inventory database.
     */
    void updateProduct(String title) {
        MovieDbHelper mDbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                MovieEntry._ID,
                MovieEntry.COLUMN_TITLE,
                MovieEntry.COLUMN_FAVORITE,
        };

        Cursor productCursor = db.query(
                MovieEntry.TABLE_NAME,                // The table to query
                projection,                               // The columns to return
                MovieEntry.COLUMN_TITLE + " = ?",      // The columns for the WHERE clause
                new String[]{title},                       // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );

        ContentValues productValues = new ContentValues();

        productCursor.moveToFirst();

        // update favorite
        int oldFavorite = productCursor.getInt(productCursor.getColumnIndexOrThrow(MovieEntry.COLUMN_FAVORITE));
        if (oldFavorite == 0) {
            productValues.put(MovieEntry.COLUMN_FAVORITE, 1);
        } else {
            productValues.put(MovieEntry.COLUMN_FAVORITE, 0);
        }

        String selection = MovieEntry.COLUMN_TITLE + " LIKE ?";
        String[] selectionArgs = {title};

        // Finally, insert inventory data into the database.
        db.update(
                MovieEntry.TABLE_NAME,
                productValues,
                selection,
                selectionArgs
        );
    }

    @Override
    protected Void doInBackground(String... params) {
        updateProduct(mTitle);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        try {
            // Reload current fragment
            DetailFragment df = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DetailFragment.class.getSimpleName());
            df.getLoaderManager().restartLoader(LOADER, null, df);
        } catch (Exception e) {
            Log.v(LOG_TAG, "DetailFragment not reloaded");
        }
    }

    private FragmentManager getSupportFragmentManager() {
        try {
            final DetailActivity activity = (DetailActivity) mContext;

            // Return the fragment manager
            return activity.getSupportFragmentManager();

        } catch (ClassCastException e) {
            Log.d(LOG_TAG, mContext.getString(R.string.error_cant_get_fragment));
        }
        try {
            final MainActivity activity = (MainActivity) mContext;

            // Return the fragment manager
            return activity.getSupportFragmentManager();

        } catch (ClassCastException e) {
            Log.d(LOG_TAG, mContext.getString(R.string.error_cant_get_fragment));
        }
        return null;
    }
}

