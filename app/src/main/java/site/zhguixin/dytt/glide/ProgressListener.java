package site.zhguixin.dytt.glide;

/**
 * Created by zhguixin on 2017/11/16.
 */

public interface ProgressListener {
    void onPreExecute(long contentLength);
    void update(long totalBytes, boolean done);
}
