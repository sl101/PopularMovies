package zhevaha.com.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.util.List;

public class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

//    private final String LOG_TAG = "YoutubeTlayerTest";

    TextView mTextView;
    YouTubeThumbnailView mYouTubePlayer;
    CardView mCardView;
    YouTubePlayerSupportFragment mYouTubePlayerSupportFragment;

    RelativeLayout relativeLayoutOverYouTubeThumbnailView;
    YouTubeThumbnailView youTubeThumbnailView;
    ImageView playButton;
    List<String> mItemsArray;
    private Context mContext;

    public VideoViewHolder(Context activityContext, View itemView, List<String> itemsArray) {

        super( itemView );

        mContext = activityContext;
        mItemsArray = itemsArray;

        playButton = (ImageView) itemView.findViewById( R.id.btnYoutube_player );
        playButton.setOnClickListener( this );
        relativeLayoutOverYouTubeThumbnailView = (RelativeLayout) itemView.findViewById( R.id.relativeLayout_over_youtube_thumbnail );
        youTubeThumbnailView = (YouTubeThumbnailView) itemView.findViewById( R.id.youtube_thumbnail );
        youTubeThumbnailView.setOnClickListener( this );
    }

    @Override
    public void onClick(View v) {

        Intent intent = YouTubeStandalonePlayer.createVideoIntent( (Activity) mContext, Config.YOUTUBE_API_KEY, mItemsArray.get( getLayoutPosition() ) );
        mContext.startActivity( intent );
    }
}
