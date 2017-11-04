package site.zhguixin.dytt.ui.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import site.zhguixin.dytt.R;

/**
 * Created by zhguixin on 2017/11/1.
 */

public class WaveView extends View {

    private Path mPath;
    private Paint mPaint;
    private int mWaveLength = 6000;
    private int mOffset;
    private int mScreenHeight;
    private int mScreenWidth;
    private int mWaveCount;
    private int mCenterY;
    private int mHeight;
    private ValueAnimator mAnimator;

    public WaveView(Context context) {
        super(context);
        init();
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mPath = new Path();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(Color.GRAY);
        mAnimator = ValueAnimator.ofInt(0, mWaveLength);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mScreenHeight = h;
        mScreenWidth = w;
        mWaveCount = (int) Math.round(mScreenWidth / mWaveLength + 1.5);
        mCenterY = mScreenHeight / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPath.reset();
        mPath.moveTo(-mWaveLength + mOffset, mCenterY);
        for (int i = 0; i < mWaveCount; i++) {
            // + (i * mWaveLength)
            // + mOffset
            mPath.quadTo((-mWaveLength * 3 / 4) + (i * mWaveLength) + mOffset,
                    mCenterY + mHeight, (-mWaveLength / 2) + (i * mWaveLength) + mOffset, mCenterY);
            mPath.quadTo((-mWaveLength / 4) + (i * mWaveLength) + mOffset, mCenterY - mHeight,
                    i * mWaveLength + mOffset, mCenterY);
        }
        mPath.lineTo(mScreenWidth, mScreenHeight * 3/4);
        mPath.lineTo(0, mScreenHeight *3/4);
        mPath.close();
        canvas.drawPath(mPath, mPaint);
    }

    public void start() {
        mAnimator = ValueAnimator.ofInt(0, mWaveLength);
        mAnimator.setDuration(8000);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOffset = (int) animation.getAnimatedValue();
                mHeight = mOffset * 3/100;
                postInvalidate();
            }
        });
        mAnimator.start();
    }

    public void cancel() {
        mAnimator.cancel();
    }
}
