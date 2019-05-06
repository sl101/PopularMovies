package zhevaha.com.popularmovies;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.ExecutionException;

import zhevaha.com.popularmovies.zhevaha.com.popularmovies.config.ApiKey;


public class FilmOverview extends Activity {

    //    private static final String LOG_TAG = "PopularMoview";
    private Film mFilm;
    private String apiKey;
    private List<String> mFilmTrailers;

    private TrailerAdapter mAdapter;

    private Bundle mArguments;

    private ImageView mImageView;
    private TextView mTitleView;
    private TextView mReleaseDateView;
    private TextView mOverviewView;
    private RatingBar mRatingBarView;
    private RecyclerView mRecyclerView;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.film_overview_layout );

//        Log.d( LOG_TAG, "start FilmOverview class " + getClass().toString() );
        apiKey = ApiKey.getInstance( this ).getApiKey();
        mArguments = getIntent().getExtras();

        mImageView = findViewById( R.id.ivOverview );
        mRatingBarView = findViewById( R.id.ratingBar );
        mTitleView = findViewById( R.id.filmTitle );
        mReleaseDateView = findViewById( R.id.releaseDate );
        mOverviewView = findViewById( R.id.tvOverview );
        mAdView = findViewById( R.id.adView );
        setUpRecyclerView();
        initAdMObBlock();

        if (mArguments != null) {
            mFilm = (Film) mArguments.getSerializable( Film.class.getSimpleName() );
            Picasso.get()
                    .load( "https://image.tmdb.org/t/p/w500" + mFilm.getPosterPath() )
                    .into( mImageView );
            mRatingBarView.setStepSize( (float) 0.5 );
            mRatingBarView.setRating( (float) mFilm.getVoteAverage() / 2 );

            mTitleView.setText( mFilm.getFilmTitle() );

            String date = mFilm.getReleaseDate().substring( 0, 4 );
            mReleaseDateView.setText( date );

            mOverviewView.setText( "\t" + mFilm.getOverview() );

            long filmId = mFilm.getId();
//            need to chenge for any language
            String language = "&language=ru";
            String query = "https://api.themoviedb.org/3/movie/" + filmId + "/videos?api_key=" + apiKey + language;
            FetchAsyncTask fetchAsyncTask = new FetchAsyncTask();
            fetchAsyncTask.execute( query );
            try {
//                Log.d( LOG_TAG, "films Uri: \n"+ String.valueOf( fetchAsyncTask.get() ) );
                mFilm.setTrailersUri( String.valueOf( fetchAsyncTask.get() ) );
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
//            Log.d( LOG_TAG, "mArguments is null" );
        }

        mFilmTrailers = mFilm.getTrailersUri();
//        Log.d( LOG_TAG, "mItemsArray  " + mFilmTrailers );
        mAdapter = new TrailerAdapter( this, mFilmTrailers );
        mRecyclerView.setAdapter( mAdapter );

    }

    private void initAdMObBlock() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd( adRequest );
    }

    private void setUpRecyclerView() {
        mRecyclerView = findViewById( R.id.recycler_view );
        mRecyclerView.setHasFixedSize( true );

        //Horizontal direction recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager( this, LinearLayoutManager.HORIZONTAL, false );
        mRecyclerView.setLayoutManager( linearLayoutManager );
    }

}
