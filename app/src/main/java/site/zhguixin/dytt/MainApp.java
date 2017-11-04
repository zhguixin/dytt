package site.zhguixin.dytt;

import android.app.Application;
import android.content.Context;

/**
 * Created by zhguixin on 2017/10/30.
 */

public class MainApp extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return mContext;
    }
}
