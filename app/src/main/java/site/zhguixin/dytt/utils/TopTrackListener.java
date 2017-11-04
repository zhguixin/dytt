package site.zhguixin.dytt.utils;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;


public class TopTrackListener extends RecyclerView.OnScrollListener {

    private static final String TAG = "BottomTrackListener";

    private View mTargetView = null;

    private boolean isAlreadyHide = false, isAlreadyShow = false;

    private ObjectAnimator mAnimator = null;

    private int mLastDy = 0;
    private int mTotalDy = 0;

    public TopTrackListener(View target) {
        if (target == null) {
            throw new IllegalArgumentException("target shouldn't be null");
        }
        mTargetView = target;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            final float transY = mTargetView.getTranslationY();
            int distance = -mTargetView.getBottom();
            if (transY == 0 || transY == distance) {
                return;
            }
            if (mLastDy > 0) {
                mAnimator = animateHide(mTargetView);
            } else {
                mAnimator = animateShow(mTargetView);
            }
        } else if(newState == RecyclerView.SCROLL_STATE_DRAGGING){
            if (mAnimator != null && mAnimator.isRunning()) {
                mAnimator.cancel();
            }
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        mTotalDy -= dy;
        mLastDy = dy;
        final float transY = mTargetView.getTranslationY();
        float newTransY;
        final int distance = -mTargetView.getBottom();

        if (mTotalDy >= distance && dy > 0) {
            return;
        }

        if (isAlreadyHide && dy > 0) {
            return;
        }

        if (isAlreadyShow && dy < 0) {
            return;
        }

        newTransY = transY - dy;
        if (newTransY < distance) {
            newTransY = distance;
        } else if (newTransY == distance) {
            return;
        } else if (newTransY > 0) {
            newTransY = 0;
        } else if (newTransY == 0) {
            return;
        }

        mTargetView.setTranslationY(newTransY);
        isAlreadyHide = newTransY == distance;
        isAlreadyShow = newTransY == 0;
    }

    private ObjectAnimator animateShow (View view) {
        return animationFromTo(view, view.getTranslationY(), 0);
    }

    private ObjectAnimator animateHide (View view) {
        int distance = -view.getBottom();
        return animationFromTo(view, view.getTranslationY(), distance);
    }

    private ObjectAnimator animationFromTo (View view, float start, float end) {
        String propertyName = "translationY";
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, propertyName, start, end);
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = (float) animation.getAnimatedValue();
                Log.d(TAG, "onAnimationUpdate: alpha="+ alpha);
            }
        });
        return animator;
    }
}
