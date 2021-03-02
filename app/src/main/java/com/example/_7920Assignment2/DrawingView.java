package com.example._7920Assignment2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

public class DrawingView extends View {

    public static Context context;
    public static Paint mPaint;
    public static String selectedShape="";
    private Bitmap mBitmap;
    public static int selectedColor;
    private Path path;
    List<PathTracker> pathList = new ArrayList<PathTracker>();
    Canvas mCanvas;
    float mStartX;
    float mStartY;
    float mEndX;
    float mEndY;
    private PointF startPoint, endPoint;

    public DrawingView(Context c) {
        //add drawing view to the screen
        super(c);
        context = c;
        path=new Path();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
    }
    public static Paint GetPaintObject()
    {
        return mPaint;
    }

    public static void SetShape (String selectedShapeString)
    {
        selectedShape = selectedShapeString;
    }

    public static void SetPaintColor(int color) {
        selectedColor = color;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.setDrawingCacheEnabled(true);
        buildDrawingCache();
        mBitmap = Bitmap.createBitmap(500, 800, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    public Bitmap getBitmap()
    {
        this.setDrawingCacheEnabled(true);
        this.buildDrawingCache();
        Bitmap bmp = Bitmap.createBitmap(this.getDrawingCache());
        this.setDrawingCacheEnabled(false);
        return bmp;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(selectedShape.equals("Line")) {
            path = new Path();
            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    startPoint = new PointF(event.getX(), event.getY());
                    endPoint = new PointF();
                    path = new Path();
                    break;
                case MotionEvent.ACTION_MOVE:
                    endPoint.x = event.getX();
                    endPoint.y = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    endPoint.x = event.getX();
                    endPoint.y = event.getY();
                    path.moveTo(startPoint.x, startPoint.y);
                    path.lineTo(endPoint.x, endPoint.y);
                    pathList.add(new PathTracker(path, startPoint.x, startPoint.y, endPoint.x, endPoint.y,
                            selectedShape, selectedColor, false));
                    break;
                default:
                    break;
            }
            return true;
        }  else if (selectedShape.equals("Rectangle Solid") || selectedShape.equals("Rectangle Stroke")) {
            boolean isFill = false;
            if (selectedShape.equals("Rectangle Solid"))
                isFill = true;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mStartX = event.getX();
                    mStartY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    mEndX = event.getX();
                    mEndY = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    mEndX = event.getX();
                    mEndY = event.getY();
                    path = new Path();
                    path.addRect(mStartX, mStartY, mEndX, mEndY, Path.Direction.CW);
                    pathList.add(new PathTracker(path, mStartX, mStartY, mEndX, mEndY,
                            selectedShape, selectedColor, isFill));
                    path = new Path();
                    break;
                default:
                    return false;
            }
            return true;
        }else if (selectedShape.equals("Circle Solid") || selectedShape.equals("Circle Stroke")) {
            boolean isFill = false;
            if (selectedShape.equals("Circle Solid"))
                isFill = true;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mStartX = event.getX();
                    mStartY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    mEndX = event.getX();
                    mEndY = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    mEndX = event.getX();
                    mEndY = event.getY();
                    path = new Path();
                    path.addOval(mStartX, mStartY, mEndX, mEndY, Path.Direction.CW);
                    pathList.add(new PathTracker(path, mStartX, mStartY, mEndX, mEndY,
                            selectedShape, selectedColor, isFill));
                    path = new Path();
                    break;
                default:
                    return false;
            }
            return true;
        }else
            return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        if(mBitmap!=null) {
            for (PathTracker pathTracker : pathList) {
                if (pathTracker.getIsFill())
                    mPaint.setStyle(Paint.Style.FILL);
                else
                    mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setColor(pathTracker.getSelectedColor());
                canvas.drawPath(pathTracker.getPathOfObject(), mPaint);
            }
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }
        invalidate();
    }

}
