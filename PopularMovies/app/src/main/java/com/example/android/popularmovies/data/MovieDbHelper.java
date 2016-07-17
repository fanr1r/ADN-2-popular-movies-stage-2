package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;

/**
 * Created by Eisdrachl on 17.07.2016.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    // Database Version
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    // Database Name
    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Table create statement
        // Create a table to hold products.
        final String SQL_CREATE_INVENTORY_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY," +
                MovieEntry.COLUMN_MOVIE_LIST_SETTING + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_PLOT_SYNOPSIS + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_USER_RATING + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_FAVORITE + " INTEGER NOT NULL " +
                " );";

        // creating table
        db.execSQL(SQL_CREATE_INVENTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // updates the schema without wiping data
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}