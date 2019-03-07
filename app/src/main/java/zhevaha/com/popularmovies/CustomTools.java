package zhevaha.com.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
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

public class CustomTools extends AppCompatActivity implements View.OnClickListener {


//    private final String LOG_TAG = "PopularMovies";
//    private String ISO_COD = "iso_cod";
//    private String ENGLISH_NAME = "english_name";
//    private String NAME = "name";
    private TextView mLanguageText;
    private List<Language> mLanguagesArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.tools_layout );

        mLanguagesArray = new ArrayList<>();
        new FetchLanguagesTask().execute();

        mLanguageText = findViewById( R.id.nav_language );
        mLanguageText.setText( readPrefLanguageName() );
        mLanguageText.setOnClickListener( this );

    }

    private String readPrefLanguageName() {
        SharedPreferences preferredName = getSharedPreferences( String.valueOf( APP_PREFERENCES ), Context.MODE_PRIVATE );
        return preferredName.getString( String.valueOf( NAME ), "English" );
    }

    private void setPrefLanguage(int item) {
        SharedPreferences languagePreferences = getSharedPreferences( String.valueOf( APP_PREFERENCES ), Context.MODE_PRIVATE );
        SharedPreferences.Editor ed = languagePreferences.edit();
        String englishName = mLanguagesArray.get( item ).getEnglishName();
        String name = mLanguagesArray.get( item ).getName();
        String languageCod = mLanguagesArray.get( item ).getLanguageCod();
        ed.putString( String.valueOf(ENGLISH_NAME), englishName );
        ed.putString( String.valueOf( NAME ), name );
        ed.putString( String.valueOf( ISO_COD ), languageCod );
        ed.commit();
    }

    public void onClick(View v) {

        PopupMenu popupMenu = new PopupMenu( this, mLanguageText );
        popupMenu.inflate( R.menu.popupmenu );
        for (int i = 0; i < mLanguagesArray.size(); i++) {
            popupMenu.getMenu().add( 0, i, 0, mLanguagesArray.get( i ).getName() );
        }
        popupMenu.setOnMenuItemClickListener( new PopupMenu.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public boolean onMenuItemClick(MenuItem item) {
                setPrefLanguage( item.getItemId() );
                mLanguageText.setText( readPrefLanguageName() );
                return true;
            }
        } );
        popupMenu.show();
    }

    private class FetchLanguagesTask extends AsyncTask<Void, Void, String> {

        private List<Language> getLanguagesDataFromJson(String languageJsonStr) {

            List<Language> resultArray = new ArrayList();
            String ISO_COD = "iso_639_1";
            String ENGLISH_NAME = "english_name";
            String NAME = "name";

            try {
                JSONArray languagesJsonArray = new JSONArray( languageJsonStr );
                for (int i = 0; i < languagesJsonArray.length(); i++) {
                    JSONObject languageObject = languagesJsonArray.getJSONObject( i );
                    Language language = new Language();
                    String cod = languageObject.getString( ISO_COD );
                    language.setLanguageCod( cod );
                    String englishName = languageObject.getString( ENGLISH_NAME );
                    language.setEnglishName( englishName );
                    String name = languageObject.getString( NAME );
                    if (name.length() < 1 || name.contains( "?" )) {
                        language.setName( englishName );
                    } else if (name.contains( "No Language" )) {
                        language.setName( "Default (English)" );
                    } else {
                        language.setName( name );
                    }
                    resultArray.add( language );

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return resultArray;
        }

        @Override
        protected String doInBackground(Void... strings) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            StringBuilder builder = new StringBuilder();

            try {
                URL url = new URL( "https://api.themoviedb.org/3/configuration/languages?api_key=f4ca38bc9fdb107e48dc28c3483ba7a0&language" );

                urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection != null) {
                    urlConnection.setRequestMethod( "GET" );
                    urlConnection.connect();
                } else {
                    Log.d(  String.valueOf( LOG_TAG ), "urlConnection null" );
                    return null;
                }

                InputStream inputStream = urlConnection.getInputStream();


                if (inputStream == null) {
                    // Nothing to do.
//                    Log.v( LOG_TAG, "inputStream: " + null );
                    return null;
                }

                reader = new BufferedReader( new InputStreamReader( inputStream ) );
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append( line + "\n" );
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(  String.valueOf( LOG_TAG ), "Error closing stream", e );
                    }
                } else {
                    Log.e(  String.valueOf( LOG_TAG ), "reader = null" );
                }
            } catch (IOException e) {
                Log.e(  String.valueOf( LOG_TAG ), "Error ", e );
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

            return builder.toString();
        }


        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
//                Log.v( LOG_TAG, "String length from onPostExecute(result) = " + result.length() );
//                Log.v(LOG_TAG, "String from onPostExecute(result): \n" + result);
                mLanguagesArray = getLanguagesDataFromJson( result );
//                mLanguageText.setText( result );
            }
        }
    }
}
