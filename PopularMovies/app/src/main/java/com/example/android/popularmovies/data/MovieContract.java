package com.example.android.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Eisdrachl on 17.07.2016.
 */
public class MovieContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies.app";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.popularmovies.app/movie/ is a valid path for
    // looking at movie.
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_TITLE = "title";

    /* Inner class that defines the table contents of the movie table */
    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final Uri CONTENT_URI_TITLE =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TITLE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        // Table name
        public static final String TABLE_NAME = "movie";

        // The show movie setting string is what will be sent to movie db api
        // as the query which movie list to get.
        public static final String COLUMN_MOVIE_LIST_SETTING = "movie_list_setting";

        // Title of the movie
        public static final String COLUMN_TITLE = "title";

        // Url to get the poster of the movie
        public static final String COLUMN_POSTER_PATH = "poster_path";

        // Plot synopsis of the movie
        public static final String COLUMN_PLOT_SYNOPSIS = "plot_synopsis";

        // Release date of the movie
        public static final String COLUMN_RELEASE_DATE = "release_date";

        // The user rating of the movie, a decimal between 0.00 and 10.00
        public static final String COLUMN_USER_RATING = "user_rating";

        // Shows if the movie is marked as favorite: 0 not favorite, 1 favorite
        public static final String COLUMN_FAVORITE = "favorite";

        // The movie id used by movie db api
        public static final String COLUMN_MOVIE_ID = "movie_id";


        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieUri() {
            return CONTENT_URI.buildUpon().build();
        }

        public static Uri buildMovieUri(String name) {
            return CONTENT_URI_TITLE.buildUpon().appendPath(name).build();
        }

        public static Uri buildMovieUriWithListSetting(String movieListSetting) {
            return CONTENT_URI.buildUpon().appendPath(movieListSetting).build();
        }

        public static String getTitleFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getListSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }
}
