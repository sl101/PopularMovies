package zhevaha.com.popularmovies;

import android.content.Intent;
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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import zhevaha.com.popularmovies.zhevaha.com.popularmovies.config.ApiKey;
import zhevaha.com.popularmovies.zhevaha.com.popularmovies.config.Config;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String LOG_TAG = "PopularMoview";
    private NavigationView navigationView;
    private String LANGUAGE;
    private GridView gridView;
    private List<Film> mFilmList;
    private ImageAdapter mAdapter;
    private AdView mAdView;
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
        if (LANGUAGE != readLanguageCod()) {
            updateFilmLibrary();
        }
        super.onRestart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        showPosterList();

    }

    private void showPosterList() {

        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        DrawerLayout drawer = findViewById( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.addDrawerListener( toggle );
        toggle.syncState();

        navigationView = findViewById( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener( this );

        mAdView = findViewById( R.id.adView );
        MobileAds.initialize( getApplicationContext(), Config.getAppId() );
        initAdMObBlock();

        gridView = findViewById( R.id.gv_images );
        gridView.setOnItemClickListener( gridviewOnItemClickListener );

        updateFilmLibrary();
    }

    private void initAdMObBlock() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd( adRequest );
    }

    private void updateFilmLibrary() {
        String apiKey = ApiKey.getInstance( this ).getApiKey();
        Log.d( LOG_TAG, "apiKey = " + apiKey );
        String language = getCustomLanguage();
        String generalQuery = "http://api.themoviedb.org/3/movie/popular?api_key=" + apiKey + language;

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
        return "&language=" + readLanguageCod();
    }

    private String readLanguageCod() {
        return Language.getInstance( this ).getLanguageCod();
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

    private void showToolsMenu(View view) {
        Intent intent = new Intent( this, CustomTools.class );
        intent.putExtra( "iso_cod", readLanguageCod() );
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
                onInviteClicked();
                break;
        }

        DrawerLayout drawer = findViewById( R.id.drawer_layout );
        drawer.closeDrawer( GravityCompat.START );
        return true;
    }

    private void onInviteClicked() {
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
