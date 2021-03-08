package com.example._7920Assignment2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/* Drawing triangle operations - draw , ontouch , color changes, shape changer */
public class DrawView extends View {

    private final int TOUCH_TOLERANCE = 4;
    private final List<PathData> pdList;
    private final Context context;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private final Paint mPaint;

    private List<PathTracker> pathList;
    private List<PathPoint> pointList;
    private Path mPath;

    private int mStartX;
    private int mStartY;
    private int mEndX;
    private int mEndY;
    private PathPoint nextPoint;

    private boolean isFill = false;
    private boolean isRightDirection = false;
    private boolean isTopDirection = false;
    private String drawingMode;
    private String selectedShape;
    private int selectedColor;


    //constructor
    public DrawView(Context cntxt) {
        super(cntxt);
        context = cntxt;
        selectedShape = "";
        drawingMode = "";
        selectedColor = Color.MAGENTA;
        mPath = new Path();
        pdList = new ArrayList<>();
        pointList = new ArrayList<>();
        pathList = new ArrayList<>();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
    }

    //set drawwing mode
    public void SetDrawingMode(String drawingModeString) {
        drawingMode = drawingModeString;
    }

    //set shape and fill-unfill
    public void SetShape(String shapeString) {
        selectedShape = shapeString;
        if (selectedShape.equals(Shape.TriangleStroke) || selectedShape.equals(Shape.CircleStroke))
            isFill = false;
        else if (selectedShape.equals(Shape.TriangleSolid) || selectedShape.equals(Shape.CircleSolid))
            isFill = true;
    }

    // set paint color
    public void SetPaintColor(int color) {
        selectedColor = color;
    }

