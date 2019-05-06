package zhevaha.com.popularmovies.zhevaha.com.popularmovies.config;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import zhevaha.com.popularmovies.R;


public class ApiKey {

//    private final String LOG_TAG = "PopularMovies";

    private static ApiKey instance;
    private String mApiKey;

    private ApiKey(Context mContext) {
        readApiKey( mContext );
    }

    public static ApiKey getInstance(Context mContext) {
        if (instance == null)
            instance = new ApiKey( mContext.getApplicationContext() );
        return instance;
    }

    private void readApiKey(Context mContext) {

        String result;
        StringBuilder text = new StringBuilder();

        InputStream is = mContext.getResources().openRawResource( R.raw.api_key );
        try {
            BufferedReader reader = new BufferedReader( new InputStreamReader( is ) );
            while ((result = reader.readLine()) != null) {
                text.append( result );
            }
            mApiKey = text.toString();
            reader.close();
        } catch (IOException e) {
//            Log.d( LOG_TAG, e.getMessage() );
        }
    }

    public String getApiKey() {
        return mApiKey;
    }
}
