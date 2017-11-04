package site.zhguixin.dytt.presenter;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import site.zhguixin.dytt.entity.MovieInfo;
import site.zhguixin.dytt.utils.Contants;
import site.zhguixin.dytt.utils.Utils;

/**
 * Created by zhguixin on 2017/10/25.
 */

public class NewMoviePresenter {

    public static void getNewMovie(String url, NewMovieInfoCallback callback) {
        final NewMovieInfoCallback mCallback = callback;
        NetworkPresenter.getInstance()
                .getHtml(url, new IHttpVisitListener() {
                    @Override
                    public void getHttpResult(String result) {
                        List<MovieInfo> infoList = praseHtml(result);
                        List<String> pages = praseNext(result);
                        Log.d("zgx", "getHttpResult: size=" + pages.get(0) +
                            " " + pages.get(1));
                        mCallback.onSuccess(infoList, pages);
                    }

                    @Override
                    public void failed() {
                        mCallback.onFailed();
                    }
                });
    }

    public static void getNextPage(String url, NewMovieInfoCallback callback) {
        final NewMovieInfoCallback mCallback = callback;
        NetworkPresenter.getInstance()
                .getHtml(Contants.HOST_URL + url, new IHttpVisitListener() {
                    @Override
                    public void getHttpResult(String result) {
                        List<MovieInfo> infoList = praseHtml(result);
                        mCallback.onSuccess(infoList, null);
                    }

                    @Override
                    public void failed() {
                        mCallback.onFailed();
                    }
                });
    }

    private static List<MovieInfo> praseHtml(String html) {
        String reg_url = "<a href=\"/html/gndy/(.*?)\" class=\"ulink\">(.*?)</a>";
        String reg_title = "(.*?)年(.*?)《(.*?)》.*?";
        Pattern pattern_url = Pattern.compile(reg_url);
        Pattern pattern_title = Pattern.compile(reg_url);
        Matcher matcher = pattern_url.matcher(html);

        List<MovieInfo> infoList = new ArrayList<>();
        while (matcher.find()) {
            String url = matcher.group(1);
            String fullTitle = matcher.group(2);
            String date = Utils.getMidString(url.substring(5), "", "/");
            String title = Utils.getMidString(fullTitle, "《", "》");

//            想加入电影标签的，没有匹配出来。。。
//            Matcher matcher_title = pattern_url.matcher(fullName);
//            String tag = "";
//            if (matcher_title.find()) {
//                tag = matcher_title.group(2);
//            }

            infoList.add(new MovieInfo(Contants.HOST_URL + url, date, title, fullTitle));
        }
        return infoList;
    }

    private static List<String> praseNext(String result) {
        String start = "<select name='sldd'";
        String end = "</select></td></div>";
        String pageTemp = Utils.getMidString(result, start, end);

        String page_reg = "<option value='(.*?)'>";
        Pattern page_pattern = Pattern.compile(page_reg);
        Matcher macther = page_pattern.matcher(pageTemp);

        List<String> pageList = new ArrayList<>();
        while (macther.find()) {
            pageList.add(macther.group(1));
        }

        return pageList;
    }

}
