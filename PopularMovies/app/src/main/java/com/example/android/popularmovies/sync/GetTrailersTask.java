package com.example.android.popularmovies.sync;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Review;
import com.example.android.popularmovies.data.Trailer;
import com.example.android.popularmovies.ui.DetailActivity;
import com.example.android.popularmovies.ui.DetailFragment;
import com.example.android.popularmovies.ui.MainActivity;
import com.example.android.popularmovies.ui.MainActivityFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eisdrachl on 21.07.2016.
 */
public class GetTrailersTask extends AsyncTask<String, Void, Trailer[]> {
    private final String LOG_TAG = GetReviewsTask.class.getSimpleName();

    private static final int LOADER = 0;

    private final Context mContext;
    private String mMovieId;

    public GetTrailersTask(Context context, String movieId) {
        mContext = context;
        mMovieId = movieId;
    }

    private Trailer[] getReviewDataFromJson(String reviewJsonStr) throws JSONException {

        final String MDB_LIST = "results";
        final String MDB_TRAILER_KEY = "key";
        final String MDB_TRAILER_NAME = "name";
        final String MDB_TRAILER_SITE = "site";
        final String MDB_TRAILER_TYPE = "type";

        JSONObject movieJson = new JSONObject(reviewJsonStr);
        JSONArray movieJsonArray = movieJson.getJSONArray(MDB_LIST);

        List<Trailer> trailerList = new ArrayList<Trailer>();
        for (int i = 0; i < movieJsonArray.length(); i++) {
            String name = "";
            String url = "";

            JSONObject reviewJsonObject = movieJsonArray.getJSONObject(i);

            if (reviewJsonObject.getString(MDB_TRAILER_TYPE).equals("Trailer") && reviewJsonObject.getString(MDB_TRAILER_SITE).equals("YouTube")) {
                name = reviewJsonObject.getString(MDB_TRAILER_NAME);
                url = "http://www.youtube.com/watch?v=" + reviewJsonObject.getString(MDB_TRAILER_KEY);

                Trailer trailer = new Trailer(name, url);
                trailerList.add(trailer);
            }

        }

        return trailerList.toArray(new Trailer[trailerList.size()]);
    }

    @Override
    protected Trailer[] doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String reviewJsonStr = null;

        try {
            // Construct the URL for the Movie DB query
            final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String ID_PARAM = mMovieId;
            final String TRAILERS_PARAM = "videos";
            final String APPID_PARAM = "api_key";

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendEncodedPath(ID_PARAM)
                    .appendEncodedPath(TRAILERS_PARAM)
                    .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            Log.v(LOG_TAG, "Built URI " + builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                reviewJsonStr = null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            reviewJsonStr = buffer.toString();


        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            return getReviewDataFromJson(reviewJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Trailer[] trailers) {
        try {
            // Reload current fragment
            DetailFragment df = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DetailFragment.class.getSimpleName());
            df.setTrailers(trailers);
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
            Log.d(LOG_TAG, mContext.getString(R.string.error_cant_get_fragment, DetailFragment.class.getSimpleName()));
        }
        try {
            final MainActivity activity = (MainActivity) mContext;

            // Return the fragment manager
            return activity.getSupportFragmentManager();

        } catch (ClassCastException e) {
            Log.d(LOG_TAG, mContext.getString(R.string.error_cant_get_fragment, MainActivityFragment.class.getSimpleName()));
        }
        return null;
    }
}