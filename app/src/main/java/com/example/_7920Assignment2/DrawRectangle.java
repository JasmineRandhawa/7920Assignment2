package com.example._7920Assignment2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
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
class DrawRectangle extends View {

    private final int TOUCH_TOLERANCE = 4;
    List<RectanglePathTracker> pathList;
    Canvas mCanvas;
    int mStartX;
    int mStartY;
    int mEndX;
    int mEndY;
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
    public DrawRectangle(Context c) {
        //add drawing view to the screen
        super(c);
        context = c;
        mStartX = 0;
        mStartY = 0;
        mEndX = 0;
        mEndY = 0;
        mPath = new Path();
        selectedColor = Color.MAGENTA;
        pathList = new ArrayList<RectanglePathTracker>();
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
        if (selectedShape.equals(Shape.RectangleStroke))
            isFill = false;
        else if (selectedShape.equals(Shape.RectangleSolid))
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
            for (RectanglePathTracker pathTracker : pathList) {
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
                    mPath = new Path();
                    mPath.addRect(mStartX, mStartY, mEndX, mEndY, Path.Direction.CW);
                    pathList.add(new RectanglePathTracker(mPath, mStartX, mStartY, mEndX, mEndY,
                            selectedShape, drawingMode, selectedColor, isFill));
                    mPath = new Path();
                    break;
                default:
                    return false;
            }
            return true;
        }
        return true;
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
        PathMeasure pathMeasure = new PathMeasure(mPath,true);
        float pathLength = pathMeasure.getLength();
        int radius = (int)((pathLength)/(2*3.14f));
        mPath.addCircle(mStartX, mEndY,radius,Path.Direction.CW);
        pathList.add(new RectanglePathTracker(mPath, mStartX, mStartY, mEndX, mEndY,
                selectedShape, drawingMode, selectedColor, isFill));
        mPath = new Path();
    }

    // undo drawing steps
    public void UndoDrawing() {
        if (pathList != null && pathList.size() > 0) {
            mBitmap = Bitmap.createBitmap(500, 800, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
            pathList.remove(pathList.size() - 1);
            if (mBitmap != null) {
                for (RectanglePathTracker pathTracker : pathList) {
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
