package com.example._7920Assignment2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class DrawAndPaint extends AppCompatActivity {
    DrawingView dv;
    Context context;
    ConstraintLayout drawingViewLayout;
    String selectedColor = Color.GREEN + "";
    Shape selectedShapeListItem;
    ArrayList<Shape> shapes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitiy_draw_and_paint);
        context = getApplicationContext();
        shapes= new ArrayList<Shape>();

        //add color pallette to layout
        CreateColorPalette();

        //add shappes listview to the screen
        CreateShapesView();

        //add drawing view to the screen
        CreateDrawingView();

        // adding clear button
        CreateClearButton();

    }

    private void CreateColorPalette()
    {
        GridView colorPalletteGrid = (GridView) findViewById(R.id.colorGrid);
        ListAdapter colorPalleteListAdapter = ColorPalette.Create(context, android.R.layout.simple_list_item_1);
        colorPalletteGrid.setAdapter(colorPalleteListAdapter);
        colorPalletteGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                selectedColor = parent.getItemAtPosition(position).toString();
                DrawingView.SetPaintColor(Integer.parseInt(selectedColor));
            }
        });
    }

    private void CreateShapesView()
    {
        shapes.add(new Shape("Line",R.drawable.line,0));
        shapes.add(new Shape("Rectangle Solid",R.drawable.rectangle_solid,1));
        shapes.add(new Shape("Rectangle Stroke",R.drawable.rectangle_stroke,2));
        shapes.add(new Shape("Circle Solid",R.drawable.circle_solid,3));
        shapes.add(new Shape("Circle Stroke",R.drawable.circle_stroke,4));
        shapes.add(new Shape("Triangle Solid",R.drawable.triangle_solid,5));
        shapes.add(new Shape("Triangle Stroke",R.drawable.triangle_stroke,6));


        ShapeListAdapter shapesAdapter = new ShapeListAdapter(this,shapes);
        ListView listview_shapes = (ListView) findViewById(R.id.listview_shapes);
        listview_shapes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> list, View lv, int position, long id) {
                selectedShapeListItem = Shape.GetItemForAtPosition(position,shapes);
                DrawingView.SetShape(selectedShapeListItem.getShapeName());
            }
        });
        listview_shapes.setAdapter(shapesAdapter);

    }

    private void CreateDrawingView()
    {
        drawingViewLayout = findViewById(R.id.drawingViewLayout);
        dv = new DrawingView(context);
        dv.setVisibility(View.VISIBLE);
        dv.setId(R.id.drawingView);
        drawingViewLayout.addView(dv);
    }

    public void CreateClearButton()
    {
        TextView textView = (TextView) findViewById(R.id.btnClearAll);
        SpannableString content = new SpannableString("Clear All");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);
    }

    private void clearAllDrawingView(View view) {
        drawingViewLayout.removeView(dv);
        dv = new DrawingView(context);
        dv.setVisibility(View.VISIBLE);
        dv.setId(R.id.drawingView);
        drawingViewLayout.addView(dv);
    }

    public class ShapeListAdapter extends ArrayAdapter<Shape> {

        public ShapeListAdapter(Activity context, ArrayList<Shape> shapes) {
            super(context, 0, shapes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View listItemView = convertView;
            if (listItemView == null) {
                listItemView = LayoutInflater.from(getContext()).inflate(
                        R.layout.shapes_list, parent, false);
            }
            Shape shape = getItem(position);
            ImageView shapeImage = (ImageView) listItemView.findViewById(R.id.shape);
            shapeImage.setImageResource((int) shape.getResourceId());
            return listItemView;
        }

    }
}