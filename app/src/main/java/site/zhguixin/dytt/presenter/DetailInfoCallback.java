package site.zhguixin.dytt.presenter;

import site.zhguixin.dytt.entity.MovieDetailInfo;

/**
 * Created by 10200927 on 2017/10/25.
 */

public interface DetailInfoCallback {
    void onSuccess(MovieDetailInfo info);
    void onFailed();
}
