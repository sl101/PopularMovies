package zhevaha.com.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static zhevaha.com.popularmovies.ConstantMovies.ISO_COD;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //    private final static String FILE_NAME = "api_key";
//    private final String ENGLISH_NAME = "englishName";
    private static final String LOG_TAG = "PopularMoview";
    private final String FILM_LIST = "filmlList";
    NavigationView navigationView;
    //    private String ISO_COD = "iso_cod";
    private String LANGUAGE;
    private GridView gridView;
    private List<Film> mFilmList;
    private ImageAdapter mAdapter;
    private TextView mSelectText;
    //    private String apiKey;
    private GridView.OnItemClickListener gridviewOnItemClickListener = new GridView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position,
                                long id) {
            Film film = mFilmList.get( position );
//            Log.d(LOG_TAG, "film position "+position);
            Intent intent = new Intent( MainActivity.this, FilmOverview.class );
            intent.putExtra( Film.class.getSimpleName(), film );
//            ArrayList<CharSequence> filmsPicturePath = new ArrayList<CharSequence>();
//            Log.d(LOG_TAG, "films \n");
//            for (Film item : mFilmList) {
//                filmsPicturePath.add( item.getPosterPath() );
////                Log.d(LOG_TAG, " "+item.getPosterPath());
//            }
//            intent.putCharSequenceArrayListExtra( FILM_LIST, filmsPicturePath );
            startActivity( intent );
        }
    };

    @Override
    protected void onRestart() {
//        Log.d( LOG_TAG, "LANGUAGE = " + LANGUAGE );
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

//        Log.d( LOG_TAG, "START "  );
//        Log.d( LOG_TAG , "START LOG_TAG"  );
        Log.d( LOG_TAG , "START PopularMovies"  );
//        String apiKey = new ApiKey( this ).getApiKey();
//        String apiKey = new ApiKey().getApiKey();

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
//        String apiKey = new ApiKey( this ).getApiKey();
        String language = getCustomLanguage();
//        String generalQuery = "http://api.themoviedb.org/3/movie/popular?api_key=" + apiKey + language;

        String generalQuery = "http://api.themoviedb.org/3/movie/popular?api_key=" + "f4ca38bc9fdb107e48dc28c3483ba7a0" + language;
//        new FetchFilmLiblaryTask( generalQuery ).execute();
        FetchAsyncTask fetchAsyncTask = new FetchAsyncTask();
        fetchAsyncTask.execute( generalQuery );
        try {
            mFilmList = getFilmLibraryDataFromJson( String.valueOf( fetchAsyncTask.get() ) );
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mAdapter = new ImageAdapter( getApplicationContext(), mFilmList );
        gridView.setAdapter( mAdapter );
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
            Log.e( LOG_TAG, "JSONException: \n" + e.toString() );
        }
        return resultStrs;
    }
}
