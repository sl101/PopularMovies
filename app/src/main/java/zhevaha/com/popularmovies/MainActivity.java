package zhevaha.com.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
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

import static zhevaha.com.popularmovies.ConstantMovies.*;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

//    private final static String FILE_NAME = "api_key";
//    private final String ENGLISH_NAME = "englishName";
//    private final String LOG_TAG = "PopularMovies";
    NavigationView navigationView;
//    private String ISO_COD = "iso_cod";
    private String LANGUAGE;
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
    protected void onRestart() {
//        Log.d( String.valueOf( LOG_TAG ), "LANGUAGE = " + LANGUAGE );
//        Log.d( String.valueOf( LOG_TAG ), "readPrefLanguage() = " + readPrefLanguage() );
        if (LANGUAGE != readPrefLanguage()) {
            updateFilmLibrary();
        }
        super.onRestart();
    }

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

        navigationView = findViewById( R.id.nav_view );
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
        LANGUAGE = languageName;
        return "&language=" + languageName;
    }

    private String readPrefLanguage() {
        SharedPreferences languagePreferences = getSharedPreferences( String.valueOf( ConstantMovies.APP_PREFERENCES ), Context.MODE_PRIVATE );
        String result = languagePreferences.getString( String.valueOf( ISO_COD ), "xx" );
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
            reader.close();
            return result;
        } catch (IOException e) {
            Log.d( String.valueOf( LOG_TAG ), e.getMessage() );
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById( R.id.drawer_layout );
        if (drawer.isDrawerOpen( GravityCompat.START )) {
            drawer.closeDrawer( GravityCompat.START );
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate( R.menu.main, menu );
//        return true;
//    }

    //    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected( item );
//    }
    private void showToolsMenu(View view) {
        Intent intent = new Intent( this, CustomTools.class );
        intent.putExtra( "iso_cod", readPrefLanguage() );
        startActivity( intent );
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_manage:
                showToolsMenu( navigationView );
                break;
            case R.id.nav_share:
                break;
        }

        DrawerLayout drawer = findViewById( R.id.drawer_layout );
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
                    Double voteAverage = filmLibrary.getDouble( VOTE_AVERAGE );
                    film.setVoteAverage( voteAverage );
                    String overview = filmLibrary.getString( OVERVIEW );
                    film.setOverview( overview );
                    String poster = filmLibrary.getString( POSTER_PATH );
                    film.setPosterPath( poster );
                    resultStrs.add( film );

                }
            } catch (JSONException e) {
                Log.e( String.valueOf( LOG_TAG ), "JSONException: \n" + e.toString() );
            }
            return resultStrs;
        }

        @Override
        protected List<Film> doInBackground(String... strings) {

            if (mQuery == null) {
                return null;
            } else {
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
                        urlConnection.setRequestMethod( "GET" );
                        urlConnection.connect();
                    } else {
                        Log.d( String.valueOf( LOG_TAG ), "urlConnection null" );
                        return null;
                    }

                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuilder builder = new StringBuilder();

                    if (inputStream == null) {
                        return null;
                    }
                    reader = new BufferedReader( new InputStreamReader( inputStream ) );
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append( line + "\n" );
                    }
                    if (builder.length() == 0) {

                        Log.v( String.valueOf( LOG_TAG ), "FilmLibrary string: " + null );
                        return null;
                    }
                    filmLibraryJsonStr = builder.toString();

                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e( String.valueOf( LOG_TAG ), "Error closing stream", e );
                        }
                    } else {
                        Log.e( String.valueOf( LOG_TAG ), "reader = null" );
                    }
                } catch (IOException e) {
                    Log.e( String.valueOf( LOG_TAG ), "Error ", e );
                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e( String.valueOf( LOG_TAG ), "Error closing stream", e );
                        }
                    }
                }

                try {
                    return getFilmLibraryDataFromJson( filmLibraryJsonStr );
                } catch (JSONException e) {
                    Log.e( String.valueOf( LOG_TAG ), e.getMessage(), e );
                    e.printStackTrace();
                }
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
