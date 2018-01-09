package com.wolf.jumphelp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

/**
 * Created by Roye on 2018/1/8.
 */

public class TiTView extends View implements View.OnTouchListener {

    GestureDetector mGestureDetector;
    boolean isStartAnim;
    long cms, jumpMs;
    float x, y;
    int action;
    ClickCallback mClickCallback;
    int screenWidth;
    List<ImagePosColor> mPoints;

    public TiTView(Context context) {
        super(context, null);
        setBackgroundColor(0x00000000);
    }

    public TiTView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(0x00000000);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        action = event.getAction();
        if (event.getAction() == MotionEvent.ACTION_UP) {
            android.util.Log.d("www", "ACTION_UP");
            mClickCallback.doJump(jumpMs);
            //setClick(0, 0, 0);
        } else if (event.getAction() == MotionEvent.ACTION_POINTER_DOWN) {
            android.util.Log.d("www", "ACTION_POINTER_DOWN");
        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
            android.util.Log.d("www", "ACTION_DOWN " + event.getX() + " " + event.getY());
            //setClick(System.currentTimeMillis(), event.getX(), event.getY());
        }

        return true;
    }

    public void setClickCallback(ClickCallback clickCallback) {
        this.mClickCallback = clickCallback;
    }

    public void setAnim(boolean isStartAnim) {
        this.isStartAnim = isStartAnim;
    }

    public void setStartPoints(List<ImagePosColor> points) {
        mPoints = points;
        android.util.Log.d("www", mPoints.toString());
        invalidate();
    }

    void setClick(long cms, float x, float y) {
        this.cms = cms;
        this.x = x;
        this.y = y;
        isStartAnim = x > 0;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        if (MotionEvent.ACTION_UP == action && jumpMs > 0 && jumpMs < 10_000) {
//            mClickCallback.doJump(jumpMs);
//            Paint paint = new Paint();
//            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//            canvas.drawPaint(paint);
//            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD.SRC));
//            return;
//        }
//
//        long jumpMs1 = System.currentTimeMillis() - cms;
//        jumpMs = jumpMs1;
//        float radius = (jumpMs1) / 1.35f;
//
//        if (cms == 0) {
//            radius = 0;
//            jumpMs = 0;
//        }
//
//        Paint paint = new Paint();
//        paint.setColor(0xffFF5E4D);
//        android.util.Log.d("www", " x y r " + x + " " + y + " " + radius);
//
//        //canvas.save();
//
//        canvas.drawCircle(x, y, radius, paint);
//
//        //canvas.restore();
//
//        if (isStartAnim) {
//            invalidate();
//        }
        if (mPoints != null) {
            android.util.Log.d("www", "mPoints.size=====" + mPoints.size());
            Paint paint = new Paint();
            paint.setColor(0xffFF5E4D);
            paint.setStrokeWidth((float) 20.0);
            int maxy = 0, maxyx = 0, mcolor = 0;
            for (int i = 0; i < mPoints.size(); i++) {
                ImagePosColor colorpos = mPoints.get(i);
                if (colorpos.y > maxy) {
                    maxy = colorpos.y;
                    maxyx = colorpos.x;
                    mcolor = colorpos.color;
                }
            }

            canvas.drawPoint(maxyx, maxy-getTop(), paint);
            android.util.Log.d("www", String.format("%d %d %d", maxyx, maxy, getTop()));
        }

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }
}
