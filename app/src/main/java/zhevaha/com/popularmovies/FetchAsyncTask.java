package zhevaha.com.popularmovies;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static zhevaha.com.popularmovies.ConstantMovies.LOG_TAG;

public class FetchAsyncTask extends AsyncTask {
    @Override
    protected Object doInBackground(Object[] objects) {

        String query = (String) objects[0];

        if (query == null) {
            return null;
        } else {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String expectedString;
            try {
                // Construct the URL for the Themoviedb query
                // Possible parameters are avaiable at API page
                URL url = new URL( query );

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
                    Log.v( String.valueOf( LOG_TAG ), "expectedString " + null );
                    return null;
                }
                expectedString = builder.toString();

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
            return expectedString;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate( values );
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute( o );
    }
}
