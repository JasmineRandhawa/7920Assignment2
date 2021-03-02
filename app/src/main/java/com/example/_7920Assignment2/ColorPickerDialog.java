package com.example.a7920assignment2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;


public class ColorPickerDialog extends AppCompatActivity {
    DrawingView dv ;
    private Paint mPaint;
    String selectedColor = Color.GREEN +"";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitiy_color_picker_dialog);

        //add color pallette to layout
        GridView colorPalletteGrid  =   (GridView) findViewById(R.id.colorGrid);
        ListAdapter listAdapter = getColors(getApplicationContext());
        colorPalletteGrid.setAdapter(listAdapter);

        colorPalletteGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                selectedColor = parent.getItemAtPosition(position).toString();
                mPaint.setColor(Integer.parseInt(selectedColor));
            }
        });
        //add drawing view to the screen
        addDrawingView();

        TextView textView = (TextView) findViewById(R.id.btnClearAll);
        SpannableString content = new SpannableString("Clear All");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);

    }
    public static android.widget.ListAdapter getColors(Context context) {
        // Get the ArrayList of HSV colors
        final ArrayList colors = HSVColors();

        // Create an ArrayAdapter using colors list
        ArrayAdapter<Integer> ad = new ArrayAdapter<Integer>(context, android.R.layout.simple_list_item_1, colors) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                int currentColor = (int) colors.get(position);
                view.setBackgroundColor(currentColor);
                view.setText("");
                AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                        AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT
                );
                view.setLayoutParams(lp);
                AbsListView.LayoutParams params = (AbsListView.LayoutParams) view.getLayoutParams();
                params.width = 40;
                params.height = 40;
                view.setLayoutParams(params);
                view.requestLayout();
                return view;
            }
        };
        return ad;
    }

    public static ArrayList HSVColors() {
        ArrayList<Integer> colors = new ArrayList<>();

        // Loop through hue channel, saturation and light full
        for (int h = 0; h <= 360; h += 20) {
            colors.add(HSVColor(h, 1, 1));
        }

        // Loop through hue channel, different saturation and light full
        for (int h = 0; h <= 360; h += 20) {
            colors.add(HSVColor(h, .25f, 1));
            colors.add(HSVColor(h, .5f, 1));
            colors.add(HSVColor(h, .75f, 1));
        }

        // Loop through hue channel, saturation full and light different
        for (int h = 0; h <= 360; h += 20) {
            //colors.add(createColor(h, 1, .25f));
            colors.add(HSVColor(h, 1, .5f));
            colors.add(HSVColor(h, 1, .75f));
        }

        // Loop through the light channel, no hue no saturation
        // It will generate gray colors
        for (float b = 0; b <= 1; b += .10f) {
            colors.add(HSVColor(0, 0, b));
        }
        return colors;
    }

    public static int HSVColor(float hue, float saturation, float black) {
        /*
            Hue is the variation of color
            Hue range 0 to 360

            Saturation is the depth of color
            Range is 0.0 to 1.0 float value
            1.0 is 100% solid color

            Value/Black is the lightness of color
            Range is 0.0 to 1.0 float value
            1.0 is 100% bright less of a color that means black
        */
        int color = Color.HSVToColor(255, new float[]{hue, saturation, black});
        return color;
    }

    public class DrawingView extends View {

        public int width;
        public  int height;
        private Bitmap mBitmap;
        private Canvas mCanvas;
        private Path mPath;
        private Paint mBitmapPaint;
        Context context;
        private Paint circlePaint;
        private Path circlePath;

        public DrawingView(Context c) {
            super(c);
            context=c;
        }


        float mStartX;
        float mStartY;
        float mEndX;
        float mEndY;

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mStartX = event.getX();
                    mStartY = event.getY();

                    break;
                // return true;
                case MotionEvent.ACTION_MOVE:

                    mEndX = event.getX();
                    mEndY = event.getY();

                    mCanvas.drawRect(mStartX, mStartY, mEndX, mEndY, mPaint);

                    invalidate(); // Tell View that the canvas needs to be redrawn
                    break;
                case MotionEvent.ACTION_UP:
                    mCanvas.drawRect(mStartX, mStartY, mEndX, mEndY, mPaint);

                    break;
                default:
                    return false;
            }
            return true;
        }


        @Override
        protected void onDraw(Canvas canvas) {

            super.onDraw(canvas);

            canvas.drawRect(mStartX, mStartY, mEndX, mEndY, mPaint);
        }

        public void drawRectangle(int left, int top, int right, int bottom, Canvas canvas, Paint paint) {
            right = left + right; // width is the distance from left to right
            bottom = top + bottom; // height is the distance from top to bottom
            mCanvas.drawRect(left, top, right, bottom, mPaint);
        }

        /*  public DrawingView(Context c) {
            super(c);
            context=c;
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(Color.BLUE);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(4f);
        }*/

        /*@Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

           canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath( mPath,  mPaint);
            canvas.drawPath( circlePath,  circlePaint);
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y) {
            mPath.reset();
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

                circlePath.reset();
                circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
            }
        }

        private void touch_up() {
            mPath.lineTo(mX, mY);
            //circlePath.reset();
            // commit the path to our offscreen
           // mCanvas.drawPath(mPath,  mPaint);

            int x = (int)mX;  //position coordinate from left
            int y = (int)mY;  //position coordinate from top
            int w = 100; //width of the rectangle
            int h = 100; //height of the rectangle


            // fill
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(Integer.parseInt(selectedColor));

            // border
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(Integer.parseInt(selectedColor));

            drawRectangle(x, y, w, h, mCanvas, mPaint);
            // kill this so we don't double draw
            mPath.reset();
        }



        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }

        public void clear()
        {
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(Color.BLUE);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(4f);
        }*/
    }

    public void clearAllDrawingView(View v)
    {
        View drawingView = findViewById(R.id.drawingView);
        ((ViewGroup) drawingView.getParent()).removeView(drawingView);
        addDrawingView();
    }

    public void addDrawingView()
    {
        //add drawing view to the screen
        final ConstraintLayout drawingViewLayout = findViewById(R.id.drawingView);
        dv = new DrawingView(this);
        dv.setVisibility(View.VISIBLE);
        dv.setId(R.id.drawingView);
        drawingViewLayout.addView(dv);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Integer.parseInt(selectedColor));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
    }

}