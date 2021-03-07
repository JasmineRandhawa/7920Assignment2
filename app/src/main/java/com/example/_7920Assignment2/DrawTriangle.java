package com.example._7920Assignment2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/* Drawing triangle operations - draw , ontouch , color changes, shape changer */
public class DrawTriangle extends View {

    private List<TrianglePathTracker> pathList;
    private  List<PathPoint> pointList;
    private Canvas mCanvas;
    private int mStartX;
    private  int mStartY;
    private int mEndX;
    private int mEndY;
    private boolean isFill = false;
    private PathPoint nextPoint;
    boolean isRightDirection = false;
    boolean isTopDirection = false;
    private Context context;
    private Paint mPaint;
    private String drawingMode ;
    private String selectedShape ;
    private Bitmap mBitmap;
    private int selectedColor;
    private List<PathData> pdList ;
    private Point startPoint, endPoint;

    //constructor
    public DrawTriangle(Context c) {
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
        if (selectedShape.equals(Shape.TriangleStroke))
            isFill = false;
        else if (selectedShape.equals(Shape.TriangleSolid))
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
                        List<PathPoint> finalPoints = new ArrayList<PathPoint>();
                        finalPoints.addAll(pd.pathPointList);
                        int selectecColor = pd.SelectedColor;
                        if (pd.IsFill)
                            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                        else
                            mPaint.setStyle(Paint.Style.STROKE);
                        mPaint.setColor(selectecColor);
                        canvas.drawPath(pd.Path, mPaint);
                }
            }
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

        if(drawingMode.equals("")) {
            ShowAlert("Please select Drawing Mode ! ");
            return true;
        }
        if(selectedShape.equals("")) {
            ShowAlert("Please select Shape ! ");
            return true;
        }
        if (drawingMode.equals(Shape.FreeHandDrawingMode)) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startPoint = new Point((int)event.getX(),(int) event.getY());
                    endPoint = new Point();
                    break;
                case MotionEvent.ACTION_MOVE:
                    endPoint.x = (int) event.getX();
                    endPoint.y = (int)event.getY();
                    pathList.add(new TrianglePathTracker(startPoint.x, startPoint.y, endPoint.x, endPoint.y));
                    startPoint = new Point((int)event.getX(),(int) event.getY());
                    break;
                case MotionEvent.ACTION_UP:
                    endPoint.x = (int) event.getX();
                    endPoint.y = (int)event.getY();
                    pathList.add(new TrianglePathTracker(startPoint.x, startPoint.y, endPoint.x, endPoint.y));
                    UpdateList();
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
                    int radius = (int) calculateRadius(mStartX, mStartY, mEndX, mEndY);
                    mPath.reset();
                    mPath.moveTo(mStartX, mStartY - radius);
                    mPath.lineTo(mStartX - radius, mStartY + radius); // Bottom left
                    mPath.lineTo(mStartX + radius, mStartY + radius); // Bottom right
                    mPath.lineTo(mStartX, mStartY - radius); // Back to Top
                    mPath.close();
                    List<PathPoint> finalPoints = new ArrayList<PathPoint>();
                    finalPoints.add(new PathPoint(mStartX,mStartY));
                    finalPoints.add(new PathPoint(mEndX,mEndY));
                    pdList.add(new PathData(mPath,finalPoints, selectedColor,isFill));
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
    public void UpdateList()
    {
        List<PathPoint> finalPoints = new ArrayList<PathPoint>();
        if (pathList != null && pathList.size() > 0) {
            pointList = new ArrayList<PathPoint>();
            for (int i = 0; i <= pathList.size() - 1; i++) {
                if (i <= pathList.size() - 1)
                    pointList.add(new PathPoint(pathList.get(i).StartX, pathList.get(i).StartY));
                if (i == pathList.size() - 1)
                    pointList.add(new PathPoint(pathList.get(i).EndX, pathList.get(i).EndY));
            }


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
            pdList.add(new PathData(p,finalPoints, selectedColor,isFill));
            pathList = new ArrayList<TrianglePathTracker>();
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
