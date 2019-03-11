package zhevaha.com.popularmovies;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class FilmOverview extends Activity {

    private final String LOG_TAG = "PopularMoview";
    private Film mFilm;
    private List<String> mFilmTrailer;

//    private TrailerAdapter;

    private Bundle mArguments;

    private ImageView mImageView;
    private TextView mTitleView;
    private TextView mReleaseDateView;
    private TextView mOverviewView;
    private RatingBar mRatingBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.film_overview_layout );

        mArguments = getIntent().getExtras();

        mImageView = findViewById( R.id.ivOverview );
        mRatingBarView = findViewById( R.id.ratingBar );
        mTitleView = findViewById( R.id.filmTitle );
        mReleaseDateView = findViewById( R.id.releaseDate );
        mOverviewView = findViewById( R.id.tvOverview );


        if (mArguments != null) {
            mFilm = (Film) mArguments.getSerializable( Film.class.getSimpleName() );
            Picasso.get()
                    .load( "https://image.tmdb.org/t/p/w500" + mFilm.getPosterPath() )
                    .into( mImageView );
            mRatingBarView.setStepSize( (float) 0.5 );
            mRatingBarView.setRating( (float) mFilm.getVoteAverage() / 2 );
            mTitleView.setText( mFilm.getTitle() );

            String date = mFilm.getReleaseDate().substring( 0, 4 );
            mReleaseDateView.setText( date );
            mOverviewView.setText( "\t" + mFilm.getOverview() );

            String apiKey = new ApiKey( this ).getApiKey();
            long filmId = mFilm.getId();
            String query = "https://api.themoviedb.org/3/movie/"+filmId+"/videos?api_key="+apiKey;
            FetchAsyncTask fetchAsyncTask = new FetchAsyncTask();
            fetchAsyncTask.execute( query );



        } else {
            Log.d( LOG_TAG, "mArguments is null" );
        }
    }
}
