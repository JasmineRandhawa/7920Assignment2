package com.example._7920Assignment2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;


import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.List;

public class DrawingView extends View {

    public static Context context;
    public static Paint mPaint;
    public static String selectedShape="";
    private Bitmap mBitmap;
    private Path path = new Path();
    List<Path> pathList = new ArrayList<Path>();
    Canvas mCanvas;

    float mStartX;
    float mStartY;
    float mEndX;
    float mEndY;
    float x;
    float y;
    float mX;
    float mY;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // TODO Auto-generated method stub
        super.onSizeChanged(w, h, oldw, oldh);


        this.setDrawingCacheEnabled(true);
        buildDrawingCache();
        mBitmap = Bitmap.createBitmap(500,800, Bitmap.Config.ARGB_8888);
        //canvasBitmap=BitmapFactory.decodeResource(getResources(),R.drawable.images);
        mCanvas = new Canvas(mBitmap);

    }


    private Paint paint;
    private PointF startPoint, endPoint;
    private boolean isDrawing;
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
                        pathList.add(path);
                    break;
                default:
                    break;
            }
            return true;

        }
        else {

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
                    path.addRect(mStartX, mStartY, mEndX, mEndY,Path.Direction.CW);
                    pathList.add(path);
                    path = new Path();
                    break;
                default:
                    return false;
            }
            return true;
        }
    }

    public Bitmap getBitmap()
    {
        //this.measure(100, 100);
        //this.layout(0, 0, 100, 100);
        this.setDrawingCacheEnabled(true);
        this.buildDrawingCache();
        Bitmap bmp = Bitmap.createBitmap(this.getDrawingCache());
        this.setDrawingCacheEnabled(false);


        return bmp;
    }


    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        if(mBitmap!=null) {
            for(Path path :pathList)
            canvas.drawPath(path, mPaint);
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }
  /*      if(selectedShape.equals("Rectangle Solid"))
            canvas.drawRect(mStartX, mStartY, mEndX, mEndY, mPaint);
        else if(selectedShape.equals("Rectangle Stroke"))
            canvas.drawRect(mStartX, mStartY, mEndX, mEndY, mPaint);
        else if(selectedShape.equals("Cicle Solid"))
            canvas.drawRect(mStartX, mStartY, mEndX, mEndY, mPaint);
        else if(selectedShape.equals("Cicle Stroke"))
            canvas.drawRect(mStartX, mStartY, mEndX, mEndY, mPaint);
        else if(selectedShape.equals("Triangle Solid"))
            canvas.drawRect(mStartX, mStartY, mEndX, mEndY, mPaint);
        else if(selectedShape.equals("Triangle Stroke"))
            canvas.drawRect(mStartX, mStartY, mEndX, mEndY, mPaint);
        else if(selectedShape.equals("Line"))
            canvas.drawLine(x, y, mX, mY, mPaint);
        else
            canvas.drawRect(mStartX, mStartY, mEndX, mEndY, mPaint);*/
        invalidate();
    }

    public static void SetShape (String selectedShapeString)
    {
        selectedShape = selectedShapeString;
    }

    public static void SetPaintColor(int selectedColor)
    {
        mPaint.setColor(selectedColor);
    }

    public static Paint GetPaintObject()
    {
        return mPaint;
    }

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
}
