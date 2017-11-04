package site.zhguixin.dytt.entity;

import java.util.List;

/**
 * Created by 10200927 on 2017/10/25.
 */

public class MovieDetailInfo {
    private String content;
    private String downloadUrl;
    private List<String> imgList;

    public MovieDetailInfo(String content, String downloadUrl, List<String> imgList) {
        this.content = content;
        this.downloadUrl = downloadUrl;
        this.imgList = imgList;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String mDownloadUrl) {
        this.downloadUrl = mDownloadUrl;
    }

    public List<String> getImgList() {
        return imgList;
    }

    public void setImgList(List<String> imgList) {
        this.imgList = imgList;
    }

}
