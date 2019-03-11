package zhevaha.com.popularmovies;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static zhevaha.com.popularmovies.ConstantMovies.LOG_TAG;

public class ApiKey extends AppCompatActivity {

    private Context mContext;

    public ApiKey(Context current) {
        mContext = current;
    }

    private String readApiKey() {

        String result;
        StringBuilder text = new StringBuilder();

        InputStream is = mContext.getResources().openRawResource( R.raw.api_key );
        try {
            BufferedReader reader = new BufferedReader( new InputStreamReader( is ) );
            while ((result = reader.readLine()) != null) {
                text.append( result );
            }
            result = text.toString();
            reader.close();
            Log.d( String.valueOf( LOG_TAG ), "Api Key = " + result );
            return result;
        } catch (IOException e) {
            Log.d( String.valueOf( LOG_TAG ), e.getMessage() );
        }
        return null;
    }

    public String getApiKey() {

        return readApiKey();
    }
}
