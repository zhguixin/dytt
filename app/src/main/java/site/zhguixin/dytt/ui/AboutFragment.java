package site.zhguixin.dytt.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import site.zhguixin.dytt.R;
import site.zhguixin.dytt.ui.view.WaveView;
import site.zhguixin.dytt.utils.Utils;

/**
 */
public class AboutFragment extends Fragment {

    private static final String TAG = "AboutFragment";
    private Context mContext;
    private FrameLayout mHeadContainer;
    private WaveView mWaveView;

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        init();
//        mWaveView = view.findViewById(R.id.wave_view);
//        mWaveView.start();
    }

    private void init(){
        Bitmap original = BitmapFactory.decodeResource(getResources(), R.drawable.head_img);
        Bitmap blurBitmap = Utils.blurBtimap(getActivity(),original);
        mHeadContainer.setBackground(new BitmapDrawable(getResources(),blurBitmap));
    }
}
