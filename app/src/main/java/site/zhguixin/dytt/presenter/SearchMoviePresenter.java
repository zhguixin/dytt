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
 * Created by zhguixin on 2017/11/1.
 */

public class SearchMoviePresenter {

    private static  String mKeywords;

    public static void getMovie(String url, String keywords, NewMovieInfoCallback callback) {
        final NewMovieInfoCallback mCallback = callback;
        mKeywords = keywords;
        NetworkPresenter.getInstance()
                .getHtml(url, new IHttpVisitListener() {
                    @Override
                    public void getHttpResult(String result) {
                        List<MovieInfo> infoList = praseHtml(result);
                        List<String> pages = praseNext(result);
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
        List<MovieInfo> infoList = new ArrayList<>();

        // 没有搜索到相关影片,直接返回
        if(html.indexOf("0条记录") > 0){
//			System.out.println("找不到");
            return infoList;
        }

        String tempHtml = Utils.getMidString(html,
                "<div class=\"co_content8\">", "</table> </td>");
        String reg_url = "href='(.*?)'>(.*?)</a>";
        Pattern pattern_url = Pattern.compile(reg_url);
        Matcher matcher = pattern_url.matcher(tempHtml);

        while (matcher.find()) {
            String url = matcher.group(1);
            String[] strs = url.split("/");
            String date = strs[strs.length -2];
            String fullTitle = matcher.group(2);
            fullTitle = fullTitle.replaceAll("</font>", "")
                    .replaceAll("<font color='red'>", "");

            String title = mKeywords;
            if(fullTitle.contains("《")) {
                title = Utils.getMidString(fullTitle,"《","》");
            }

            infoList.add(new MovieInfo(Contants.SEARCH_HOST + url, date, title, fullTitle));
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
