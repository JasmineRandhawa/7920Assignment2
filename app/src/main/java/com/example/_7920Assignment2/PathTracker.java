package com.example._7920Assignment2;

import android.graphics.Path;

public class PathTracker {
    Path pathOfObject;
    float StartX;
    float StartY;
    float EndX;
    float EndY;
    String SelectedShape;
    int SelectedColor;
    boolean IsFill;

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
    public Path getPathOfObject() {
        return pathOfObject;
    }

    public void setPathOfObject(Path pathOfObject) {
        this.pathOfObject = pathOfObject;
    }

    public float getStartX() {
        return StartX;
    }

    public void setStartX(float startX) {
        StartX = startX;
    }

    public float getStartY() {
        return StartY;
    }

    public void setStartY(float startY) {
        StartY = startY;
    }

    public float getEndX() {
        return EndX;
    }

    public void setEndX(float endX) {
        EndX = endX;
    }

    public float getEndY() {
        return EndY;
    }

    public void setEndY(float endY) {
        EndY = endY;
    }

    public String getSelectedShape() {
        return SelectedShape;
    }

    public void setSelectedShape(String selectedShape) {
        SelectedShape = selectedShape;
    }

    public int getSelectedColor() {
        return SelectedColor;
    }

    public void setSelectedColor(int selectedColor) {
        SelectedColor = selectedColor;
    }

    public boolean getIsFill() {
        return IsFill;
    }
    public void setIsFill(boolean isFill) {
        IsFill = isFill;
    }
}
