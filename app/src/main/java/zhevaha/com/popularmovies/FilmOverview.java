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
    private ImageView mImageView;
    private TextView mTitleView;
    private TextView mReleaseDateView;
    private TextView mOverviewView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.film_overview_layout );

        mArguments = getIntent().getExtras();
        mImageView = findViewById( R.id.ivOverview );
        mTitleView = findViewById( R.id.filmTitle );
        mReleaseDateView = findViewById( R.id.releaseDate );
        mOverviewView = findViewById( R.id.tvOverview );

        if (mArguments != null) {
            mFilm = (Film) mArguments.getSerializable( Film.class.getSimpleName() );
            Picasso.get()
                    .load( "https://image.tmdb.org/t/p/w500" + mFilm.getPosterPath() )
                    .into( mImageView );
            mTitleView.setText( mFilm.getTitle() );
            mReleaseDateView.setText( mFilm.getReleaseDate() );
            mOverviewView.setText( mFilm.getOverview() );

        } else {
            Log.d( LOG_TAG, "mArguments is null" );
        }
    }
}