    // Ui refresh of screem
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.setDrawingCacheEnabled(true);
        mBitmap = Bitmap.createBitmap(500, 800, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    // Draw dunctions
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBitmap != null) {
            if(pdList!=null && pdList.size()>0) {

                for (PathData pd :pdList) {
                    int selectecColor = pd.getSelectedColor();

                    mPaint.setColor(selectecColor);
                    if(pd.getPath()!=null) {
                        if (pd.getIsFill())
                            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                        else
                            mPaint.setStyle(Paint.Style.STROKE);
                        List<PathPoint> finalPoints = new ArrayList<PathPoint>();
                        finalPoints.addAll(pd.getPathPointList());
                        mPaint.setColor(selectecColor);
                        canvas.drawPath(pd.getPath(), mPaint);
                    }
                }
            }
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(selectedColor);
            canvas.drawPath(mPath, mPaint);
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }
    }


    //show alert when shape not selected
    public void ShowAlert(String message)
    {
       Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }


    // on touch event
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (drawingMode.equals("")) {
            ShowAlert("Please select Drawing Mode ! ");
            return true;
        }
        if (selectedShape.equals("")) {
            ShowAlert("Please select Shape ! ");
            return true;
        }
        if (drawingMode.equals(Shape.FreeHandDrawingMode)) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mStartX = (int) event.getX();
                    mStartY = (int) event.getY();
                    mPath.moveTo(mStartX, mStartY);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float dx = Math.abs(mStartX - mEndX);
                    float dy = Math.abs(mStartY - mEndY);
                    if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                        if (selectedShape.equals(Shape.CircleSolid) || selectedShape.equals(Shape.CircleStroke))
                            mPath.quadTo(mStartX, mStartY, (x + mStartX) / 2, (y + mStartY) / 2);
                        else if (selectedShape.equals(Shape.TriangleSolid) || selectedShape.equals(Shape.TriangleStroke)) {
                            mEndX = x;
                            mEndY = y;
                            mPath.lineTo(mEndX, mEndY);
                            pathList.add(new PathTracker(mStartX, mStartY, mEndX, mEndY));
                        }
                    }
                    mStartX = x;
                    mStartY = y;
                    invalidate();
                    break;

                case MotionEvent.ACTION_UP:
                    if (selectedShape.equals(Shape.TriangleSolid) || selectedShape.equals(Shape.TriangleStroke)) {
                        mEndX = (int) event.getX();
                        mEndY = (int) event.getY();
                        pathList.add(new PathTracker(mStartX, mStartY, mEndX, mEndY));
                        UpdateList(mPath);
                    }
                    else if ((selectedShape.equals(Shape.CircleSolid) || selectedShape.equals(Shape.CircleStroke))) {
                        PathPoint pathMidpoint = Utility.CalculatePathMidPoint(mPath);
                        PathPoint circleCenterPoint = Utility.CalculateCircleCenter(mStartX, pathMidpoint.getX(), mStartY, pathMidpoint.getY());
                        float radius = Utility.DistanceBetweenTwoPoints(mStartX, circleCenterPoint.getX(), mStartY, circleCenterPoint.getY());
                        Path path = new Path();
                        path.addCircle(circleCenterPoint.getX(), circleCenterPoint.getY(), radius, Path.Direction.CW);
                        List<PathPoint> finalPoints = new ArrayList<>();
                        finalPoints.add(new PathPoint(mStartX, mStartY));
                        finalPoints.add(new PathPoint(circleCenterPoint.getX(), circleCenterPoint.getY()));
                        pdList.add(new PathData(path, finalPoints, selectedColor, isFill));
                    }
                    mPath = new Path();
                    invalidate();
                    break;

                default:
                    return false;
            }
            return true;

        } else if (drawingMode.equals(Shape.AutomaticDrawingmMode)) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mStartX = (int) event.getX();
                    mStartY = (int) event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    mEndX = (int) event.getX();
                    mEndY = (int) event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    mEndX = (int) event.getX();
                    mEndY = (int) event.getY();
                    Path mPath = new Path();
                    if ((selectedShape.equals(Shape.TriangleSolid) || selectedShape.equals(Shape.TriangleStroke))) {
                        int radius = (int) Utility.CalculateRadius(mStartX, mStartY, mEndX, mEndY);
                        mPath.reset();
                        mPath.moveTo(mStartX, mStartY - radius);
                        mPath.lineTo(mStartX - radius, mStartY + radius);
                        mPath.lineTo(mStartX + radius, mStartY + radius);
                        mPath.lineTo(mStartX, mStartY - radius);
                        mPath.close();
                    }
                    else if ((selectedShape.equals(Shape.CircleSolid) || selectedShape.equals(Shape.CircleStroke)))
                        mPath.addOval(mStartX, mStartY, mEndX, mEndY, Path.Direction.CW);
                    List<PathPoint> finalPoints = new ArrayList<PathPoint>();
                    finalPoints.add(new PathPoint(mStartX, mStartY));
                    finalPoints.add(new PathPoint(mEndX, mEndY));
                    pdList.add(new PathData(mPath, finalPoints, selectedColor, isFill));
                    invalidate();
                    break;
                default:
                    return false;
            }
            return true;
        }
        return true;
    }


    // update Path data List
    public void UpdateList(Path path) {
        List<PathPoint> finalPoints = new ArrayList<PathPoint>();
        pointList.addAll(Utility.GetPoints(path));
        if (pathList != null && pathList.size() > 0) {
            pointList = new ArrayList<PathPoint>();
            pointList.addAll(Utility.GetPoints(path));
            if (pointList != null && pointList.size() > 0) {
                pointList = Utility.RemoveDuplicates(pointList);

                // find direction of triangle
                PathPoint startPoint = new PathPoint(pointList.get(0).x, pointList.get(0).y);
                PathPoint secondPoint = new PathPoint(pointList.get((int)pointList.size() /2).x, pointList.get((int)pointList.size() /2).y);
                if (secondPoint.getX() > startPoint.getX())
                    isRightDirection = true;
                else
                    isRightDirection = false;

                if (secondPoint.getY() < startPoint.getY())
                    isTopDirection = true;
                else
                    isTopDirection = false;

                //add first corner of triangle
                finalPoints.add(startPoint);

                //find second corner of triangle
                int peakIndex = GetSecondCorner(pointList, isTopDirection, isRightDirection);
                finalPoints.add(nextPoint);

                //find third corner of triangle
                List<PathPoint> nextList = Utility.GetNextList(peakIndex, pointList);
                if (nextList != null && nextList.size() > 0) {
                    int nextPeakIndex = GetThirdCorner(nextList, isRightDirection);
                    finalPoints.add(nextPoint);
                }
            }
        }
        if (finalPoints != null && finalPoints.size() > 0) {
            Path pathObj = new Path();
            pathObj.moveTo(finalPoints.get(0).x, finalPoints.get(0).y);
            for (int i = 1; i <= finalPoints.size() - 1; i++) {
                pathObj.lineTo(finalPoints.get(i).x, finalPoints.get(i).y);
            }
            pathObj.lineTo(finalPoints.get(0).x, finalPoints.get(0).y);
            pdList.add(new PathData(pathObj, finalPoints, selectedColor, isFill));
            pathList = new ArrayList<>();
        }
    }

    //get second corner of triangle
    public int GetSecondCorner(List<PathPoint> pointList,boolean isTopDirection, boolean isRightDirection) {
        List<PathPoint> finalPoints = new ArrayList<PathPoint>();
        for (int i = 1; i <= pointList.size() - 1; i++) {
            if (isTopDirection && isRightDirection) {
                if (pointList.get(i).y > pointList.get(i - 1).y &&  pointList.get(i).x > pointList.get(i - 1).x) {
                    nextPoint = new PathPoint(pointList.get(i).x, (pointList.get(i).y));
                    return i;
                }
            } else if (isTopDirection && !isRightDirection) {
                if (pointList.get(i).y > pointList.get(i - 1).y && pointList.get(i).x < pointList.get(i - 1).x) {
                    nextPoint = new PathPoint(pointList.get(i).x, (pointList.get(i).y));
                    return i;
                }
            }
            else if (!isTopDirection && isRightDirection) {
                if (pointList.get(i).y < pointList.get(i - 1).y && pointList.get(i).x > pointList.get(i - 1).x) {
                    nextPoint = new PathPoint(pointList.get(i).x, (pointList.get(i).y));
                    return i;
                }
            }
            else if (!isTopDirection && !isRightDirection) {
                if (pointList.get(i).y < pointList.get(i - 1).y && pointList.get(i).x < pointList.get(i - 1).x) {
                    nextPoint = new PathPoint(pointList.get(i).x, (pointList.get(i).y));
                    return i;
                }
            }
        }
        nextPoint = new PathPoint(pointList.get(pointList.size() - 1).x, pointList.get(pointList.size() - 1).y);
        return pointList.size() - 1;
    }

    //get second corner of triangle
    public int GetThirdCorner(List<PathPoint> pointList, boolean isRightDirection) {
        for (int i = 1; i <= pointList.size() - 1; i++) {
            if (isRightDirection) {
                if (pointList.get(i).x < pointList.get(i - 1).x) {
                    nextPoint = new PathPoint(pointList.get(i).x, (pointList.get(i).y));
                    return i;
                }
            } else {
                if (pointList.get(i).x > pointList.get(i - 1).x) {
                    nextPoint = new PathPoint(pointList.get(i).x, (pointList.get(i).y));
                    return i;
                }
            }
        }
        nextPoint = new PathPoint(pointList.get(pointList.size() - 1).x, pointList.get(pointList.size() - 1).y);
        return pointList.size() - 1;
    }

    // undo drawing steps
    public void UndoDrawing() {
        if (pdList != null && pdList.size() > 0) {
            mBitmap = Bitmap.createBitmap(500, 800, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
            pdList.remove(pdList.size() - 1);
            if (mBitmap != null) {
                    for (PathData pd :pdList) {
                        List<PathPoint> finalPoints = new ArrayList<PathPoint>();
                        finalPoints.addAll(pd.getPathPointList());
                        int selectecColor = pd.getSelectedColor();
                        if (pd.getIsFill())
                            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                        else
                            mPaint.setStyle(Paint.Style.STROKE);
                        mPaint.setColor(selectecColor);
                        mCanvas.drawPath(pd.getPath(), mPaint);
                    }
                mCanvas.drawBitmap(mBitmap, 0, 0, null);
            }
            invalidate();
        }
    }

    //save drawing
    public void saveDrawing() throws FileNotFoundException {
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
        String filePath = targetDirPath + UUID.randomUUID().toString() + ".jpg";
        FileOutputStream fos = new FileOutputStream(filePath);
        try {
            this.getDrawingCache().compress(Bitmap.CompressFormat.JPEG, 100, fos);
            MediaStore.Images.Media.insertImage(context.getContentResolver(), this.getDrawingCache(),
                                           null, "drawing");
            Toast.makeText(context, "Drawing Saved", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context, "Error in saving", Toast.LENGTH_LONG).show();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                Toast.makeText(context, "Error in saving", Toast.LENGTH_LONG).show();
            }
        }
    }
}
