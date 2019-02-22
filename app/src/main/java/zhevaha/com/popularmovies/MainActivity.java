package zhevaha.com.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final static String FILE_NAME = "api_key";
    private final String LOG_TAG = "PopularMoview";
    private GridView gridView;
    private List<Film> mFilmList;
    private ImageAdapter mAdapter;
    private TextView mSelectText;
    private SharedPreferences mLanguagePreferences;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.addDrawerListener( toggle );
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener( this );

        mSelectText = findViewById( R.id.ad_Block );
        gridView = findViewById( R.id.gv_images );
        gridView.setOnItemClickListener( gridviewOnItemClickListener );
        updateFilmLibrary();
    }

    private void updateFilmLibrary() {

        String apiKey = getApiKey();
        String language = getCustomLanguage();
        String generalQuery = "http://api.themoviedb.org/3/movie/popular?api_key=" + apiKey + language;
        new FetchFilmLiblaryTask( generalQuery ).execute();
    }

    private String getCustomLanguage() {
        String languageName = readPrefLanguage();
        Log.d( LOG_TAG, "languageName = " + languageName );
        if (languageName.isEmpty()) {
            List languages = getLanguages();
        }
        return "&language=" + languageName;
    }

    private String readPrefLanguage() {
        mLanguagePreferences = getPreferences( MODE_PRIVATE );
        String result = mLanguagePreferences.getString( "englishName", "xx" );
        return result;
    }

    private List getLanguages() {
        List result = new ArrayList();
        Log.d( LOG_TAG, "result.size() = " + result.size() );
        if (result.size() == 0) {

            return result;
        }
        return result;
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        if (drawer.isDrawerOpen( GravityCompat.START )) {
            drawer.closeDrawer( GravityCompat.START );
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.main, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        drawer.closeDrawer( GravityCompat.START );
        return true;
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

                    Double voteAverage = filmLibrary.getDouble( VOTE_AVERAGE );
                    film.setVoteAverage( voteAverage );
                    String overview = filmLibrary.getString( OVERVIEW );
                    film.setOverview( overview );
                    String poster = filmLibrary.getString( POSTER_PATH );
                    film.setPosterPath( poster );
                    resultStrs.add( film );


//                    Log.d( LOG_TAG,"Film "+title+" rating = "+voteAverage );
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
