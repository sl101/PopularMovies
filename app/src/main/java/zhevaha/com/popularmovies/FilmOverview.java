package zhevaha.com.popularmovies;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class FilmOverview extends Activity {

    private final String LOG_TAG = "PopularMoview";
    private Film mFilm;
    private Bundle mArguments;
    private ImageView image;
    private TextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.film_overview_layout );
        mArguments = getIntent().getExtras();
        image = (ImageView) findViewById( R.id.ivOverview );
        description = (TextView) findViewById( R.id.tvOverview );

        if (mArguments != null) {
            mFilm = (Film) mArguments.getSerializable( Film.class.getSimpleName() );
            Picasso.get()
                    .load( "https://image.tmdb.org/t/p/w500" + mFilm.getPosterPath() )
                    .into( image );
            description.setText( mFilm.getOverview() );

        } else {
            Log.d( LOG_TAG, "mArguments is null" );
        }
    }
}
