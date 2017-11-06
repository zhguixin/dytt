package site.zhguixin.dytt.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
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
 * Created by zhguixin on 2017/10/23.
 */

public class Utils {

    private static final String TAG = "Utils";
    // 图片缩放比例
    private static final float BITMAP_SCALE = 0.5f;
    // 图片模糊度
    private static final float BLUR_RADIUS = 25f;

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

    public static Bitmap blurBtimap(Context context, Bitmap originalBitmap) {
        // 计算图片缩小后的长宽
        int width = Math.round(originalBitmap.getWidth() * BITMAP_SCALE);
        int height = Math.round(originalBitmap.getHeight() * BITMAP_SCALE);
        // 将缩小后的图片做为预渲染的图片。
        Bitmap inputBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, false);
        // 创建一张渲染后的输出图片。
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);
        // 创建RenderScript内核对象
        RenderScript rs = RenderScript.create(context);
        // 创建一个模糊效果的RenderScript的工具对象
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        // 由于RenderScript并没有使用VM来分配内存,所以需要使用Allocation类来创建和分配内存空间。
        // 创建Allocation对象的时候其实内存是空的,需要使用copyTo()将数据填充进去。
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        // 设置渲染的模糊程度, 25f是最大模糊度
        blurScript.setRadius(BLUR_RADIUS);
        // 设置blurScript对象的输入内存
        blurScript.setInput(tmpIn);
        // 将输出数据保存到输出内存中
        blurScript.forEach(tmpOut);
        // 将数据填充到Allocation中
        tmpOut.copyTo(outputBitmap);
        return outputBitmap;
    }
}
