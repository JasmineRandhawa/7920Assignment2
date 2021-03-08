package com.example._7920Assignment2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/* Drawing view operations - draw , ontouch , color changes, shape changer */
class DrawingLine extends View {

    private final int TOUCH_TOLERANCE = 4;
    List<LinePathTracker> pathList;
    List<PathPoint> pointList;
    Canvas mCanvas;
    private Point startPoint, endPoint;
    boolean isFill = false;
    private Context context;
    private Paint mPaint;
    private String drawingMode;
    private String selectedShape;
    private Bitmap mBitmap;
    private int selectedColor;
    private Path mPath;
    private int mX, mY;

    //constructor
    public DrawingLine(Context c) {
        //add drawing view to the screen
        super(c);
        context = c;
        mPath = new Path();
        selectedColor = Color.MAGENTA;
        pathList = new ArrayList<LinePathTracker>();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
    }

    //getter setter to change shape and color fields
    public Paint GetPaintObject() {
        return mPaint;
    }

    //set drawwing mode
    public void SetDrawingMode(String drawingModeString) {
        drawingMode = drawingModeString;
    }

    //set shape and fill-unfill
    public void SetShape(String shapeString) {
        selectedShape = shapeString;
        if (selectedShape.equals(Shape.CircleStroke))
            isFill = false;
        else if (selectedShape.equals(Shape.CircleSolid))
            isFill = true;
    }

    // set paint color
    public void SetPaintColor(int color) {
        selectedColor = color;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.setDrawingCacheEnabled(true);
        mBitmap = Bitmap.createBitmap(500, 800, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBitmap != null) {
            for (LinePathTracker pathTracker : pathList) {
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


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (drawingMode.equals(Shape.FreeHandDrawingMode)) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    break;
                default:
                    return false;
            }
            return true;
        } else if (drawingMode.equals(Shape.AutomaticDrawingmMode)) {
            mPath = new Path();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startPoint = new Point((int)event.getX(),(int) event.getY());
                    endPoint = new Point();
                    mPath = new Path();
                    break;
                case MotionEvent.ACTION_MOVE:
                    endPoint.x = (int) event.getX();
                    endPoint.y = (int)event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    endPoint.x = (int) event.getX();
                    endPoint.y = (int)event.getY();
                    mPath.moveTo(startPoint.x, startPoint.y);
                    mPath.lineTo(endPoint.x, endPoint.y);
                    pathList.add(new LinePathTracker(mPath, startPoint.x, startPoint.y, endPoint.x, endPoint.y,
                            selectedShape, drawingMode,selectedColor, false));
                    break;
                default:
                    return false;
            }
            return true;
        }
        return true;
    }


    private void touch_start(int x, int y) {
        mX = x;
        mY = y;
        mPath.moveTo(x, y);
        pointList.add(new PathPoint(x, y));

    }

    private void touch_move(int x, int y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        pointList.add(new PathPoint(mX, mY));
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
        pointList.add(new PathPoint(x, y));
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void touch_up() {
        List<PathPoint> finalPoints = new ArrayList<PathPoint>();
        if (pointList != null && pointList.size() > 0) {
            mPath = new Path();
            pointList = RemoveDuplicates(pointList);
            // find direction of triangle
            PathPoint startPoint = new PathPoint(pointList.get(0).x, pointList.get(0).y);
            PathPoint endPoint = new PathPoint(pointList.get(pointList.size()-1).x, pointList.get(pointList.size()-1).y);
            mPath.moveTo(startPoint.x, startPoint.y);
            mPath.lineTo(endPoint.x, endPoint.y);
        }
        pathList.add(new LinePathTracker(mPath, startPoint.x, startPoint.y, endPoint.x, endPoint.y,
                selectedShape, drawingMode, selectedColor, isFill));
        mPath = new Path();
    }

    //removed duplicate points
    public List<PathPoint> RemoveDuplicates(List<PathPoint> points) {
        List<PathPoint> finalPoints = new ArrayList<PathPoint>();
        for (PathPoint p : points) {
            if (!ContainsPoints(finalPoints, p))
                finalPoints.add(new PathPoint((int) p.x, (int) p.y));
        }
        return finalPoints;
    }

    //check if a point exists in a list
    public boolean ContainsPoints(List<PathPoint> finalPoints, PathPoint p) {
        boolean isInList = false;
        for (PathPoint point : finalPoints) {
            if ((int) point.getX() == (int) p.getX() && (int) point.getY() == (int) p.getY())
                isInList = true;
        }
        return isInList;
    }


    // undo drawing steps
    public void UndoDrawing() {
        if (pathList != null && pathList.size() > 0) {
            mBitmap = Bitmap.createBitmap(500, 800, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
            pathList.remove(pathList.size() - 1);
            if (mBitmap != null) {
                for (LinePathTracker pathTracker : pathList) {
                    if (pathTracker.getIsFill())
                        mPaint.setStyle(Paint.Style.FILL);
                    else
                        mPaint.setStyle(Paint.Style.STROKE);
                    mPaint.setColor(pathTracker.getSelectedColor());
                    mCanvas.drawPath(pathTracker.getPathOfObject(), mPaint);
                }
                mCanvas.drawBitmap(mBitmap, 0, 0, null);
            }
        }
    }

    //save drawing
    public void saveDrawing() {
        String storagePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String targetDirPath = storagePath + "/Pictures/";
        File targetDir = new File(targetDirPath);
        if (!targetDir.exists()) {
            if (false == targetDir.mkdirs()) {

                return;
            }
        }
        if (targetDir.isDirectory()) {
            String[] children = targetDir.list();
            for (int i = 0; i < children.length; i++) {
                new File(targetDir, children[i]).delete();
            }
        }
        File file = new File(targetDirPath + UUID.randomUUID().toString() + ".jpg");
        String filePath = targetDirPath + UUID.randomUUID().toString() + ".jpg";
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            this.getDrawingCache().compress(Bitmap.CompressFormat.JPEG, 100, fos);
            String imgSaved = MediaStore.Images.Media.insertImage(
                    context.getContentResolver(), this.getDrawingCache(),
                    null, "drawing");
            Toast.makeText(context, "Drawing Saved", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            String s = e.getMessage();
            Toast.makeText(context, "Error in saving", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
