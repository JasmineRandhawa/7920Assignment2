package com.example._7920Assignment2;

import android.graphics.Path;

import java.util.List;

public class PathData {


    Path Path ;
    List<PathPoint> pathPointList ;

    public PathData(Path path, List<PathPoint> pathPointList,  int selectedColor, boolean isFill) {
        this.Path = path;
        this.pathPointList = pathPointList;
        SelectedColor = selectedColor;
        IsFill = isFill;
    }

    int SelectedColor;
    boolean IsFill;
}
