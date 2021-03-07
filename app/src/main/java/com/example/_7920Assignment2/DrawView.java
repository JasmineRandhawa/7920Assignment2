package com.example._7920Assignment2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
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

/* Drawing triangle operations - draw , ontouch , color changes, shape changer */
public class DrawView extends View {

    private final int TOUCH_TOLERANCE = 4;
    private List<TrianglePathTracker> pathList;
    private List<PathPoint> pointList;
    private Canvas mCanvas;
    private int mStartX;
    private int mStartY;
    private int mEndX;
    private int mEndY;
    private boolean isFill = false;
    private PathPoint nextPoint;
    private boolean isRightDirection = false;
    private boolean isTopDirection = false;
    private Context context;
    private Paint mPaint;
    private String drawingMode;
    private String selectedShape;
    private Bitmap mBitmap;
    private int selectedColor;
    private final List<PathData> pdList;
    private Point startPoint, endPoint;
    private Path mPath;
    private int mX, mY;

    //constructor
    public DrawView(Context c) {
        //add drawing view to the screen
        super(c);
        context = c;
        mStartX = 0;
        mStartY = 0;
        mEndX = 0;
        mEndY = 0;
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
        if (selectedShape.equals(Shape.TriangleStroke) || selectedShape.equals(Shape.OvalStroke))
            isFill = false;
        else if (selectedShape.equals(Shape.TriangleSolid) || selectedShape.equals(Shape.OvalSolid))
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
            if(pdList!=null && pdList.size()>0) {

                for (PathData pd :pdList) {
                    int selectecColor = pd.SelectedColor;

                    mPaint.setColor(selectecColor);
                    if(pd.Path!=null) {
                        if (pd.IsFill)
                            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                        else
                            mPaint.setStyle(Paint.Style.STROKE);
                        List<PathPoint> finalPoints = new ArrayList<PathPoint>();
                        finalPoints.addAll(pd.pathPointList);
                        mPaint.setColor(selectecColor);
                        canvas.drawPath(pd.Path, mPaint);
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
        if (drawingMode.equals(Shape.FreeHandDrawingMode) &&
                (selectedShape.equals(Shape.TriangleStroke) || selectedShape.equals(Shape.TriangleSolid))) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startPoint = new Point((int) event.getX(), (int) event.getY());
                    endPoint = new Point();
                    mPath.moveTo(startPoint.x, startPoint.y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:

                    float dx = Math.abs(startPoint.x - endPoint.x);
                    float dy = Math.abs(startPoint.y - endPoint.y);
                    mPaint.setColor(selectedColor);
                    if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE)
                    {
                        endPoint.x = (int) event.getX();
                    endPoint.y = (int) event.getY();
                    mPath.lineTo(endPoint.x, endPoint.y);
                    }

                    pathList.add(new TrianglePathTracker(startPoint.x, startPoint.y, endPoint.x, endPoint.y));
                    startPoint = new Point((int)event.getX(),(int) event.getY());
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    endPoint.x = (int) event.getX();
                    endPoint.y = (int) event.getY();
                    //mPath.lineTo(endPoint.x,endPoint.y);
                    pathList.add(new TrianglePathTracker(startPoint.x, startPoint.y, endPoint.x, endPoint.y));
                    UpdateList(mPath);

                    invalidate();
                    mPath = new Path();
                    break;
                default:
                    return false;
            }
            return true;

        } else if (drawingMode.equals(Shape.AutomaticDrawingmMode) &&
                (selectedShape.equals(Shape.TriangleStroke) || selectedShape.equals(Shape.TriangleSolid))) {
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
                    int radius = (int) calculateRadius(mStartX, mStartY, mEndX, mEndY);
                    mPath.reset();
                    mPath.moveTo(mStartX, mStartY - radius);
                    mPath.lineTo(mStartX - radius, mStartY + radius); // Bottom left
                    mPath.lineTo(mStartX + radius, mStartY + radius); // Bottom right
                    mPath.lineTo(mStartX, mStartY - radius); // Back to Top
                    mPath.close();
                    List<PathPoint> finalPoints = new ArrayList<PathPoint>();
                    finalPoints.add(new PathPoint(mStartX, mStartY));
                    finalPoints.add(new PathPoint(mEndX, mEndY));
                    pdList.add(new PathData(mPath,null, finalPoints, selectedColor, isFill));
                    invalidate();
                    break;
                default:
                    return false;
            }
            return true;
        } else if (drawingMode.equals(Shape.FreeHandDrawingMode) &&
                ( selectedShape.equals(Shape.OvalSolid) || selectedShape.equals(Shape.OvalStroke))) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    circle_touch_start(x, y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    circle_touch_move(x, y);
                    break;
                case MotionEvent.ACTION_UP:
                    circle_touch_up();
                    invalidate();
                    break;
                default:
                    return false;
            }
            return true;

        } else if (drawingMode.equals(Shape.AutomaticDrawingmMode) &&
                (selectedShape.equals(Shape.OvalSolid) || selectedShape.equals(Shape.OvalStroke))) {
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
                    mPath.addOval(mStartX, mStartY, mEndX, mEndY, Path.Direction.CW);
                    List<PathPoint> finalPoints = new ArrayList<PathPoint>();
                    finalPoints.add(new PathPoint(mStartX, mStartY));
                    finalPoints.add(new PathPoint(mEndX, mEndY));
                    pdList.add(new PathData(mPath, null,finalPoints, selectedColor, isFill));
                    invalidate();
                    break;
                default:
                    return false;
            }
            return true;
        }
        return true;
    }

    private void circle_touch_start(int x, int y) {
        mStartX = x;
        mStartY = y;
        mX = x;
        mY = y;
        mPath.moveTo(x, y);
        invalidate();
    }

    private void circle_touch_move(int x, int y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
            mEndY = x;
            mEndY = y;
            invalidate();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void circle_touch_up() {

        PathPoint pathMidpoint = calculatePathMidPoint(mPath);
        PathPoint circleCenterPoint = calculateCircleCenter(mStartX, pathMidpoint.getX(), mStartY, pathMidpoint.getY());
        float distanceBetweenTwoPoints = distanceBetweenTwoPoints(mStartX, circleCenterPoint.getX(), mStartY, circleCenterPoint.getY());
        int radius = (int) distanceBetweenTwoPoints;
        Path path = new Path();
        path.addCircle(circleCenterPoint.getX(), circleCenterPoint.getY(), radius, Path.Direction.CW);
        List<PathPoint> finalPoints = new ArrayList<PathPoint>();
        finalPoints.add(new PathPoint(mStartX, mStartY));
        finalPoints.add(new PathPoint(circleCenterPoint.getX(), circleCenterPoint.getY()));
        pdList.add(new PathData(path,mPath, finalPoints, selectedColor, isFill));
        mPath= new Path();
    }

    public float distanceBetweenTwoPoints(float x1, float x2, float y1, float y2) {
        return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    public PathPoint calculateCircleCenter(float x1, float x2, float y1, float y2) {
        return new PathPoint((float) (x1 + x2) / 2, (float) (y1 + y2) / 2);
    }

    public PathPoint calculatePathMidPoint(Path path) {
        PathMeasure pm = new PathMeasure(path, true);
        //coordinates will be here
        float[] aCoordinates = {0f, 0f};

        //get coordinates of the middle point
        pm.getPosTan(pm.getLength() * 0.5f, aCoordinates, null);
        return new PathPoint(aCoordinates[0], aCoordinates[1]);
    }

    private List<PathPoint> getPoints(Path path) {
        List<PathPoint> pointList = new ArrayList<>();
        PathMeasure pm = new PathMeasure(path, false);
        float length = pm.getLength();
        float distance = 0f;
        float speed = length / 70;
        int counter = 0;
        float[] aCoordinates = new float[2];

        while ((distance < length) && (counter < 70)) {
            // get point from the path
            pm.getPosTan(distance, aCoordinates, null);
            pointList.add(new PathPoint(aCoordinates[0],
                    aCoordinates[1]));
            counter++;
            distance = distance + speed;
        }

        return pointList;
    }

    // update Path data List
    public void UpdateList(Path path) {
        List<PathPoint> finalPoints = new ArrayList<PathPoint>();
        pointList.addAll(getPoints(path));
        if (pathList != null && pathList.size() > 0) {
            pointList = new ArrayList<PathPoint>();
            /*for (int i = 0; i <= pathList.size() - 1; i++) {
                if (i <= pathList.size() - 1)
                    pointList.add(new PathPoint(pathList.get(i).StartX, pathList.get(i).StartY));
                if (i == pathList.size() - 1)
                    pointList.add(new PathPoint(pathList.get(i).EndX, pathList.get(i).EndY));
            }*/

            pointList.addAll(getPoints(path));
            if (pointList != null && pointList.size() > 0) {
                pointList = RemoveDuplicates(pointList);

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
                List<PathPoint> nextList = GetNextList(peakIndex, pointList);
                if (nextList != null && nextList.size() > 0) {
                    int nextPeakIndex = GetThirdCorner(nextList, isRightDirection);
                    finalPoints.add(nextPoint);
                }
            }
        }
        if (finalPoints != null && finalPoints.size() > 0) {
            Path p = new Path();
            p.moveTo(finalPoints.get(0).x, finalPoints.get(0).y);
            for (int i = 1; i <= finalPoints.size() - 1; i++) {
                p.lineTo(finalPoints.get(i).x, finalPoints.get(i).y);
            }
            p.lineTo(finalPoints.get(0).x, finalPoints.get(0).y);
            pdList.add(new PathData(p,null, finalPoints, selectedColor, isFill));
            pathList = new ArrayList<>();
        }
    }

    //get trisangle radii
    private float calculateRadius(float x1, float y1, float x2, float y2) {

        return ((float) Math.sqrt(
                Math.pow(x1 - x2, 2) +
                        Math.pow(y1 - y2, 2)) / 2
        );
    }

    //removed duplicate points
    public List<PathPoint> RemoveDuplicates(List<PathPoint> points) {
        List<PathPoint> finalPoints = new ArrayList<PathPoint>();
        for (PathPoint p : points) {
            if (!ContainsPoints(finalPoints, p))
                finalPoints.add(new PathPoint( p.x,  p.y));
        }
        return finalPoints;
    }

    //check if a point exists in a list
    public boolean ContainsPoints(List<PathPoint> finalPoints, PathPoint p) {
        boolean isInList = false;
        for (PathPoint point : finalPoints) {
            if ( point.getX() ==  p.getX() &&  point.getY() == p.getY())
                isInList = true;
        }
        return isInList;
    }

    //get point list after second point of triangle
    private List<PathPoint> GetNextList(int peakIndex, List<PathPoint> pList) {
        List<PathPoint> nextList = new ArrayList<PathPoint>();
        for (int i = peakIndex + 1; i <= pList.size() - 1; i++) {
            nextList.add(new PathPoint(pList.get(i).x,  pList.get(i).y));
        }
        return nextList;
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
            if (mBitmap != null) {
                if(pdList!=null && pdList.size()>0) {
                    pdList.remove(pdList.size() - 1);
                    for (PathData pd :pdList) {
                        List<PathPoint> finalPoints = new ArrayList<PathPoint>();
                        finalPoints.addAll(pd.pathPointList);
                        int selectecColor = pd.SelectedColor;
                        if (pd.IsFill)
                            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                        else
                            mPaint.setStyle(Paint.Style.STROKE);
                        mPaint.setColor(selectecColor);
                        mCanvas.drawPath(pd.Path, mPaint);
                    }
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
