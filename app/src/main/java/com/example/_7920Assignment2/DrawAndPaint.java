package com.example._7920Assignment2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/* Draw and Paint Shapes
 Four chapes are used with unfill anf fill funtionality
 Shapes Used: Line, Rectangle/Square, Oval/Circle and Triangle */
public class DrawAndPaint extends AppCompatActivity {
    private static final int RESULT_LOAD_IMAGE =1 ;
    DrawView drawingView;
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

    View prevView = null;
    int prevColor = -1;
    //add  color palette  to screen
    private void CreateColorPalette()
    {
        GridView colorPalletteGrid = (GridView) findViewById(R.id.colorGrid);
        ListAdapter colorPalleteListAdapter =  new ColorPalette.ColorListAdapter(this,ColorPalette.HSVColors());
        colorPalletteGrid.setAdapter(colorPalleteListAdapter);
        colorPalletteGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                selectedColor = parent.getItemAtPosition(position).toString();
                view.setSelected(true);
                drawingView.SetPaintColor(Integer.parseInt(selectedColor));
            }
        });
    }

    //add  shapes palette  to screen
    private void CreateShapesView() {
        shapes.add(new Shape(Shape.Line, R.drawable.line, 0));
        shapes.add(new Shape(Shape.SquareSolid, R.drawable.square_solid, 1));
        shapes.add(new Shape(Shape.SquareStroke, R.drawable.square_stroke, 2));
        shapes.add(new Shape(Shape.CircleSolid, R.drawable.circle_solid, 3));
        shapes.add(new Shape(Shape.CircleStroke, R.drawable.circle_stroke, 4));
        shapes.add(new Shape(Shape.TriangleSolid, R.drawable.triangle_solid, 5));
        shapes.add(new Shape(Shape.TriangleStroke, R.drawable.triangle_stroke, 6));

        Shape.ShapeListAdapter shapesAdapter = new Shape.ShapeListAdapter(this, shapes);
        ListView listview_shapes = (ListView) findViewById(R.id.listview_shapes);
        listview_shapes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> list, View lv, int position, long id) {
                selectedShapeListItem = Shape.GetItemForAtPosition(position, shapes);
                drawingView.SetShape(selectedShapeListItem.getShapeName());
                LinearLayout freeHandImage =  findViewById(R.id.layoutPencil);
                freeHandImage.setBackgroundColor(Color.WHITE);
            }
        });
        listview_shapes.setAdapter(shapesAdapter);
    }

    //add  drawng view  to screen
    private void CreateDrawingView()
    {
        drawingViewLayout = findViewById(R.id.drawingViewLayout);
        drawingView = new DrawView(context);
        drawingView.setVisibility(View.VISIBLE);
        drawingView.setId(R.id.drawingView);
        drawingViewLayout.addView(drawingView);
    }

    //setup options for drawing - Undo, save ,clearAll
    private void SetUpImageButtons() {
        ImageButton saveButton = (ImageButton) findViewById(R.id.btnSave);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String fileName = drawingView.saveDrawing();
                    if(!fileName.equals(""))
                    {
                        File folder = new File(Environment.getExternalStorageDirectory().getPath()
                                + File.separator + "Pictures" + File.separator);
                        File[] allFiles = folder.listFiles();
                        if(allFiles!=null && allFiles.length>0) {
                            File imageFile = allFiles[allFiles.length - 2];
                            File lastFile = allFiles[allFiles.length - 1];
                            lastFile.delete();
                            new SingleMediaScanner(context, imageFile);
                        }
                    }
                    else
                        Toast.makeText(context, "Error in saving", Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    Toast.makeText(context, "Error in saving", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageButton undoButton = (ImageButton) findViewById(R.id.btnUndo);
        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.UndoDrawing();
            }
        });

        ImageButton resetButton = (ImageButton) findViewById(R.id.btnClearAll);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.SetPaintColor(Integer.parseInt(Color.MAGENTA+""));
                finish();
                Intent intent = getIntent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });


        RadioGroup radioGroupDrawingMode =  findViewById(R.id.radioDrawingMode);
        radioGroupDrawingMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int selectedId = radioGroupDrawingMode.getCheckedRadioButtonId();
                RadioButton radioDrawingModeButton = (RadioButton) findViewById(selectedId);
                String drawingMode = radioDrawingModeButton.getText().toString();
                drawingView.SetDrawingMode(drawingMode);
            }
        });
        ((RadioButton)radioGroupDrawingMode.getChildAt(0)).setChecked(true);
        drawingView.SetDrawingMode(Shape.FreeHandDrawingMode);

        ImageButton btnPencil =  findViewById(R.id.btnPencil);
        btnPencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListView shapeListView = (ListView) findViewById(R.id.listview_shapes);
                CreateShapesView();
                drawingView.SetShape("Custom");
                LinearLayout freeHandImage =  findViewById(R.id.layoutPencil);
                freeHandImage.setBackgroundColor(Color.BLUE);
            }
        });
        LinearLayout freeHandImage =  findViewById(R.id.layoutPencil);
        freeHandImage.setBackgroundColor(Color.BLUE);
    }

    //open saved image
    public class SingleMediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {

        private final MediaScannerConnection mMs;
        private final File mFile;

        public SingleMediaScanner(Context context, File f) {
            mFile = f;
            mMs = new MediaScannerConnection(context, this);
            mMs.connect();
        }

        public void onMediaScannerConnected() {
            mMs.scanFile(mFile.getAbsolutePath(), null);
        }

        public void onScanCompleted(String path, Uri uri) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            mMs.disconnect();
            finish();
        }
    }

}