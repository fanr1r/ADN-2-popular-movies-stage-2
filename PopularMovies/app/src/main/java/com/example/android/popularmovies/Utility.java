package com.example.android.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Eisdrachl on 17.07.2016.
 */
public class Utility {
    public static String getPreferredMovies(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_show_movies_key),
                context.getString(R.string.pref_show_movies_default));
    }

    public static boolean isMovieListByFavorite(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_show_movies_key),
                context.getString(R.string.pref_show_movies_default))
                .equals(context.getString(R.string.pref_show_movies_by_favorite));
    }
}
