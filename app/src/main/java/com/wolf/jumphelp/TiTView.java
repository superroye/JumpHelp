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
    ImagePosColor currentPos, targetPos;

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
        currentPos = new ImagePosColor(0, 0, 0);
        targetPos = new ImagePosColor(0, 0, 0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        action = event.getAction();
        if (event.getAction() == MotionEvent.ACTION_UP) {
            android.util.Log.d("www", "ACTION_UP");
            setClick(event.getX(), event.getY());
            mClickCallback.screencap();
        } else if (event.getAction() == MotionEvent.ACTION_POINTER_DOWN) {
            android.util.Log.d("www", "ACTION_POINTER_DOWN");
        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
            android.util.Log.d("www", "ACTION_DOWN " + event.getX() + " " + event.getY());
        }

        return true;
    }

    public void setClickCallback(ClickCallback clickCallback) {
        this.mClickCallback = clickCallback;
    }


    public void setStartPoints(List<ImagePosColor> points) {
        mPoints = points;
        invalidate();
    }

    void setClick(float x, float y) {
        targetPos.x = (int) x;
        targetPos.y = (int) y;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPoints != null) {
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

            canvas.drawPoint(maxyx, maxy - getTop(), paint);

            double _x = Math.abs(maxyx - targetPos.x);
            double _y = Math.abs(maxy - getTop() - targetPos.y);
            mClickCallback.doJump((int) (Math.sqrt(_x * _x + _y * _y) * 1.35));

            android.util.Log.d("www", String.format("%s %s %s %s", String.valueOf(maxyx),String.valueOf(maxy),String.valueOf(targetPos.x),String.valueOf(targetPos.y)));
        }

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }
}
