package zhevaha.com.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends ArrayAdapter<Film> {


    private final String LOG_TAG = "PopularMoview";
    private LayoutInflater lInflater;
    private Context mContext;
    private List<Film> filmsArray;


    public ImageAdapter(Context c, List<Film> filmsArray) {

        super( c, 0, filmsArray );
        mContext = c;
        this.filmsArray = filmsArray;
        lInflater = (LayoutInflater) mContext
                .getSystemService( Context.LAYOUT_INFLATER_SERVICE );
    }

    @Override
    public int getCount() {
        return filmsArray.size();
    }

    @Override
    public Film getItem(int position) {
        return filmsArray.get( position );
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Film film = filmsArray.get( position );
        View view = convertView;
        if (view == null) {
            // if it's not recycled, initialize some attributes
            view = lInflater.inflate( R.layout.item_view, parent, false );
        }
        ImageView imageView = view.findViewById( R.id.imageView );

//        RatingBar smallRatingBar = (RatingBar) view.findViewById(R.id.ratingBar);
//        smallRatingBar.setNumStars(5);

//        TextView textView = (TextView) view.findViewById(R.id.textView);
//        textView.setText(""+film.getTitle());

//        Film film = filmsArray.get( position );
        Picasso.get()
                .load( "https://image.tmdb.org/t/p/w500" + film.getPosterPath() )
                .into( imageView );

//        smallRatingBar.setRating((float) film.getVoteAverage());
//            textView.setText(""+film.getTitle());
//            Log.d(LOG_TAG, film.getPosterPath());
        return view;
    }
}
