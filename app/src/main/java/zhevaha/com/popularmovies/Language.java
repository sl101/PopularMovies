package zhevaha.com.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;

import static zhevaha.com.popularmovies.zhevaha.com.popularmovies.config.ConstantMovies.APP_PREFERENCES;
import static zhevaha.com.popularmovies.zhevaha.com.popularmovies.config.ConstantMovies.ENGLISH_NAME;
import static zhevaha.com.popularmovies.zhevaha.com.popularmovies.config.ConstantMovies.ISO_COD;
import static zhevaha.com.popularmovies.zhevaha.com.popularmovies.config.ConstantMovies.NAME;

public class Language {

    private static Language instance;
    private final String LOG_TAG = "PopularMovies";
    private String mLanguageCod;
    private String englishName;
    private String name;

    private Language(Context lContext) {
        readPrefLanguage( lContext );
    }

    public static Language getInstance(Context lContext) {
        if (instance == null)
            instance = new Language( lContext.getApplicationContext() );
        return instance;
    }

    private void readPrefLanguage(Context lContext) {
        SharedPreferences languagePreferences = lContext.getSharedPreferences( String.valueOf( APP_PREFERENCES ), Context.MODE_PRIVATE );
        mLanguageCod = languagePreferences.getString( String.valueOf( ISO_COD ), "xx" );
        englishName = languagePreferences.getString( String.valueOf( ENGLISH_NAME ), "No Language" );
        name = languagePreferences.getString( String.valueOf( NAME ), "No Language" );
    }

    public String getLanguageCod() {
        return mLanguageCod;
    }

    public void setLanguageCod(String languageCod) {
        mLanguageCod = languageCod;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
