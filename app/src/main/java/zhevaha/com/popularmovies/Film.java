package zhevaha.com.popularmovies;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


class Film implements Serializable {


    private final String LOG_TAG = "PopularMovies";

    private String originalTitle, mTitle, overview, original_language, posterPath, backdropPath, adult, releaseDate;
//    private String trailerId, trailerKey, trailerName, trailerSite, trailerType;
    private double popularity, voteAverage;
//    private int trailerSize;
    private long id, voteCount;
    private int[] genreIds;
    private boolean video;
    private String apiKey;
//    private List<Trailer> mTrailers;
    private List<String> mTrailers;

//    public Film(){

//        apiKey = new ApiKey().getApiKey();
//        apiKey = new ApiKey(this).getApiKey();
//        Log.d( String.valueOf( LOG_TAG ), "Film Api Key = " + apiKey );
//    }

    public List<String> getTrailersUri() {
//        public List<Trailer> getTrailersUri() {
        return mTrailers;
    }

    public void setTrailersUri(String filmTrailers) {
        mTrailers = new ArrayList<>( );
        // These are the names of the JSON objects that need to be extracted.
        final String TRAILER_ID = "id";
        final String ISO_COD = "iso_639_1";
        final String TRAILER_KEY = "key";
        final String TRAILER_NAME = "name";
        final String TRAILER_SITE = "site";
        final String TRAILER_SIZE = "size";
        final String TRAILER_TYPE = "type";

//        List<Trailer> resultStrs = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject( filmTrailers );
            JSONArray jsonArray = jsonObject.getJSONArray( "results" );

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject trailerJson = jsonArray.getJSONObject( i );
                Trailer trailer = new Trailer();
                String trailerId = trailerJson.getString( TRAILER_ID );
                trailer.setTrailerId( trailerId );
                String iso = trailerJson.getString( ISO_COD );
                trailer.setIsoCOD( iso );
                String trailerKey = trailerJson.getString( TRAILER_KEY );
                trailer.setTrailerKey( trailerKey );
                String trailerName = trailerJson.getString( TRAILER_NAME );
                trailer.setTrailerName( trailerName );
                String trailerSite = trailerJson.getString( TRAILER_SITE );
                trailer.setTrailerSite( trailerSite );
                int trailerSize = trailerJson.getInt( TRAILER_SIZE );
                trailer.setTrailerSize( trailerSize );
                String trailerType = trailerJson.getString( TRAILER_TYPE );
                trailer.setTrailerType( trailerType );

//                String query = "https://api.themoviedb.org/3/movie/"+"/videos?api_key=<<api_key>>&language=en-US";
//                String query = "https://api.themoviedb.org/3/movie/"+trailer.getTrailerKey()+"/images?api_key="+apiKey+"&language="+trailer.getIsoCOD();
//                String query = "https://www.youtube.com/watch?v="+trailer.getTrailerKey();
                String query = trailer.getTrailerKey();
                mTrailers.add( query );
            }
        } catch (JSONException e) {
            Log.e( LOG_TAG , "JSONException: \n" + e.toString() );
        }
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getFilmTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public void setOriginal_language(String original_language) {
        this.original_language = original_language;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getAdult() {
        return adult;
    }

    public void setAdult(String adult) {
        this.adult = adult;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(long voteCount) {
        this.voteCount = voteCount;
    }

    public int[] getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(int[] genreIds) {
        this.genreIds = genreIds;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

}
