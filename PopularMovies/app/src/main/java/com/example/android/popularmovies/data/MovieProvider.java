package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.Utility;
import com.example.android.popularmovies.data.MovieContract.MovieEntry;

/**
 * Created by Eisdrachl on 17.07.2016.
 */
public class MovieProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    static final int MOVIE = 100;
    static final int MOVIE_WITH_NAME = 101;
    static final int MOVIE_WITH_LIST_SETTING = 102;

    private static final SQLiteQueryBuilder sMovieByNameQueryBuilder;
    private static final SQLiteQueryBuilder sMovieByListSettingQueryBuilder;
    private static final SQLiteQueryBuilder sMovieByListSettingFavoriteQueryBuilder;

    static {
        sMovieByNameQueryBuilder = new SQLiteQueryBuilder();

        sMovieByNameQueryBuilder.setTables(
                MovieEntry.TABLE_NAME
        );
    }

    static {
        sMovieByListSettingQueryBuilder = new SQLiteQueryBuilder();

        sMovieByListSettingQueryBuilder.setTables(
                MovieEntry.TABLE_NAME
        );
    }

    static {
        sMovieByListSettingFavoriteQueryBuilder = new SQLiteQueryBuilder();

        sMovieByListSettingFavoriteQueryBuilder.setTables(
                MovieEntry.TABLE_NAME
        );
    }

    //movie.name = ?
    private static final String sMovieNameSelection =
            MovieEntry.TABLE_NAME +
                    "." + MovieEntry.COLUMN_TITLE + " = ? ";

    //movie.movie_list_setting = ?
    private static final String sMovieListSetting =
            MovieEntry.TABLE_NAME +
                    "." + MovieEntry.COLUMN_MOVIE_LIST_SETTING + " = ? ";

    //movie.favorite = ?
    private static final String sMovieListSettingFavorite =
            MovieEntry.TABLE_NAME +
                    "." + MovieEntry.COLUMN_FAVORITE + " = ? ";

    private Cursor getMovieByName(Uri uri, String[] projection, String sortOrder) {
        String name = MovieEntry.getTitleFromUri(uri);

        String[] selectionArgs = new String[]{name};
        String selection = sMovieNameSelection;

        return sMovieByNameQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getMovieByListSetting(Uri uri, String[] projection, String sortOrder) {
        String listSettingFromUri = MovieEntry.getListSettingFromUri(uri);
        if (listSettingFromUri.equals(getContext().getResources().getString(R.string.pref_show_movies_by_favorite))) {
            String[] selectionArgs = new String[]{"1"};
            String selection = sMovieListSettingFavorite;

            return sMovieByListSettingFavoriteQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );
        }

        String[] selectionArgs = new String[]{listSettingFromUri};
        String selection = sMovieListSetting;

        return sMovieByListSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

    }

    static UriMatcher buildUriMatcher() {

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, MovieContract.PATH_TITLE + "/*", MOVIE_WITH_NAME);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/*", MOVIE_WITH_LIST_SETTING);

        return matcher;
    }

    /*
        We just create a new MovieDbHelper for later use here.
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    //Here's where you'll code the getType function that uses the UriMatcher.
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                return MovieEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "movie"
            case MOVIE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "movie/*"
            case MOVIE_WITH_NAME: {
                retCursor = getMovieByName(uri, projection, sortOrder);
                break;
            }
            // "movie/*"
            case MOVIE_WITH_LIST_SETTING: {
                retCursor = getMovieByListSetting(uri, projection, sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE: {
                long _id = db.insert(MovieEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case MOVIE:
                rowsDeleted = db.delete(
                        MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIE:
                rowsUpdated = db.update(MovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

}