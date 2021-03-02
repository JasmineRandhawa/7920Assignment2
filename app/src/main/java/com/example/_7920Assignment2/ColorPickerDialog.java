package com.example._7920Assignment2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.List;


public class ColorPickerDialog extends AppCompatActivity {
    DrawingView dv;
    private Paint mPaint;
    String selectedColor = Color.GREEN + "";
    int shapeSelectedValue = -1;
    List<View> otherShapes = new ArrayList<View>();

    //bind colors to gridview adapter
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

    //get all colors for collor pallette
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
        // Loop through the light channel, no hue no saturation
        // It will generate gray colors
        for (float b = 0; b <= 1; b += .10f) {
            colors.add(HSVColor(0, 0, b));
        }
        // Loop through hue channel, saturation full and light different
        for (int h = 0; h <= 360; h += 20) {
            //colors.add(createColor(h, 1, .25f));
            colors.add(HSVColor(h, 1, .5f));
            colors.add(HSVColor(h, 1, .75f));
        }


        return colors;
    }

    // get color from android
    public static int HSVColor(float hue, float saturation, float black) {
        int color = Color.HSVToColor(255, new float[]{hue, saturation, black});
        return color;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitiy_color_picker_dialog);

        //add color pallette to layout
        GridView colorPalletteGrid = (GridView) findViewById(R.id.colorGrid);
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
        populateShapesListeView();
        //add drawing view to the screen
        addDrawingView();

        TextView textView = (TextView) findViewById(R.id.btnClearAll);
        SpannableString content = new SpannableString("Clear All");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);

    }

    public void populateShapesListeView() {
        // Create an ArrayList of Dessert objects
        ArrayList<Integer> shapes = new ArrayList<Integer>();

        shapes.add(new Integer(R.drawable.rectangle_solid));
        shapes.add(new Integer(R.drawable.rectangle_stroke));
        shapes.add(new Integer(R.drawable.square_solid));
        shapes.add(new Integer(R.drawable.square_stroke));
        shapes.add(new Integer(R.drawable.circle_solid));
        shapes.add(new Integer(R.drawable.circle_stroke));
        shapes.add(new Integer(R.drawable.triangle_solid));
        shapes.add(new Integer(R.drawable.triangle_stroke));

        ShapesAdapter shapesAdapter = new ShapesAdapter(this, shapes);
        ListView listview_shapes = (ListView) findViewById(R.id.listview_shapes);
        listview_shapes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> list, View lv, int position, long id) {
                shapeSelectedValue = position;
            }
        });
        listview_shapes.setAdapter(shapesAdapter);
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

    // draw on drawing view
    public class DrawingView extends View {


        private Canvas mCanvas;
        private Path mPath;
        Context context;

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

                    //mCanvas.drawRect(mStartX, mStartY, mEndX, mEndY, mPaint);

                    invalidate(); // Tell View that the canvas needs to be redrawn
                    break;
                case MotionEvent.ACTION_UP:
                    mEndX = event.getX();
                    mEndY = event.getY();
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
            invalidate();
        }

    }

    public class ShapesAdapter extends ArrayAdapter<Integer> {

        public ShapesAdapter(Activity context, ArrayList<Integer> shapes) {
            super(context, 0, shapes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View listItemView = convertView;
            if (listItemView == null) {
                listItemView = LayoutInflater.from(getContext()).inflate(
                        R.layout.shapes_list, parent, false);
            }
            Integer shapeIndex = getItem(position);
            ImageView shape = (ImageView) listItemView.findViewById(R.id.shape);
            shape.setImageResource(shapeIndex);
            return listItemView;
        }
    }

}