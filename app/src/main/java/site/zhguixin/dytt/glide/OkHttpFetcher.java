package site.zhguixin.dytt.glide;

import android.util.Log;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.util.ContentLengthInputStream;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by zhguixin on 2017/11/14.
 */

public class OkHttpFetcher implements DataFetcher<InputStream> {

    private OkHttpClient mClient;
    private GlideUrl mUrl;
    private InputStream mInputStream;
    private ResponseBody mResponseBody;
    private volatile boolean isCanceled;

    public OkHttpFetcher(OkHttpClient client, GlideUrl url) {
        mClient = client;
        mUrl = url;
    }

    @Override
    public InputStream loadData(Priority priority) throws Exception {
        Request.Builder builder = new Request.Builder()
                .addHeader("Accept-Encoding", "identity")
                .url(mUrl.toURL());
        Request request = builder.build();

        if(isCanceled) {
            return null;
        }

        Response response = mClient.newCall(request).execute();
        mResponseBody = response.body();
        if(mResponseBody == null || !response.isSuccessful()) {
            throw new Exception("Request Failed with code= " + response.code());
        }

        Log.d("zgx", "loadData: " + mResponseBody.contentLength());
        mInputStream = ContentLengthInputStream.obtain(mResponseBody.byteStream(),
                mResponseBody.contentLength());
        return mInputStream;
    }

    @Override
    public void cleanup() {
        try {
            if (mInputStream != null) {
                mInputStream.close();
            }
            if (mResponseBody != null) {
                mResponseBody.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getId() {
        return mUrl.getCacheKey();
    }

    @Override
    public void cancel() {
        isCanceled = true;
    }
}
