package zhevaha.com.popularmovies;

public class Language {
    private String englishName;
    private String name;

    private static String getLanguageQuery(){

        String result = "&language=ru";
        return result;
    }
    String query = "https://api.themoviedb.org/3/configuration/languages?api_key=f4ca38bc9fdb107e48dc28c3483ba7a0";
}
