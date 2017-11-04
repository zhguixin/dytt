package site.zhguixin.dytt.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import site.zhguixin.dytt.MainApp;
import site.zhguixin.dytt.R;
import site.zhguixin.dytt.entity.MovieDetailInfo;
import site.zhguixin.dytt.presenter.DetailInfoCallback;
import site.zhguixin.dytt.presenter.DetailMoviePresenter;
import site.zhguixin.dytt.utils.Contants;
import site.zhguixin.dytt.utils.URLEncode;
import site.zhguixin.dytt.utils.Utils;

public class MovieDetailActivity extends AppCompatActivity {

    private final static String TAG = "MovieDetailActivity";

    private Context mContext;
    private String mDownloadLink;
    private TextView mIntroduceView;
    private TextView mDownloadUrlView;
    private ImageView mAlbumImageView;
    private ProgressBar mLoadingBar;
    private CollapsingToolbarLayout mToolBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = this;
        mIntroduceView = findViewById(R.id.movie_introduce);
        mDownloadUrlView = findViewById(R.id.download_url);
        mAlbumImageView = findViewById(R.id.album);
        mLoadingBar = findViewById(R.id.loading_detail_bar);
        mToolBarLayout = findViewById(R.id.toolbar_layout);

        init();
    }

    public void init() {
        Intent intent = getIntent();
        String url = intent.getStringExtra(Contants.URL);
        Log.d(TAG, "onCreate: url = " + url);
        String name = intent.getStringExtra(Contants.NAME);
        mToolBarLayout.setTitle(name);
        DetailMoviePresenter.getInstance().getDetailInfo(url, new DetailInfoCallback() {
            @Override
            public void onSuccess(MovieDetailInfo info) {
                final MovieDetailInfo movieInfo = info;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLoadingBar.setVisibility(View.GONE);
                        mDownloadUrlView.setText(movieInfo.getDownloadUrl());
                        mIntroduceView.setText(movieInfo.getContent());
                        mDownloadLink = movieInfo.getDownloadUrl();
                        Glide.with(MainApp.getAppContext())
                                .load(movieInfo.getImgList().get(0))
                                .override(1200, 1700)
                                .centerCrop()
                                .placeholder(R.drawable.place_holder)
                                .into(mAlbumImageView);
                    }
                });
            }

            @Override
            public void onFailed() {
                Log.d(TAG, "onFailed: ");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLoadingBar.setVisibility(View.GONE);
                        Toast.makeText(mContext,"网络连接超时，请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void download(View view) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(null, mDownloadLink);
        if (cm != null) {
            cm.setPrimaryClip(clipData);
        }
        Snackbar.make(this.findViewById(R.id.main_content),
                "下载链接已复制，可以手动打开迅雷下载", Snackbar.LENGTH_LONG)
                .setAction("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: open Thunder");
                        downloadViaThunder(mDownloadLink);
                    }
                }).show();
    }

    private void downloadViaThunder(String link) {
        if (checkIsInstall(Contants.XUNLEI_PACKAGENAME)) {
            mContext.startActivity(new Intent("android.intent.action.VIEW",
                    Uri.parse(URLEncode.thunderEncode(link))));
        }else{
            Toast.makeText(this, "您还没有安装迅雷, 请先下载手机迅雷",
                    Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkIsInstall(String paramString) {
        if ((paramString == null) || ("".equals(paramString)))
            return false;
        try {
            mContext.getPackageManager().getApplicationInfo(paramString, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}
