package com.example._7920Assignment2;

import android.graphics.Path;

import java.util.List;

public class PathData {


    Path Path ;
    Path TempPath ;
    List<PathPoint> pathPointList ;

    public PathData(Path path,Path tempPath ,List<PathPoint> pathPointList,  int selectedColor, boolean isFill) {
        this.Path = path;
        this.TempPath = tempPath;
        this.pathPointList = pathPointList;
        SelectedColor = selectedColor;
        IsFill = isFill;
    }

    int SelectedColor;
    boolean IsFill;
}
