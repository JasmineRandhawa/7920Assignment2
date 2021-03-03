package com.example._7920Assignment2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.File;
import java.util.ArrayList;

/* Draw and Paint Shapes
 Four chapes are used with unfill anf fill funtionality
 Shapes Used: Line, Rectangle/Square, Oval/Circle and Triangle */
public class DrawAndPaint extends AppCompatActivity {
    private static final int RESULT_LOAD_IMAGE =1 ;
    DrawingView dv;
    Context context;
    ConstraintLayout drawingViewLayout;
    String selectedColor = Color.GREEN + "";
    Shape selectedShapeListItem;
    ArrayList<Shape> shapes;

    //add shapes, color palette , drawing view and clear button
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

        //setup image button for drawing options
        SetUpImageButtons();
    }

    //add  color palette  to screen
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

    //add  shapes palette  to screen
    private void CreateShapesView()
    {
        shapes.add(new Shape(Shape.Line,R.drawable.line,0));
        shapes.add(new Shape(Shape.RectangleSolid,R.drawable.rectangle_solid,1));
        shapes.add(new Shape(Shape.RectangleStroke,R.drawable.rectangle_stroke,2));
        shapes.add(new Shape(Shape.OvalSolid,R.drawable.oval_solid,3));
        shapes.add(new Shape(Shape.OvalStroke,R.drawable.oval_stroke,4));
        shapes.add(new Shape(Shape.TriangleSolid,R.drawable.triangle_solid,5));
        shapes.add(new Shape(Shape.TriangleStroke,R.drawable.triangle_stroke,6));

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

    //add  drawng view  to screen
    private void CreateDrawingView()
    {
        drawingViewLayout = findViewById(R.id.drawingViewLayout);
        dv = new DrawingView(context);
        dv.setVisibility(View.VISIBLE);
        dv.setId(R.id.drawingView);
        drawingViewLayout.addView(dv);
    }

    //setup options for drawing - Undo, save ,clearAll
    private void SetUpImageButtons() {
        ImageButton saveButton = (ImageButton) findViewById(R.id.btnSave);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dv.saveDrawing();
                Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()
                        +  File.separator + "Pictures" + File.separator);
                Intent intent = new Intent(Intent.ACTION_PICK,uri);
                intent.setType("image/*");
                startActivity(intent);
            }
        });

        ImageButton undoButton = (ImageButton) findViewById(R.id.btnUndo);
        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dv.UndoDrawing();
            }
        });

        ImageButton resetButton = (ImageButton) findViewById(R.id.btnClearAll);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = getIntent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });
    }


    //shape list adapter to bind listview containing all shapes
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