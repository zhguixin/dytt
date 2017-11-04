package site.zhguixin.dytt.presenter;

import java.util.List;

import site.zhguixin.dytt.entity.MovieDetailInfo;
import site.zhguixin.dytt.entity.MovieInfo;

/**
 * Created by zhguixin on 2017/10/27.
 */

public interface NewMovieInfoCallback {
    void onSuccess(List<MovieInfo> info, List<String> pageList);
    void onFailed();
}
