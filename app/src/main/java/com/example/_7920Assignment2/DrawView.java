package com.example._7920Assignment2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/* Drawing triangle operations - draw , ontouch , color changes, shape changer */
public class DrawView extends View {

    private final int TOUCH_TOLERANCE = 4;
    private final List<PathData> pdList;
    private final Context context;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private final Paint mPaint;

    private List<PathPoint> pointList;
    private Path mPath;

    private int mStartX;
    private int mStartY;
    private int mEndX;
    private int mEndY;

    private boolean isFill = false;
    private boolean isLine = false;
    private boolean isTriangle = false;
    private boolean isSquare = false;
    private boolean isCircle = false;
    private boolean isCustom = false;
    private boolean isRhombus = false;
    List<Path> pList = new ArrayList<>();
    private String drawingMode;
    private String selectedShape;
    private int selectedColor = -1;


    //constructor
    public DrawView(Context cntxt) {
        super(cntxt);
        context = cntxt;
        selectedShape = Shape.Custom;
        drawingMode = Shape.FreeHandDrawingMode;
        mPath = new Path();
        pdList = new ArrayList<>();
        pointList = new ArrayList<>();
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

    // set paint color
    public void SetPaintColor(int color) {
        selectedColor = color;
    }

    // Ui refresh of screem
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.setDrawingCacheEnabled(true);
        mBitmap = Bitmap.createBitmap(525, 610, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    // Draw dunctions
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mBitmap = Bitmap.createBitmap(525, 610, Bitmap.Config.ARGB_8888);
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
        invalidate();
    }


    //show alert when shape not selected
    public void ShowAlert(String message)
    {
        int toastDurationInMilliSeconds = 400;
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);

        CountDownTimer toastCountDown;
        toastCountDown = new CountDownTimer(toastDurationInMilliSeconds, 400 ) {
            public void onTick(long millisUntilFinished) {
                toast.show();
            }
            public void onFinish() {
                toast.cancel();
            }
        };
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_dialog, null);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        toastCountDown.start();
    }

    //set shape and fill-unfill
    public void SetShape(String shapeString) {
        selectedShape = shapeString;
        isLine = selectedShape.equals(Shape.Line);
        isCustom = selectedShape.equals(Shape.Custom);
        isTriangle = selectedShape.equals(Shape.TriangleStroke) || selectedShape.equals(Shape.TriangleSolid);
        isCircle = selectedShape.equals(Shape.CircleStroke) || selectedShape.equals(Shape.CircleSolid);
        isSquare = selectedShape.equals(Shape.SquareStroke) || selectedShape.equals(Shape.SquareSolid);
        isRhombus = selectedShape.equals(Shape.RhombusStroke) || selectedShape.equals(Shape.RhombusSolid);

        if (selectedShape.equals(Shape.Line) ||
                selectedShape.equals(Shape.TriangleStroke) ||
                selectedShape.equals(Shape.CircleStroke) ||
                selectedShape.equals(Shape.SquareStroke) ||
                selectedShape.equals(Shape.Custom) ||
                selectedShape.equals(Shape.RhombusStroke))
            isFill = false;
        else if (selectedShape.equals(Shape.TriangleSolid) ||
                selectedShape.equals(Shape.CircleSolid) ||
                selectedShape.equals(Shape.SquareSolid)||
                selectedShape.equals(Shape.RhombusSolid))
            isFill = true;
    }

    // on touch event
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (selectedColor == -1) {
            ShowAlert("Please select color!");
            return true;
        }

        int x = (int) event.getX();
        int y = (int) event.getY();

        if (isCustom)
        {
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
                            mEndX = x;
                            mEndY = y;
                            mPath.lineTo(mEndX, mEndY);
                    }
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    List<PathPoint> pathPoints = new ArrayList<PathPoint>();
                    pathPoints.addAll(MyPointClass.GetPoints(mPath));
                    pdList.add(new PathData(mPath, pathPoints, selectedColor, isFill));
                    invalidate();
                    mPath = new Path();
                    break;
                default:
                    return false;
            }
            return true;
        }
        else if (!isCustom && drawingMode.equals(Shape.FreeHandDrawingMode) ) {
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
                    if (selectedShape.equals(Shape.Line))
                        pointList.add(new PathPoint(mStartX, mStartY));
                    if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                        if (isLine) {
                            mEndX = x;
                            mEndY = y;
                            mPath.lineTo(mEndX, mEndY);
                        } else
                            mPath.quadTo(mStartX, mStartY, (x + mStartX) / 2, (y + mStartY) / 2);
                    }

                    if (!isLine) {
                        mStartX = x;
                        mStartY = y;
                    }
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    if (isTriangle) {
                        mEndX = (int) event.getX();
                        mEndY = (int) event.getY();
                        List<PathPoint> cornerPoints = MyPointClass.GetPathCornersTriangle(mPath);
                        DrawTriangle(cornerPoints);
                    } else if (isRhombus) {
                        mEndX = (int) event.getX();
                        mEndY = (int) event.getY();
                        List<PathPoint> cornerPoints = MyPointClass.GetPathCornersRhombus(mPath);
                        DrawRhombus(cornerPoints);
                    }
                   /* else if (isSquare) {
                        mEndX = (int) event.getX();
                        mEndY = (int) event.getY();
                        List<PathPoint> cornerPoints = MyPointClass.GetPathCornersTriangle(mPath);
                        //cornerPoints = MyPointClass.AllignSquareLines(cornerPoints);
                        //DrawSquare(cornerPoints);
                        Path path= MyPointClass.AllignSquareLines(cornerPoints);
                        pdList.add(new PathData(path, pointList, selectedColor, isFill));
                    }*/
                    else if (isSquare)
                        DrawSquare();
                    else if (isCircle)
                        DrawCircle();
                    else if(isLine)
                        DrawLine();
                    mPath = new Path();
                    invalidate();
                    break;

                default:
                    return false;
            }
            return true;

        } else if (!isCustom &&  drawingMode.equals(Shape.AutomaticDrawingmMode)) {
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
                    float distance = MyPointClass.DistanceBetweenTwoPoints(mStartX,  mEndX,mStartY, mEndY);
                    if (isTriangle) {
                        int radius = (int) MyPointClass.CalculateRadius(mStartX, mStartY, mEndX, mEndY);
                        mPath.reset();
                        mPath.moveTo(mStartX, mStartY - radius);
                        mPath.lineTo(mStartX - radius, mStartY + radius);
                        mPath.lineTo(mStartX + radius, mStartY + radius);
                        mPath.lineTo(mStartX, mStartY - radius);
                        mPath.close();
                    }
                    else if (isCircle)
                        mPath.addCircle(mStartX, mStartY, distance/2, Path.Direction.CW);
                    else if (isSquare)
                        mPath.addRect(mStartX, mStartY, mEndX+distance/2, mEndY+distance/2, Path.Direction.CW);
                    else if (selectedShape.equals(Shape.Line)) {
                        mPath.moveTo(mStartX, mStartY);
                        mPath.lineTo(mEndX, mEndY);
                    }
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

    // Draw free hand Line
    public void DrawLine()
    {
        Path path = new Path();
        List<PathPoint> finalPoints = new ArrayList<PathPoint>();
        finalPoints.add(new PathPoint(mStartX, mStartY));
        finalPoints.add(new PathPoint(mEndX, mEndY));
        path.moveTo(mStartX, mStartY);
        path.lineTo(mEndX, mEndY);
        pdList.add(new PathData(path, pointList, selectedColor, isFill));
    }

    // Draw free hand Circle
    public void DrawCircle()
    {
        PathPoint pathMidpoint = MyPointClass.CalculatePathMidPoint(mPath);
        PathPoint circleCenterPoint = MyPointClass.CalculateCircleCenter(mStartX, pathMidpoint.getX(), mStartY, pathMidpoint.getY());
        float radius = MyPointClass.DistanceBetweenTwoPoints(mStartX, circleCenterPoint.getX(), mStartY, circleCenterPoint.getY());
        Path path = new Path();
        path.addCircle(circleCenterPoint.getX(), circleCenterPoint.getY(), radius, Path.Direction.CW);
        List<PathPoint> finalPoints = new ArrayList<>();
        finalPoints.add(new PathPoint(mStartX, mStartY));
        finalPoints.add(new PathPoint(circleCenterPoint.getX(), circleCenterPoint.getY()));
        pdList.add(new PathData(path, finalPoints, selectedColor, isFill));
    }

    // Draw free hand Triangle
    public void DrawTriangle(List<PathPoint> cornerPoints)
    {
        if (cornerPoints != null && cornerPoints.size() == 3) {
            Path pathObj = new Path();
            pathObj.moveTo(cornerPoints.get(0).getX(), cornerPoints.get(0).getY());
            for (int i = 1; i <= cornerPoints.size() - 1; i++) {
                pathObj.lineTo(cornerPoints.get(i).getX(), cornerPoints.get(i).getY());
            }
            pathObj.lineTo(cornerPoints.get(0).getX(), cornerPoints.get(0).getY());
            pdList.add(new PathData(pathObj, cornerPoints, selectedColor, isFill));
        }
    }

    // Draw free hand Rhombus
    public void DrawRhombus(List<PathPoint> cornerPoints) {
        if (cornerPoints != null && cornerPoints.size() == 4) {
            Path pathObj = new Path();
            pathObj.moveTo(cornerPoints.get(0).getX(), cornerPoints.get(0).getY());
            for (int i = 1; i <= cornerPoints.size() - 1; i++) {
                pathObj.lineTo(cornerPoints.get(i).getX(), cornerPoints.get(i).getY());
            }
            pathObj.lineTo(cornerPoints.get(0).getX(), cornerPoints.get(0).getY());
            pdList.add(new PathData(pathObj, cornerPoints, selectedColor, isFill));
        }
    }

    // Draw free hand Square
    public void DrawSquare() {

        PathPoint pathMidpoint = MyPointClass.CalculatePathMidPoint(mPath);
        PathPoint circleCenterPoint = MyPointClass.CalculateCircleCenter(mStartX, pathMidpoint.getX(), mStartY, pathMidpoint.getY());
        float radius = MyPointClass.DistanceBetweenTwoPoints(mStartX, circleCenterPoint.getX(), mStartY, circleCenterPoint.getY());
        mPath = new Path();
        Path path = new Path();
        radius = 3*radius/4;
        path.addRect((float) circleCenterPoint.getX() - radius, (float) circleCenterPoint.getY() - radius,
                (float) circleCenterPoint.getX() + radius, (float) circleCenterPoint.getY() + radius, Path.Direction.CW);
        List<PathPoint> finalPoints = new ArrayList<>();
        finalPoints.add(new PathPoint(mStartX, mStartY));
        finalPoints.add(new PathPoint(circleCenterPoint.getX(), circleCenterPoint.getY()));
        pdList.add(new PathData(path, finalPoints, selectedColor, isFill));

       /* if (cornerPoints != null && cornerPoints.size()  == 4) {
            Path pathObj = new Path();
            pathObj.moveTo(cornerPoints.get(0).getX(), cornerPoints.get(0).getY());
            for (int i = 1; i <= cornerPoints.size() - 1; i++) {
                pathObj.lineTo(cornerPoints.get(i).getX(), cornerPoints.get(i).getY());
            }
            pathObj.lineTo(cornerPoints.get(0).getX(), cornerPoints.get(0).getY());
            pdList.add(new PathData(pathObj, cornerPoints, selectedColor, isFill));
        }*/
    }



    // undo drawing steps
    public void UndoDrawing() {
        if (pdList != null && pdList.size() > 0) {
            mBitmap = Bitmap.createBitmap(525, 610, Bitmap.Config.ARGB_8888);
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
    public  String saveDrawing() throws FileNotFoundException {
        String storagePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String targetDirPath = storagePath + "/Pictures/";
        File targetDir = new File(targetDirPath);
        if (!targetDir.exists()) {
            if (false == targetDir.mkdirs()) {

                return "";
            }
        }
        if (targetDir.isDirectory()) {
            String[] children = targetDir.list();
            for (int i = 0; i < children.length; i++) {
                new File(targetDir, children[i]).delete();
            }
        }
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
        String strDate = dateFormat.format(date);
        String fileName = "Drawing"+strDate;
        String filePath = targetDirPath + "Drawing"+strDate + ".png";
        FileOutputStream fos = new FileOutputStream(filePath);
        try {
            this.getDrawingCache().compress(Bitmap.CompressFormat.PNG, 100, fos);
            this.getDrawingCache().setHasAlpha(true);
            MediaStore.Images.Media.insertImage(context.getContentResolver(), this.getDrawingCache(),
                                           "drawing", "drawing");
            Toast.makeText(context, "Drawing Saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "Error in saving", Toast.LENGTH_LONG).show();
            return "";
        } finally {
            try {
                fos.close();
                return fileName;
            } catch (IOException e) {
                Toast.makeText(context, "Error in saving", Toast.LENGTH_LONG).show();
                return "";
            }
        }
    }
}
