package com.example.android.popularmovies.ui;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movie;

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
 * A fragment containing the list view of Movies.
 */
public class MainActivityFragment extends Fragment {

    private MovieAdapter mMovieAdapter;

    private boolean mUseTodayLayout;

    private void updateMovies() {
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOderStr = prefs.getString(getString(R.string.pref_sort_order_key), getString(R.string.pref_sort_order_popularity));
        fetchMoviesTask.execute(sortOderStr);
    }

    public MainActivityFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mMovieAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());

        // Get a reference to the ListView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.flavors_grid);
        gridView.setAdapter(mMovieAdapter);

        mMovieAdapter.setUseTodayLayout(mUseTodayLayout);

        return rootView;
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
        if (mMovieAdapter != null) {
            mMovieAdapter.setUseTodayLayout(mUseTodayLayout);
        }
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private Movie[] getMovieDataFromJson(String moviesJsonStr) throws JSONException {

            final String MDB_LIST = "results";
            final String MDB_TITLE = "original_title";
            final String MDB_POSTER_PATH = "poster_path";
            final String MDB_PLOT_SYNOPSIS = "overview";
            final String MDB_USER_RATING = "vote_average";
            final String MDB_RELEASE_DATE = "release_date";

            JSONObject movieJson = new JSONObject(moviesJsonStr);
            JSONArray movieJsonArray = movieJson.getJSONArray(MDB_LIST);

            List<Movie> movieList = new ArrayList<Movie>();
            for (int i = 0; i < movieJsonArray.length(); i++) {
                String title = "";
                String posterPath = "";
                String plotSynopsis = "";
                Double userRating = 0.0;
                String releaseDate = "";

                JSONObject movieJsonObject = movieJsonArray.getJSONObject(i);

                title = movieJsonObject.getString(MDB_TITLE);
                posterPath = movieJsonObject.getString(MDB_POSTER_PATH);
                plotSynopsis = movieJsonObject.getString(MDB_PLOT_SYNOPSIS);
                userRating = movieJsonObject.getDouble(MDB_USER_RATING);
                releaseDate = movieJsonObject.getString(MDB_RELEASE_DATE);

                Movie movie = new Movie(title, posterPath, plotSynopsis, userRating, releaseDate);
                movieList.add(movie);

            }

            Movie[] movieArray = movieList.toArray(new Movie[movieList.size()]);
            return movieArray;
        }

        @Override
        protected Movie[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            try {
                // Construct the URL for the Movie DB query
                final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendEncodedPath(params[0])
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
                    moviesJsonStr = null;
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
                moviesJsonStr = buffer.toString();

//            Log.v(LOG_TAG, "Forecast JSON String: " + moviesJsonStr);

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
                return getMovieDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage());
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            if (movies != null) {
                mMovieAdapter.clear();
                mMovieAdapter.addAll(movies);
            }
        }
    }

}