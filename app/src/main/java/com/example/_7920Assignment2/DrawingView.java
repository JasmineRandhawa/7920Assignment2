package com.example._7920Assignment2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;


import androidx.constraintlayout.widget.ConstraintLayout;

public class DrawingView extends View {

    public static DrawingView dv;
    private Canvas mCanvas;
    private Path mPath;
    public static Context context;
    public static ConstraintLayout drawingViewLayout;
    public static Paint mPaint;
    public static int selectedColor;


    float mStartX;
    float mStartY;
    float mEndX;
    float mEndY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = event.getX();
                mStartY = event.getY();

                break;
            // return true;
            case MotionEvent.ACTION_MOVE:

                mEndX = event.getX();
                mEndY = event.getY();

                //mCanvas.drawRect(mStartX, mStartY, mEndX, mEndY, mPaint);

                invalidate(); // Tell View that the canvas needs to be redrawn
                break;
            case MotionEvent.ACTION_UP:
                mEndX = event.getX();
                mEndY = event.getY();
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        canvas.drawRect(mStartX, mStartY, mEndX, mEndY, mPaint);
        invalidate();
    }

    public static Paint GetPaintObject()
    {
        return mPaint;
    }

    public DrawingView(Context c, ConstraintLayout layout, int color) {
        //add drawing view to the screen
        super(c);
        context = c;
        selectedColor = color;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(selectedColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
    }

    public static void Clear(View v) {
        drawingViewLayout.removeView(dv);
        DrawingView dv = new DrawingView(context,drawingViewLayout,selectedColor);
    }
}
