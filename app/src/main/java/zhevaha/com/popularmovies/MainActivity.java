package zhevaha.com.popularmovies;

import android.app.Activity;
import android.content.Intent;
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

    private final static String FILE_NAME = "api_key";
    private final String LOG_TAG = "PopularMoview";
    private GridView gridView;
    private List<Film> mFilmList;
    private ImageAdapter mAdapter;
    private TextView mSelectText;
    private GridView.OnItemClickListener gridviewOnItemClickListener = new GridView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position,
                                long id) {
            Film film = mFilmList.get( position );
            Intent intent = new Intent( MainActivity.this, FilmOverview.class );
            intent.putExtra( Film.class.getSimpleName(), film );
            startActivity( intent );
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
//        Log.d(LOG_TAG, "onStart");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        mSelectText = findViewById( R.id.ad_Block );
        gridView = findViewById( R.id.gv_images );
        gridView.setOnItemClickListener( gridviewOnItemClickListener );
        updateFilmLibrary();
    }

    private void updateFilmLibrary() {
        String apiKey = getApiKey();
        String generalQuery = "http://api.themoviedb.org/3/movie/popular?api_key=" + apiKey;
        new FetchFilmLiblaryTask( generalQuery ).execute();
    }

    private String getApiKey() {

        String result;
        StringBuilder text = new StringBuilder();

        InputStream is = getResources().openRawResource( R.raw.api_key );
        try {
            BufferedReader reader = new BufferedReader( new InputStreamReader( is ) );
            while ((result = reader.readLine()) != null) {
                text.append( result );
            }
            result = text.toString();
//            Log.d( LOG_TAG, " result = " + result );
            reader.close();
            return result;
        } catch (IOException e) {
            Log.d( LOG_TAG, e.getMessage() );
        }
        return null;
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

            List<Film> resultStrs = new ArrayList<>();

            try {
                JSONObject filmLibraryJson = new JSONObject( filmLibraryJsonStr );
                JSONArray filmLibraryArray = filmLibraryJson.getJSONArray( "results" );

                for (int i = 0; i < filmLibraryArray.length(); i++) {

                    JSONObject filmLibrary = filmLibraryArray.getJSONObject( i );
                    Film film = new Film();
                    Long id = filmLibrary.getLong( ID );
                    film.setId( id );
                    String originalTitle = filmLibrary.getString( ORIGINAL_TITLE );
                    film.setOriginalTitle( originalTitle );
                    String title = filmLibrary.getString( TITLE );
                    film.setTitle( title );
                    String releaseDate = filmLibrary.getString( RELEASE_DATE );
                    film.setReleaseDate( releaseDate );
//                    String adult = filmLibrary.getString( ADULT );
//                    film.setAdult( adult );
//                    Double popularity = filmLibrary.getDouble( POPULARITY );
//                    film.setPopularity( popularity );
//                    Double voteAverage = filmLibrary.getDouble( VOTE_AVERAGE );
//                    film.setVoteAverage( voteAverage );
                    String overview = filmLibrary.getString( OVERVIEW );
                    film.setOverview( overview );
                    String poster = filmLibrary.getString( POSTER_PATH );
                    film.setPosterPath( poster );
                    resultStrs.add( film );

//                    Log.e(LOG_TAG, "Array: \n" + resultStrs.toString());
                }
            } catch (JSONException e) {
                Log.e( LOG_TAG, "JSONException: \n" + e.toString() );
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
                String filmLibraryJsonStr;
                try {
                    // Construct the URL for the Themoviedb query
                    // Possible parameters are avaiable at API page
                    URL url = new URL( mQuery );

                    // Create the request to Themoviedb, and open the connection
                    urlConnection = (HttpURLConnection) url.openConnection();
                    if (urlConnection != null) {
//                        int status = urlConnection.getResponseCode();
//                    Log.d(LOG_TAG, "urlConnection status = " + status);
                        urlConnection.setRequestMethod( "GET" );
                        urlConnection.connect();
                    } else {
                        Log.d( LOG_TAG, "urlConnection null" );
                        return null;
                    }

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuilder builder = new StringBuilder();

                    if (inputStream == null) {
                        // Nothing to do.
//                        Log.v(LOG_TAG, "inputStream: " + null);
                        return null;
                    }
                    reader = new BufferedReader( new InputStreamReader( inputStream ) );
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // builder for debugging.
                        builder.append( line + "\n" );
                    }
                    if (builder.length() == 0) {
                        // Stream was empty.  No point in parsing.

                        Log.v( LOG_TAG, "FilmLibrary string: " + null );
                        return null;
                    }
                    filmLibraryJsonStr = builder.toString();
//                Log.v(LOG_TAG, "FilmLibrary string: " + filmLibraryJsonStr);

                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e( LOG_TAG, "Error closing stream", e );
                        }
                    } else {
                        Log.e( LOG_TAG, "reader = null" );
                    }
                } catch (IOException e) {
                    Log.e( LOG_TAG, "Error ", e );
                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e( LOG_TAG, "Error closing stream", e );
                        }
                    }
                }

                try {
//                    Log.d(LOG_TAG, filmLibraryJsonStr);

                    return getFilmLibraryDataFromJson( filmLibraryJsonStr );
                } catch (JSONException e) {
                    Log.e( LOG_TAG, e.getMessage(), e );
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
                mAdapter = new ImageAdapter( getApplicationContext(), mFilmList );
                gridView.setAdapter( mAdapter );
            }
        }
    }
}
