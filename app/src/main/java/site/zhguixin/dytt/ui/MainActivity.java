package site.zhguixin.dytt.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import site.zhguixin.dytt.R;
import site.zhguixin.dytt.utils.Contants;
import site.zhguixin.dytt.utils.Utils;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    private Context mContext;
    private long mExitTime;
    private FragmentController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(Utils.getIsNightTheme(this) ? R.style.NightAppTheme : R.style.AppTheme);
        setContentView(R.layout.activity_main);
        mContext = this;
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
        }
        Log.d(TAG, "onCreate: savedInstanceState=" + savedInstanceState);
        mController = FragmentController.getInstance(this, R.id.fragment_container);
        mController.showFragment(0);

        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setSelected(true);
        navigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.navigation_home:
                                mController.showFragment(0);
                                break;
                            case R.id.navigation_search:
                                mController.showFragment(1);
                                break;
                            case R.id.navigation_about:
                                mController.showFragment(2);
                                break;
                        }
                        return true;
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 避免下次重建Activity时，报错：
        // java.lang.IllegalStateException: Can not perform this action after onSaveIns
        FragmentController.destoryController();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

}


