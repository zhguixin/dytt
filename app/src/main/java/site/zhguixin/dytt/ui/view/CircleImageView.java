package site.zhguixin.dytt.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by 10200927 on 2017/11/4.
 */

public class CircleImageView extends AppCompatImageView {

    private Paint mPaintCircle;
    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;
    private int mWidth;
    private int mHeight;
    private int mRadius;
    private static final int STROKE_WIDTH = 8;
    private static final int mCircleColor = Color.RED;

    public CircleImageView(Context context) {
        super(context);
        init();
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mPaintCircle = new Paint();
//        mPaintCircle.setAntiAlias(true);
//        BitmapDrawable bitmapDrawable = (BitmapDrawable)getDrawable();
//        mBitmap = bitmapDrawable.getBitmap();
//        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
//
//        mPaintCircle.setShader(mBitmapShader);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        mWidth = getWidth();
//        mHeight = getHeight();
//        int mCircleSize = Math.min(mHeight, mWidth);
//        mRadius = mCircleSize / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);

        Drawable drawable = getDrawable();
        if(drawable != null){
            Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
            if(bitmap != null){
                Bitmap b = circleBitmap(scaleBitmap(bitmap));
                final Rect rect = new Rect(0, 0, b.getWidth(), b.getHeight());
                mPaintCircle.reset();
                canvas.drawBitmap(b,rect,rect,mPaintCircle);
            }
        } else {
            super.onDraw(canvas);
        }
//        canvas.drawCircle(500,500,30,mPaintCircle);
    }

    private Bitmap circleBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff000000;
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        mPaintCircle.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        mPaintCircle.setColor(color);
        final int width = bitmap.getWidth();
        canvas.drawCircle(width / 2, width / 2, width / 2, mPaintCircle);
        mPaintCircle.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, mPaintCircle);

        //外围白圈
        if (mCircleColor != 0) {
            mPaintCircle.reset();
            mPaintCircle.setColor(mCircleColor);
            mPaintCircle.setStyle(Paint.Style.STROKE);
            mPaintCircle.setStrokeWidth(STROKE_WIDTH);
            mPaintCircle.setAntiAlias(true);
            canvas.drawCircle(width / 2, width / 2, width / 2 - STROKE_WIDTH / 2, mPaintCircle);
        }

        return output;
    }

    private Bitmap scaleBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        final float destWidth = this.getWidth();
        final float destHeight = this.getHeight();

        float scaleWidth = destWidth / width;
        float scaleHeight = destHeight / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }
}
