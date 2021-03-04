package com.example._7920Assignment2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/* Drawing view operations - draw , ontouch , color changes, shape changer */
public class DrawingView extends View {

    private Context context;
    private static Paint mPaint;
    private static String selectedShape;
    private Bitmap mBitmap;
    private static int selectedColor;
    private Path path;
    List<PathTracker> pathList;
    Canvas mCanvas;
    float mStartX;
    float mStartY;
    float mEndX;
    float mEndY;
    private Path mPath ;
    private PointF startPoint, endPoint;

    //constructor
    public DrawingView(Context c) {
        //add drawing view to the screen
        super(c);
        context = c;
        selectedShape ="";
        selectedColor= Color.MAGENTA;
        pathList = new ArrayList<PathTracker>();
        startPoint = new PointF();
        endPoint = new PointF();
        mStartX = 0f;
        mStartY = 0f;
        mEndX = 0f;
        mEndY = 0f;
        path=new Path();
        mPath=new Path();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
    }

    //getter setter to change shape and color fields
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
        mBitmap = Bitmap.createBitmap(500, 800, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
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


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //handle all line case other than line
        if(selectedShape.equals("")) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    pathList.add(new PathTracker(mPath, mStartX, mStartY, mEndX, mEndY,
                            selectedShape, selectedColor, false));
                    mPath = new Path();
                    break;
            }

            return true;
        }
        else if(selectedShape.equals(Shape.Line)) {
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
        }
        //handle all other shapes other than line
        else  {
            boolean isFill = false;
            boolean isOval = false;
            boolean isTriangle = false;
            boolean isRectangle = false;
            if (selectedShape.equals(Shape.RectangleSolid)||
                    selectedShape.equals(Shape.OvalSolid) ||
                    selectedShape.equals(Shape.TriangleSolid))
                isFill = true;
            if (selectedShape.equals(Shape.OvalSolid)|| selectedShape.equals(Shape.OvalStroke))
                isOval = true;
            if (selectedShape.equals(Shape.RectangleSolid)|| selectedShape.equals(Shape.RectangleStroke))
                isRectangle = true;
            if (selectedShape.equals(Shape.TriangleSolid)|| selectedShape.equals(Shape.TriangleStroke))
                isTriangle = true;

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
                    if(isRectangle)
                        path.addRect(mStartX, mStartY, mEndX, mEndY, Path.Direction.CW);
                    else if(isOval)
                        path.addOval(mStartX, mStartY, mEndX, mEndY, Path.Direction.CW);
                    else if(isTriangle)
                    {
                        int halfWidth =  (int) calculateRadius(mStartX,mStartY,mEndX,mEndY);
                        path.reset();
                        path.moveTo(mStartX,mStartY-halfWidth);
                        path.lineTo(mStartX - halfWidth, mStartY + halfWidth); // Bottom left
                        path.lineTo(mStartX + halfWidth, mStartY + halfWidth); // Bottom right
                        path.lineTo(mStartX, mStartY-halfWidth); // Back to Top
                        path.close();
                    }
                    pathList.add(new PathTracker(path, mStartX, mStartY, mEndX, mEndY,
                            selectedShape, selectedColor, isFill));
                    path = new Path();
                    break;
                default:
                    return false;
            }
            return true;
        }
    }

    // undo drawing steps
    public void UndoDrawing() {
        if(pathList!=null && pathList.size()>0) {
            mBitmap = Bitmap.createBitmap(500, 800, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
            pathList.remove(pathList.size() - 1);
            if (mBitmap != null) {
                for (PathTracker pathTracker : pathList) {
                    if (pathTracker.getIsFill())
                        mPaint.setStyle(Paint.Style.FILL);
                    else
                        mPaint.setStyle(Paint.Style.STROKE);
                    mPaint.setColor(pathTracker.getSelectedColor());
                    mCanvas.drawPath(pathTracker.getPathOfObject(), mPaint);
                }
                mCanvas.drawBitmap(mBitmap, 0, 0, null);
            }
            invalidate();
        }
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;

        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        pathList.add(new PathTracker(mPath, mStartX,mStartX,mStartX,mStartX,selectedShape, selectedColor, false));
    }

    //save drawing
    public void saveDrawing() {
        String storagePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String targetDirPath = storagePath + "/Pictures/";
        File targetDir = new File(targetDirPath);
        if (!targetDir.exists()) {
            if (false == targetDir.mkdirs()) {

                return ;
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


    //get trisangle radii
    private float calculateRadius(float x1, float y1, float x2, float y2) {

        return ((float) Math.sqrt(
                Math.pow(x1 - x2, 2) +
                        Math.pow(y1 - y2, 2))/2
        );
    }

    public List<PointF>  RemoveDuplicates(List<PointF> points)
    {
        List<PointF> finalPoints = new  ArrayList<PointF>();
        for (PointF p : points)
        {
            if(!finalPoints.contains(p))
                finalPoints.add(p);
        }
        return finalPoints;
    }
}
