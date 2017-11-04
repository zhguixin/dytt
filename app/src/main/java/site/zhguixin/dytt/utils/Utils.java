package site.zhguixin.dytt.utils;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import site.zhguixin.dytt.entity.MovieBean;
import site.zhguixin.dytt.entity.MovieInfo;

/**
 * Created by 10200927 on 2017/10/23.
 */

public class Utils {

    private static final String TAG = "Utils";

    public static void main(String[] args) {
//        praseHtml(html);
    }

    public static List<MovieBean> readFromAssets(Context context) {
        StringBuilder builder = new StringBuilder();
        try {
            InputStream is = context.getResources().getAssets().open("movies.json");
            BufferedReader bufr = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line ;
            while((line = bufr.readLine()) != null){
                builder.append(line);
            }
            is.close();
            bufr.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        List<MovieBean> movies =
                gson.fromJson(builder.toString(), new TypeToken<List<MovieBean>>(){}.getType());
        return movies;
    }

    public static String getMidString(String str, String start, String end) {
        if (str == null)
            return "";
        int startIndex = str.indexOf(start) + start.length();
        int endIndex = str.indexOf(end);
        if (startIndex >= endIndex || startIndex < 0)
            return "";
        return str.substring(startIndex, endIndex);
    }

    /**
     * dp值转像素值
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     *像素值转dp值
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将待搜索关键字编码
     */
    public static String encodeKeywod(String text){
        String encodeText = null;
        try {
            encodeText = java.net.URLEncoder.encode(text, "gb2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodeText;
    }

    /**
     * 关闭软键盘
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null && imm.isActive()&& activity.getCurrentFocus()!=null){
            if (activity.getCurrentFocus().getWindowToken()!=null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
