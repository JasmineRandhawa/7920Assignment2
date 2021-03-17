package com.example._7920Assignment2;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

/* Shape class for storing shape data */
public class Shape {

    public String ShapeName;
    public long ResourceId;
    public long Position;
    public static String SquareStroke = "Square Solid";
    public static String SquareSolid = "Square Stroke";
    public static String CircleSolid = "Circle Solid";
    public static String CircleStroke = "Circle Stroke";
    public static String TriangleSolid = "Triangle Solid";
    public static String TriangleStroke = "Triangle Stroke";
    public static String RhombusSolid = "Rhombus Solid";
    public static String RhombusStroke = "Rhombus Stroke";
    public static String Line = "Line";
    public static String Custom = "Custom";
    public static String FreeHandDrawingMode = "Free Hand";
    public static String AutomaticDrawingmMode = "Automatic";


    //constructor of shape class
    public Shape(String shapeName, long resourceId, long position) {
        ShapeName = shapeName;
        ResourceId = resourceId;
        Position = position;
    }

    //getter amnd setters of shape class fields
    public String getShapeName() {
        return ShapeName;
    }


    public long getResourceId() {
        return ResourceId;
    }


    public long getPosition() {
        return Position;
    }


    // get shape selected based on listviewitem index position
    public static Shape GetItemForAtPosition(int position,ArrayList<Shape> shapes) {

        for(Shape item:shapes)
        {
            if(item.getPosition() == position)
                return item;
        }
        return null;
    }


    //shape list adapter to bind listview containing all shapes
    public  static class ShapeListAdapter extends ArrayAdapter<Shape> {
        int shapeSel = 0;
        public ShapeListAdapter(Activity context, ArrayList<Shape> shapes ,int shapeSelector) {
            super(context, 0, shapes);
            shapeSel = shapeSelector;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View listItemView = convertView;
            if (listItemView == null) {
                if(shapeSel ==1)
                listItemView = LayoutInflater.from(getContext()).inflate(
                        R.layout.shapes_list, parent, false);
                else if (shapeSel==2)
                    listItemView = LayoutInflater.from(getContext()).inflate(
                            R.layout.additional_shapes_list, parent, false);
            }
            Shape shape = getItem(position);
            ImageView shapeImage = null;
            if(shapeSel ==1)
                shapeImage = listItemView.findViewById(R.id.shape);
            else if (shapeSel ==2)
                shapeImage = listItemView.findViewById(R.id.additional_shape);
            if(shapeImage!=null)
                shapeImage.setImageResource((int) shape.getResourceId());
            return listItemView;
        }

    }
}
