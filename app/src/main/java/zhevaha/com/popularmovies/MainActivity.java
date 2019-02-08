package zhevaha.com.popularmovies;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

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

public class MainActivity extends Activity {

    private final String LOG_TAG = "PopularMoview";
    private GridView gridView;
    private List<Film> mFilmList;
    private ImageAdapter mAdapter;
    private TextView mSelectText;
    private GridView.OnItemClickListener gridviewOnItemClickListener = new GridView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position,
                                long id) {
            mSelectText.setText(String.valueOf(position));
//            Log.d(LOG_TAG, "position # : " + String.valueOf(position));
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
//        Log.d(LOG_TAG, "onStart");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSelectText = (TextView) findViewById(R.id.ad_Block);
        gridView = (GridView) findViewById(R.id.gv_images );
        gridView.setOnItemClickListener(gridviewOnItemClickListener);
        updateFilmLibrary();
    }

    private void updateFilmLibrary() {
        String query = "http://api.themoviedb.org/3/movie/popular?api_key=f4ca38bc9fdb107e48dc28c3483ba7a0";
        new FetchFilmLiblaryTask(query).execute();
    }

    private class FetchFilmLiblaryTask extends AsyncTask<String, Void, List<Film>> {

        private String mQuery;

        FetchFilmLiblaryTask(String query) {
            mQuery = query;
        }

        private List<Film> getFilmLibraryDataFromJson(String filmLibraryJsonStr)
                throws JSONException {
//            Log.d(LOG_TAG, "getFilmLibraryDataFromJson start ");
            // These are the names of the JSON objects that need to be extracted.
            final String POSTER_PATH = "poster_path";
            final String ADULT = "adult";
            final String OVERVIEW = "overview";
            final String RELEASE_DATE = "release_date";
            final String ORIGINAL_TITLE = "original_title";
            final String ORIGINAL_LANGUAGE = "original_language";
            final String TITLE = "title";
            final String BACKDROP_PATH = "backdrop_path";
            final String POPULARITY = "popularity";
            final String VIDEO = "video";
            final String ID = "id";
            final String GENRE_IDS = "genre_ids";
            final String VOTE_COUNT = "vote_count";
            final String VOTE_AVERAGE = "vote_average";

            List<Film> resultStrs = new ArrayList<Film>();

            try {
                JSONObject filmLibraryJson = new JSONObject(filmLibraryJsonStr);
                JSONArray filmLibraryArray = filmLibraryJson.getJSONArray("results");

                for (int i = 0; i < filmLibraryArray.length(); i++) {
                    String description;
                    String title;
                    String poster;
                    Long id;

                    JSONObject filmLibrary = filmLibraryArray.getJSONObject(i);
                    Film film = new Film();
                    id = filmLibrary.getLong(ID);
                    film.setId(id);
                    title = filmLibrary.getString(TITLE);
                    film.setTitle(title);
                    description = filmLibrary.getString(OVERVIEW);
                    film.setOverview(description);
                    poster = filmLibrary.getString(POSTER_PATH);
                    film.setPosterPath(poster);
                    resultStrs.add(film);
//                    Log.e(LOG_TAG, "Array: \n" + resultStrs.toString());
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "JSONException: \n" + e.toString());
            }
            return resultStrs;
        }

        @Override
        protected List<Film> doInBackground(String... strings) {

            if (mQuery == null) {
//                Log.e(LOG_TAG, "Params in AsyncTask " + mQuery);
                return null;
            } else {
//                Log.e(LOG_TAG, "Params in AsyncTask " + mQuery.toString());

                // These two need to be declared outside the try/catch
                // so that they can be closed in the finally block.
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

                // Will contain the raw JSON response as a string.
                String filmLibraryJsonStr = null;
                try {
                    // Construct the URL for the Themoviedb query
                    // Possible parameters are avaiable at API page
                     URL url = new URL(mQuery);

                    // Create the request to Themoviedb, and open the connection
                    urlConnection = (HttpURLConnection) url.openConnection();
                    if (urlConnection != null) {
//                        int status = urlConnection.getResponseCode();
//                    Log.d(LOG_TAG, "urlConnection status = " + status);
                        urlConnection.setRequestMethod("GET");
                        urlConnection.connect();
                    } else {
                        Log.d(LOG_TAG, "urlConnection null");
                        return null;
                    }

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();

                    if (inputStream == null) {
                        // Nothing to do.
//                        Log.v(LOG_TAG, "inputStream: " + null);
                        return null;
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

                        Log.v(LOG_TAG, "FilmLibrary string: " + null);
                        return null;
                    }
                    filmLibraryJsonStr = buffer.toString();
//                Log.v(LOG_TAG, "FilmLibrary string: " + filmLibraryJsonStr);

                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e(LOG_TAG, "Error closing stream", e);
                        }
                    } else {
                        Log.e(LOG_TAG, "reader = null");
                    }
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);
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
//                    Log.d(LOG_TAG, filmLibraryJsonStr);

                    return getFilmLibraryDataFromJson(filmLibraryJsonStr);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
                // This will only happen if there was an error getting or parsing the date.
                return null;
            }
        }


        @Override
        protected void onPostExecute(List<Film> result) {

            if (result != null) {
                mFilmList = result;
                mAdapter = new ImageAdapter(getApplicationContext(), mFilmList);
                gridView.setAdapter(mAdapter);
            }
        }
    }
}
