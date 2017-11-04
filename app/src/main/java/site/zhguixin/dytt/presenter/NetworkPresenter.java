package site.zhguixin.dytt.presenter;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import site.zhguixin.dytt.MainApp;
import site.zhguixin.dytt.utils.Contants;

/**
 * Created by zhguixin on 2017/10/25.
 */

public class NetworkPresenter {
    final static String TAG = "NetworkPresenter";

    private OkHttpClient mHttpClient;
    private IHttpVisitListener mListener;
    private static NetworkPresenter networkPresenter;

    public static  synchronized NetworkPresenter getInstance() {
        if(networkPresenter == null) {
            networkPresenter = new NetworkPresenter();
        }
        return networkPresenter;
    }

    private NetworkPresenter() {
        // 创建缓存文件夹
        File cacheFile = new File(MainApp.getAppContext().getCacheDir().toString(),
                "dytt_cache");
        // 设置缓存大小为10M
        int cacheSize = 10 * 1024 * 1024;
        // 创建缓存对象
        Cache cache = new Cache(cacheFile,cacheSize);
        mHttpClient = new OkHttpClient.Builder()
                .cache(cache)
                .readTimeout(60L, TimeUnit.SECONDS)
                .writeTimeout(60L, TimeUnit.SECONDS)
                .build();
    }

    public void getHtml(String url, IHttpVisitListener listener) {
        mListener = listener;
        Log.d(TAG, "getHtml: url=" + url);

        final Request request = new Request.Builder()
                .header("User-Agent", Contants.AGENT_NAME)
                .url(url)
                .build();

        final StringBuilder tempResult = new StringBuilder();
        mHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, " visit intent failed");
                mListener.failed();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "onResponse: ");
                InputStream inputStream = null;
                try {
                    inputStream = response.body().byteStream();
                    InputStreamReader instreamReader = new InputStreamReader(inputStream, "gb2312");
                    BufferedReader buffStr = new BufferedReader(instreamReader);
                    String temp = "";
                    while ((temp = buffStr.readLine()) != null) {
                        tempResult.append(temp + "\n");
                    }
                    inputStream.close();
                    mListener.getHttpResult(tempResult.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if(inputStream != null) {
                        inputStream.close();
                    }
                }
            }
        });
    }

}
