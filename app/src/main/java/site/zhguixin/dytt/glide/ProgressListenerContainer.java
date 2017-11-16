package site.zhguixin.dytt.glide;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhguixin on 2017/11/16.
 */

public class ProgressListenerContainer {

    private static final Map<String, ProgressListener> LISTENER_MAP = new HashMap<>();

    public static void addListener(String url, ProgressListener listener) {
        LISTENER_MAP.put(url, listener);
    }

    public static ProgressListener getListener(String url) {
        return LISTENER_MAP.get(url);
    }

    public static void removeListener(String url) {
        LISTENER_MAP.remove(url);
    }
}
