package site.zhguixin.dytt.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhguixin on 2017/10/28.
 */

public class FragmentController {

    private static FragmentController controller;
    private FragmentManager fm;
    private int mContainerId;
    private List<Fragment> mFragments;

    public static FragmentController getInstance(FragmentActivity activity, int containerId) {
        if (controller == null) {
            controller = new FragmentController(activity, containerId);
        }
        return controller;
    }

    private FragmentController(FragmentActivity activity, int containerId) {
        mContainerId = containerId;
        fm = activity.getSupportFragmentManager();
        initFragment();
    }

    private void initFragment() {
        mFragments = new ArrayList<>();
        mFragments.add(new MainFragment());
        mFragments.add(new SearchFragment());
        mFragments.add(new AboutFragment());

        FragmentTransaction ft = fm.beginTransaction();
        for(Fragment fragment : mFragments) {
            ft.add(mContainerId, fragment);
        }
        ft.commit();
    }

    public void showFragment(int position) {
        hideFragments();
        Fragment fragment = mFragments.get(position);
        FragmentTransaction ft = fm.beginTransaction();
        ft.show(fragment);
        ft.commit();
    }

    public void hideFragments() {
        FragmentTransaction ft = fm.beginTransaction();
        for(Fragment fragment : mFragments) {
            if(fragment != null) {
                ft.hide(fragment);
            }
        }
        // 避免在onSaveInstanceState()之后调用commit报错，这个位置折腾比较久。。
        ft.commit();
    }

    public Fragment getFragment(int position) {
        return mFragments.get(position);
    }

    public static void destoryController(){
        controller = null;
    }
}
