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
    public static String RectangleStroke = "Rectangle Solid";
    public static String RectangleSolid = "Rectangle Stroke";
    public static String CircleSolid = "Circle Solid";
    public static String CircleStroke = "Circle Stroke";
    public static String TriangleSolid = "Triangle Solid";
    public static String TriangleStroke = "Triangle Stroke";
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
