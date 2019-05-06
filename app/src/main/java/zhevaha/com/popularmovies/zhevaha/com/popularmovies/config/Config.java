package zhevaha.com.popularmovies.zhevaha.com.popularmovies.config;

public class Config {
    private Config() {
    }
    private static final String YOUTUBE_API_KEY = "AIzaSyBWl7tCjDPivM840lbhn9Uo0Kf1IR3-wbw";
    private static final String APP_ID = "ca-app-pub-1672089289204149~6075886517";

    public static String getYoutubeApiKey() {
        return YOUTUBE_API_KEY;
    }

    public static String getAppId() {
        return APP_ID;
    }
}
