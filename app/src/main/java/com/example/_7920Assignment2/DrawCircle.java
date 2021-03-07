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

/* Drawing view operations - draw , ontouch , color changes, shape changer */
class DrawCircle extends View {

    private final int TOUCH_TOLERANCE = 4;
    Canvas mCanvas;
    int mStartX;
    int mStartY;
    int mEndX;
    int mEndY;
    boolean isFill = false;
    private Context context;
    private Paint mPaint;
    private String drawingMode ;
    private String selectedShape ;
    private List<PathData> pdList ;
    private Bitmap mBitmap;
    private int selectedColor;
    private Path mPath;
    private int mX, mY;

    //constructor
    public DrawCircle(Context c) {
        //add drawing view to the screen
        super(c);
        context = c;
        mStartX = 0;
        mStartY = 0;
        mEndX = 0;
        mEndY = 0;
        mPath = new Path();
        selectedShape = "";
        drawingMode = "";
        selectedColor = Color.MAGENTA;
        pdList = new ArrayList<>();
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
        if (selectedShape.equals(Shape.OvalStroke))
            isFill = false;
        else if (selectedShape.equals(Shape.OvalSolid))
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
                    touch_start(x, y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
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
                    //int radius = (int)calculateRadius(mStartX, mStartY, mEndX, mEndY);
                   // PathPoint pathMidpoint = calculatePathMidPoint(mPath);
                    //PathPoint circleCenterPoint = calculateCircleCenter(mStartX,pathMidpoint.getX(),mStartY,pathMidpoint.getY());;
                   // mPath.addCircle(circleCenterPoint.getX(), circleCenterPoint.getY(),radius, Path.Direction.CW);
                    mPath.addOval(mStartX, mStartY, mEndX, mEndY, Path.Direction.CW);
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


    //get trisangle radii
    private float calculateRadius(float x1, float y1, float x2, float y2) {

        return ((float) Math.sqrt(
                Math.pow(x1 - x2, 2) +
                        Math.pow(y1 - y2, 2))/2
        );
    }

    private PathPoint center(float x1, float y1, float x2, float y2)
    {

        return new PathPoint((float)(x1 + x2) / 2
                , (float)(y1 + y2) / 2);
    }


    private void touch_start(int x, int y) {
        mStartX = x;
        mStartY = y;
        mX = x;
        mY = y;
        mPath.moveTo(x, y);

    }

    private void touch_move(int x, int y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
            mEndY = x;
            mEndY = y;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void touch_up() {
        PathPoint pathMidpoint = calculatePathMidPoint(mPath);
        mPath = new Path();
        PathPoint circleCenterPoint = calculateCircleCenter(mStartX,pathMidpoint.getX(),mStartY,pathMidpoint.getY());
        float distanceBetweenTwoPoints = distanceBetweenTwoPoints(mStartX,circleCenterPoint.getX(),mStartY,circleCenterPoint.getY());
        int radius = (int)distanceBetweenTwoPoints;
        Path mPath = new Path();
        mPath.addCircle(circleCenterPoint.getX(), circleCenterPoint.getY(),radius,Path.Direction.CW);
        List<PathPoint> finalPoints = new ArrayList<PathPoint>();
        finalPoints.add(new PathPoint(mStartX,mStartY));
        finalPoints.add(new PathPoint(circleCenterPoint.getX(),circleCenterPoint.getY()));
        pdList.add(new PathData(mPath,finalPoints, selectedColor,isFill));
    }
    public float  distanceBetweenTwoPoints(float x1, float x2, float y1, float y2) {
        return (float)Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    public PathPoint  calculateCircleCenter (float x1, float x2, float y1, float y2) {
        return new PathPoint((float)(x1+x2)/2,(float)(y1+y2)/2);
    }

    public PathPoint calculatePathMidPoint(Path path) {
        PathMeasure pm = new PathMeasure(path, true);
        //coordinates will be here
        float aCoordinates[] = {0f, 0f};

        //get coordinates of the middle point
        pm.getPosTan(pm.getLength() * 0.5f, aCoordinates, null);
        return new PathPoint(aCoordinates[0],aCoordinates[1]);
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
