package zhevaha.com.popularmovies;

public class Language {
    private String mLanguageCod;
    private String englishName;
    private String name;

    private static String getLanguageQuery(){

        String result = "&language=ru";
        return result;
    }
    String query = "https://api.themoviedb.org/3/configuration/languages?api_key=f4ca38bc9fdb107e48dc28c3483ba7a0";

    public String getLanguageCod() {
        return mLanguageCod;
    }

    public void setLanguageCod(String languageCod) {
        mLanguageCod = languageCod;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
