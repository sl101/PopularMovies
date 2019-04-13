package zhevaha.com.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static zhevaha.com.popularmovies.ConstantMovies.APP_PREFERENCES;
import static zhevaha.com.popularmovies.ConstantMovies.ENGLISH_NAME;
import static zhevaha.com.popularmovies.ConstantMovies.ISO_COD;
import static zhevaha.com.popularmovies.ConstantMovies.NAME;

public class CustomTools extends AppCompatActivity implements View.OnClickListener {


    //    private final String LOG_TAG = "PopularMovies";
//    private String ISO_COD = "iso_cod";
//    private String ENGLISH_NAME = "english_name";
//    private String NAME = "name";
    private String apiKey;
    private TextView mLanguageText;
    private List<Language> mLanguagesArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.tools_layout );

        mLanguagesArray = new ArrayList<>();

        apiKey = new ApiKey(this).getApiKey();
//       apiKey = new ApiKey().getApiKey();
        String query = "https://api.themoviedb.org/3/configuration/languages?api_key=" + apiKey + "&language";
        FetchAsyncTask fetchAsyncTask = new FetchAsyncTask();
        fetchAsyncTask.execute( query );
        String taskResult = null;
        try {
            taskResult = String.valueOf( fetchAsyncTask.get() );
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (taskResult != null) {
            mLanguagesArray = getLanguagesDataFromJson( taskResult );
        }

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
        ed.putString( String.valueOf( ENGLISH_NAME ), englishName );
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

}
