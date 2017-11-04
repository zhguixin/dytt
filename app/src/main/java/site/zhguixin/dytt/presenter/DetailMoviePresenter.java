package site.zhguixin.dytt.presenter;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import site.zhguixin.dytt.entity.MovieDetailInfo;
import site.zhguixin.dytt.utils.Contants;
import site.zhguixin.dytt.utils.Utils;

/**
 * Created by 10200927 on 2017/10/24.
 */

public class DetailMoviePresenter {

    private final static String TAG = "DetailMoviePresenter";
    private static DetailMoviePresenter detailMoviePresenter;
    private String mUrl;
    private DetailInfoCallback mCallBack;

    public static DetailMoviePresenter getInstance() {
        if (null == detailMoviePresenter) {
            detailMoviePresenter = new DetailMoviePresenter();
        }
        return detailMoviePresenter;
    }

    private DetailMoviePresenter() {

    }

    public void getDetailInfo(String url, DetailInfoCallback callback) {
        mUrl = url;
        mCallBack = callback;
        NetworkPresenter.getInstance().getHtml(mUrl, new IHttpVisitListener() {
            @Override
            public void getHttpResult(String result) {
                praseHtml(result);
            }

            @Override
            public void failed() {
                mCallBack.onFailed();
            }
        });
    }

    private void praseHtml(String result) {
        String temp = Utils.getMidString(result, Contants.MOVIE_INFO_START, Contants.MOVIE_INFO_END);
//        Log.d(TAG, "praseHtml: temp=" + temp);

        String regex_img = "http.*?jpg";
        Pattern pattern_img = Pattern.compile(regex_img);
        Matcher matcher_img = pattern_img.matcher(temp);
        List<String> imgList = new ArrayList<>();
        // 影片相关的URL地址
        while(matcher_img.find()){
            String str = matcher_img.group();
            imgList.add(str);
        }

        Log.d(TAG, "praseHtml: temp = " + temp);

        // 影片的详细介绍
        // 正则匹配以'◎'开头，'<br />'结尾的所有字符串
        String patterRegular = "◎.*<br />";
        Pattern pattern = Pattern.compile(patterRegular);
        Matcher matcher = pattern.matcher(temp);

        // 定义HTML标签的正则表达式
        String reg_html = "<[^>]+>";
        Pattern pat_html = Pattern.compile(reg_html);
        // 定义一些特殊字符的正则表达式 如：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        String reg_special = "\\&[a-zA-Z]{1,10};";
        Pattern pat_special = Pattern.compile(reg_special);

        String content= "";
        // 这里没有做分割处理，直接打印出所有的简介内容
        if(matcher.find()){
            String allInfos = matcher.group();
            // 想办法截取到只包含'<br />'这一个html标签页的字符串
//            Log.d(TAG, "praseHtml: allInfos="+ allInfos);
            if(allInfos.contains("<img")){
                content = Utils.getMidString(allInfos, "", "<br /><br /><img");
            } else {
                content = allInfos;
            }
            // 将'<br />'标签替换为换行符
            content = content.replaceAll("<br />", "\n");

            // 替换可能出现html标签
            content = pat_html.matcher(content).replaceAll("");
            // 替换可能出现html转义字符
//            content = content.replaceAll("&nbsp;", " ");
//            content = content.replaceAll("&middot;", "·");
//            content = content.replaceAll("&eacute;", "é");
//            content = content.replaceAll("&hellip;", "...");
            content = pat_special.matcher(content).replaceAll(" ");
        }
//        Log.d(TAG, "praseHtml: content="+ content);

        // 影片的下载地址
        String downloadUrl = "ftp://" + Utils.getMidString(temp, "ftp://", "\">ftp://");
        MovieDetailInfo info = new MovieDetailInfo(content, downloadUrl, imgList);
        mCallBack.onSuccess(info);
    }
}
