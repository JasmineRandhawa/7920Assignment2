package com.example._7920Assignment2;

import android.graphics.Path;

/* Saves history of Object Paths drawn*/
public class LinePathTracker {

    Path pathOfObject;
    int StartX;
    int StartY;
    int EndX;
    int EndY;
    String SelectedShape;
    String Drawingmode;
    int SelectedColor;
    boolean IsFill;

    // Constructor of Path Tracker class
    public LinePathTracker(Path pathOfObject, int startX, int startY, int endX, int endY,
                           String selectedShape, String drawingmode, int selectedColor, boolean isFill) {
        this.pathOfObject = pathOfObject;
        StartX = startX;
        StartY = startY;
        EndX = endX;
        EndY = endY;
        SelectedShape = selectedShape;
        Drawingmode = drawingmode;
        SelectedColor = selectedColor;
        IsFill = isFill;
    }

    // getters and setters of Path Tracker class fields
    public Path getPathOfObject() {
        return pathOfObject;
    }

    public int getSelectedColor() {
        return SelectedColor;
    }

    public boolean getIsFill() {
        return IsFill;
    }


}
