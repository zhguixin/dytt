package site.zhguixin.dytt.glide;

import android.support.annotation.Nullable;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by zhguixin on 2017/11/16.
 */

public class ProgressReponseBody extends ResponseBody {

    private ResponseBody mResponseBody;
    private ProgressListener mListener;
    private BufferedSource mBufferedSource;

    public ProgressReponseBody(ResponseBody responseBody, String url){
        mResponseBody = responseBody;
        // 为了保证不同的url，有不同的回调，加入了ProgressListenerContainer
        mListener = ProgressListenerContainer.getListener(url);
        if (mListener != null) {
            mListener.onPreExecute(contentLength());
        }
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }

    @Override
    public long contentLength() {
        return mResponseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (mBufferedSource == null) {
            mBufferedSource = Okio.buffer(source(mResponseBody.source()));
        }
        return mBufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytes = 0L;
            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                // read()函数返回已读取得字节数，返回 -1 表示已经读取完毕
                long bytesRead = super.read(sink, byteCount);
                totalBytes += (bytesRead == -1 ? 0 : bytesRead);
                if (mListener != null) {
                    mListener.update(totalBytes, bytesRead == -1);
                }
                return bytesRead;
            }
        };
    }
}
