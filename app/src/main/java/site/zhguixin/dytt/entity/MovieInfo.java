package site.zhguixin.dytt.entity;

/**
 * Created by zhguixin on 2017/10/25.
 */

public class MovieInfo {
    private String url;
    private String date;
    private String title;
    private String fullTitle;

    public MovieInfo(String url, String date, String title, String fullTitle) {
        this.url = url;
        this.date = date;
        this.title = title;
        this.fullTitle = fullTitle;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFullTitle() {
        return fullTitle;
    }

    public void setFullTitle(String fullTitle) {
        this.fullTitle = fullTitle;
    }

    @Override
    public String toString() {
        return "MovieInfo{" +
                "url='" + url + '\'' +
                ", date='" + date + '\'' +
                ", title='" + title + '\'' +
                ", fullTitle='" + fullTitle + '\'' +
                '}';
    }
}
