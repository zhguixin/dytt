package site.zhguixin.dytt.ui;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

/**
 * Created by zhguixin on 2017/10/29.
 */

public class MoviePagerAdapter extends FragmentPagerAdapter {

    private static final int COUNT = 5;
    private String[] titles = new String[]{"最新电影", "国内经典", "日韩电影", "欧美电影", "综合电影"};
    private String[] urls = new String[]{"dyzz", "china", "rihan", "oumei", "jddy"};
    private Context mContext;

    public MoviePagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        return MovieFragment.newInstance(urls[position]);
    }

    @Override
    public int getCount() {
        return COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Log.d("zgx", "destroyItem: ");
//        super.destroyItem(container, position, object);
    }

}
