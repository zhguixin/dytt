package site.zhguixin.dytt.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
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
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import site.zhguixin.dytt.MainApp;
import site.zhguixin.dytt.R;
import site.zhguixin.dytt.entity.MovieDetailInfo;
import site.zhguixin.dytt.glide.ProgressListener;
import site.zhguixin.dytt.glide.ProgressListenerContainer;
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
                        updateUI(movieInfo);
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

    private void updateUI(MovieDetailInfo movieInfo) {
        mLoadingBar.setVisibility(View.GONE);
        mDownloadUrlView.setText(movieInfo.getDownloadUrl());
        mIntroduceView.setText(movieInfo.getContent());
        mDownloadLink = movieInfo.getDownloadUrl();
        final String url = movieInfo.getImgList().get(0);
        
        ProgressListenerContainer.addListener(url, new ProgressListener() {
            @Override
            public void onPreExecute(long contentLength) {
                // 由于文件总大小未知，无法通过进度条显示
                // 这一块的内容要详细了解HTTP协议
//                Log.d(TAG, "onPreExecute: contentLength=" + contentLength);
            }

            @Override
            public void update(long totalBytes, boolean done) {
//                Log.d(TAG, "update: totalBytes=" + totalBytes + " done=" + done);
            }
        });

        Glide.with(MainApp.getAppContext())
                .load(url)
                .override(1200, 1700)
                .crossFade()
                .centerCrop()
                .placeholder(R.drawable.place_holder)
                .into(new GlideDrawableImageViewTarget(mAlbumImageView) {

                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        Log.d(TAG, "onLoadStarted: ");
                    }

                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);
                        Log.d(TAG, "onResourceReady: ");
                        ProgressListenerContainer.removeListener(url);
                    }
                });
    }
}
