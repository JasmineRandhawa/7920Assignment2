package com.example._7920Assignment2;

import android.graphics.Path;
import android.graphics.PathMeasure;
import java.util.ArrayList;
import java.util.List;

/* Contains generic functions */
public class Utility {

    //get trisangle radii
    public static float CalculateRadius(float x1, float y1, float x2, float y2) {

        return ((float) Math.sqrt(
                Math.pow(x1 - x2, 2) +
                        Math.pow(y1 - y2, 2)) / 2
        );
    }

    //removed duplicate from points list
    public static List<PathPoint> RemoveDuplicates(List<PathPoint> points) {
        List<PathPoint> finalPoints = new ArrayList<PathPoint>();
        for (PathPoint p : points) {
            if (!ContainsPoints(finalPoints, p))
                finalPoints.add(new PathPoint( p.x,  p.y));
        }
        return finalPoints;
    }

    //checks if a point exists in a list
    public static boolean ContainsPoints(List<PathPoint> finalPoints, PathPoint p) {
        boolean isInList = false;
        for (PathPoint point : finalPoints) {
            if ( point.getX() ==  p.getX() &&  point.getY() == p.getY())
                isInList = true;
        }
        return isInList;
    }

    //get point list after second point of triangle
    public static List<PathPoint> GetNextList(int peakIndex, List<PathPoint> pList) {
        List<PathPoint> nextList = new ArrayList<PathPoint>();
        for (int i = peakIndex + 1; i <= pList.size() - 1; i++) {
            nextList.add(new PathPoint(pList.get(i).x,  pList.get(i).y));
        }
        return nextList;
    }

    //compute distnace betwwen two points
    public static float DistanceBetweenTwoPoints(float x1, float x2, float y1, float y2) {
        return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    //compute cicle center point
    public static PathPoint CalculateCircleCenter(float x1, float x2, float y1, float y2) {
        return new PathPoint((float) (x1 + x2) / 2, (float) (y1 + y2) / 2);
    }

    //compure mid point of a line or path
    public static PathPoint CalculatePathMidPoint(Path path) {
        PathMeasure pm = new PathMeasure(path, true);
        //coordinates will be here
        float[] aCoordinates = {0f, 0f};

        //get coordinates of the middle point
        pm.getPosTan(pm.getLength() * 0.5f, aCoordinates, null);
        return new PathPoint(aCoordinates[0], aCoordinates[1]);
    }

    // get all points on a path
    public static List<PathPoint> GetPoints(Path path) {
        List<PathPoint> pointList = new ArrayList<>();
        PathMeasure pm = new PathMeasure(path, false);
        float length = pm.getLength();
        float distance = 0f;
        float speed = length / 70;
        int counter = 0;
        float[] aCoordinates = new float[2];

        while ((distance < length) && (counter < 70)) {
            pm.getPosTan(distance, aCoordinates, null);
            pointList.add(new PathPoint(aCoordinates[0],
                    aCoordinates[1]));
            counter++;
            distance = distance + speed;
        }
        return pointList;
    }
}
