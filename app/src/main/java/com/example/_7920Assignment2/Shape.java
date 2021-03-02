package com.example._7920Assignment2;

import java.util.ArrayList;

public class Shape {

    public String ShapeName;
    public long ResourceId;
    public long Position;

    public Shape(String shapeName, long resourceId, long position) {
        ShapeName = shapeName;
        ResourceId = resourceId;
        Position = position;
    }
    public String getShapeName() {
        return ShapeName;
    }

    public void setShapeName(String shapeName) {
        ShapeName = shapeName;
    }

    public long getResourceId() {
        return ResourceId;
    }

    public void setResourceId(long resourceId) {
        ResourceId = resourceId;
    }

    public long getPosition() {
        return Position;
    }

    public void setPosition(long position) {
        Position = position;
    }


    public static Shape GetItemForAtPosition(int position,ArrayList<Shape> shapes) {

        for(Shape item:shapes)
        {
            if(item.getPosition() == position)
                return item;
        }
        return null;
    }
}
