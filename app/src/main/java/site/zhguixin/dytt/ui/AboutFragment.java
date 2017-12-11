package site.zhguixin.dytt.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import site.zhguixin.dytt.R;
import site.zhguixin.dytt.ui.view.WaveView;
import site.zhguixin.dytt.utils.Contants;
import site.zhguixin.dytt.utils.Utils;

/**
 */
public class AboutFragment extends Fragment {

    private static final String TAG = "AboutFragment";
    private Context mContext;
    private FrameLayout mHeadContainer;
    private ImageView mHeadView;
    private WaveView mWaveView;
    private DialogFragment mDialog;
    private LinearLayout mOtherFunc;
    private LinearLayout mAppVersion;
    private LinearLayout mClearCache;
    private LinearLayout mAboutAuthor;
    private SwitchCompat mSwitchMode;
    private TextView mCacheView;

    private int mTheme;

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDialog = new ImageDialogFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getActivity();
        mHeadContainer = view.findViewById(R.id.head_container);
        mHeadView = view.findViewById(R.id.head_img);
        mOtherFunc = view.findViewById(R.id.other_func);
        mAppVersion = view.findViewById(R.id.app_version);
        mClearCache = view.findViewById(R.id.clear_cache);
        mCacheView = view.findViewById(R.id.cache_view);
        mAboutAuthor = view.findViewById(R.id.about_author);
        mSwitchMode = view.findViewById(R.id.switch_night_mode);
        init();
//        mWaveView = view.findViewById(R.id.wave_view);
//        mWaveView.start();
    }

    private void init() {
        Bitmap original = BitmapFactory.decodeResource(getResources(), R.drawable.head_img);
        Bitmap blurBitmap = Utils.blurBtimap(getActivity(),original);
        mHeadContainer.setBackground(new BitmapDrawable(getResources(),blurBitmap));

        mHeadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBigHeadView();
            }
        });

        // it's a joke
        Random rand = new Random();
        int cache = rand.nextInt(100);
        mCacheView.setText((float)cache/100.0 + "M");
        mClearCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "缓存已清理", Toast.LENGTH_SHORT).show();
                mCacheView.setText("0.0M");
            }
        });

        mAppVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNormalDialog("当前已是最新版本");
            }
        });

        mAboutAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNormalDialog("数据来自电影天堂，敬请支持。\n\nGitHub: https://github.com/zhguixin");
            }
        });

        mSwitchMode.setChecked(Utils.getIsNightTheme(mContext));
        mSwitchMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(mContext, "重启应用生效", Toast.LENGTH_SHORT).show();
                if (isChecked) {
                    Utils.saveTheme(mContext, true);
                } else {
                    Utils.saveTheme(mContext, false);
                }
            }
        });
    }

    private void showBigHeadView() {
        if (!mDialog.isAdded()) {
            mDialog.show(getFragmentManager(), "ImageShowDialog");
        }
    }

    private void showNormalDialog(String info) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(mContext);
        dlg.setMessage(info);
        dlg.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dlg.show();
    }
}
