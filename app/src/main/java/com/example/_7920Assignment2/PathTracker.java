package com.example._7920Assignment2;

import android.graphics.Path;

/* Saves history of Object Paths drawn*/
public class PathTracker {
    Path pathOfObject;
    float StartX;
    float StartY;
    float EndX;
    float EndY;
    String SelectedShape;
    int SelectedColor;
    boolean IsFill;

    // Constructor of Path Tracker class
    public PathTracker(Path pathOfObject, float startX, float startY, float endX, float endY,
                       String selectedShape, int selectedColor, boolean isFill) {
        this.pathOfObject = pathOfObject;
        StartX = startX;
        StartY = startY;
        EndX = endX;
        EndY = endY;
        SelectedShape = selectedShape;
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
