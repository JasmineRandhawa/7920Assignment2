package com.example._7920Assignment2;

import android.graphics.Path;
import android.graphics.PathMeasure;

import java.util.ArrayList;
import java.util.List;

/* Contains generic functions */
public class MyPointClass {

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
            nextList.add(new PathPoint(pList.get(i).x, pList.get(i).y));
        }
        return nextList;
    }

    //compute distnace betwwen two points
    public static float DistanceBetweenTwoPoints(float x1, float x2, float y1, float y2) {
        return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    //compute cicle center point
    public static PathPoint CalculateCircleCenter(int x1, int x2, int y1, int y2) {
        return new PathPoint((x1 + x2) / 2, (y1 + y2) / 2);
    }

    //compure mid point of a line or path
    public static PathPoint CalculatePathMidPoint(Path path) {
        PathMeasure pm = new PathMeasure(path, true);
        //coordinates will be here
        float[] aCoordinates = {0f, 0f};

        //get coordinates of the middle point
        pm.getPosTan(pm.getLength() * 0.5f, aCoordinates, null);
        return new PathPoint((int) aCoordinates[0], (int) aCoordinates[1]);
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
            pointList.add(new PathPoint((int) aCoordinates[0], (int) aCoordinates[1]));
            counter++;
            distance = distance + speed;
        }
        return pointList;
    }


    //check next 5 points are increasing or decreasing to judge a turn in path
    public static boolean CheckNextFivePoints(List<PathPoint> pointList, int index, String direction) {
        int checkChangeCount = 0;
        int lastIndex = pointList.size() - index - 1;
        if (pointList.size() - index - 1 > 5)
            lastIndex = 4;
        PathPoint point = pointList.get(index - 1);

        for (int i = 0; i <= lastIndex; i++) {
            PathPoint nextPoint = pointList.get(index + i);
            if (direction.equals("xdec") && point.x > nextPoint.x)
                checkChangeCount++;

            else if (direction.equals("ydec") && point.y > nextPoint.y)
                checkChangeCount++;

            else if (direction.equals("yinc") && point.y < nextPoint.y)
                checkChangeCount++;

            else if (direction.equals("xinc") && point.x < nextPoint.x)
                checkChangeCount++;
        }
        return checkChangeCount == lastIndex + 1;
    }

    //check next 5 points are increasing or decreasing to judge a turn in path
    public static String CheckDirection(List<PathPoint> pointList) {
        String direction="";
        int checkRight = 0;
        int checkTop= 0;
        int checkLeft= 0;
        int checkBottom= 0;
        int lastIndex = pointList.size()  - 1;
        if (pointList.size() -  1 > 10)
            lastIndex = 10;
        PathPoint point = pointList.get(0);

        for (int i = 1; i <= lastIndex-1; i++) {
            PathPoint nextPoint = pointList.get(i);
            if (point.x > nextPoint.x)
                checkLeft++;

             if (point.y > nextPoint.y)
                checkTop++;

             if (point.y < nextPoint.y)
                checkBottom++;

             if (point.x < nextPoint.x)
                 checkRight++;
        }
        if(checkLeft > checkRight)
            direction = "left";
        else
            direction =  "right";

        if(checkBottom > checkTop)
            direction =direction+"bottom";
        else
            direction =direction+"top";
        return direction;
    }

    // get Path corners
    public static List<PathPoint> GetPathCornersRhombus(Path path) {
        List<PathPoint> cornerPoints = new ArrayList<PathPoint>();
        List<PathPoint> newPointList = MyPointClass.GetPoints(path);
        newPointList = MyPointClass.RemoveDuplicates(newPointList);
        if (newPointList != null && newPointList.size() > 0) {
            PathPoint firstPoint = newPointList.get(0);
            List<PathPoint> pointList = new ArrayList<>();
            //pointList.add(newPointList.get(0));
            // first 10 points
            for (int i = 10; i <= newPointList.size() - 1; i++) {
                pointList.add(newPointList.get(i));
            }
            // find direction of triangle
            PathPoint initialPointOfPath = new PathPoint(pointList.get(pointList.size() / 10).getX(),
                    pointList.get(pointList.size() / 10).getY());

            boolean isRightDirection = initialPointOfPath.getX() > firstPoint.getX();
            boolean isTopDirection = initialPointOfPath.getY() < firstPoint.getY();

            //add first corner of path
            cornerPoints.add(firstPoint);
            List<PathPoint> nextList = null;
            //find second corner of path
            PathPoint secondCorner = GetNextCorner(pointList, 1, isRightDirection, isTopDirection);
            PathPoint thirdCorner = null, fourthCorner;
            if (secondCorner != null) {
                int indexOfSecondCornerInList = secondCorner.getPointIndex();
                cornerPoints.add(secondCorner);
                //find third corner of path
                nextList = MyPointClass.GetNextList(indexOfSecondCornerInList, pointList);
                if (nextList != null && nextList.size() > 0) {
                    initialPointOfPath = new PathPoint(nextList.get(nextList.size() / 5).getX(),
                            nextList.get(nextList.size() / 5).getY());
                    isRightDirection = initialPointOfPath.getX() > secondCorner.getX();
                    isTopDirection = initialPointOfPath.getY() < secondCorner.getY();
                    thirdCorner = GetNextCorner(nextList, 1, isRightDirection, isTopDirection);
                    if (thirdCorner != null)
                        cornerPoints.add(thirdCorner);
                }
            }

            if (thirdCorner != null) {
                int indexOfSecondCornerInList = thirdCorner.getPointIndex();
                //find third corner of path
                List<PathPoint> nextListForFourth = MyPointClass.GetNextList(indexOfSecondCornerInList, nextList);
                if (nextListForFourth != null && nextListForFourth.size() > 0) {
                    initialPointOfPath = new PathPoint(nextListForFourth.get(nextListForFourth.size() / 5).getX(),
                            nextListForFourth.get(nextListForFourth.size() / 5).getY());
                    isRightDirection = initialPointOfPath.getX() > thirdCorner.getX();
                    isTopDirection = initialPointOfPath.getY() < thirdCorner.getY();
                    fourthCorner = GetNextCorner(nextListForFourth, 2, isRightDirection, isTopDirection);
                    if (fourthCorner != null)
                        cornerPoints.add(fourthCorner);
                }
            }
        }
        return cornerPoints;
    }

    // get Path corners
    public static List<PathPoint> GetPathCornersTriangle(Path path) {
        List<PathPoint> cornerPoints = new ArrayList<PathPoint>();
        List<PathPoint> newPointList = MyPointClass.GetPoints(path);
        newPointList = MyPointClass.RemoveDuplicates(newPointList);
        if (newPointList != null && newPointList.size() > 0) {
            PathPoint firstPoint = newPointList.get(0);
            List<PathPoint> pointList = new ArrayList<>();
            //pointList.add(newPointList.get(0));
            // first 10 points
            for (int i = 10; i <= newPointList.size() - 1; i++) {
                pointList.add(newPointList.get(i));
            }
            // find direction of triangle
            PathPoint initialPointOfPath = new PathPoint(pointList.get(pointList.size() / 10).getX(),
                    pointList.get(pointList.size() / 10).getY());

            boolean isRightDirection = initialPointOfPath.getX() > firstPoint.getX();
            boolean isTopDirection = initialPointOfPath.getY() < firstPoint.getY();

            //add first corner of path
            cornerPoints.add(firstPoint);

            //find second corner of path
            PathPoint secondCorner = GetNextCorner(pointList, 1, isRightDirection, isTopDirection);
            if (secondCorner != null) {
                int indexOfSecondCornerInList = secondCorner.getPointIndex();
                cornerPoints.add(secondCorner);
                //find third corner of path
                List<PathPoint> nextList = MyPointClass.GetNextList(indexOfSecondCornerInList, pointList);
                if (nextList != null && nextList.size() > 0) {
                    initialPointOfPath = new PathPoint(nextList.get(nextList.size() / 5).getX(),
                            nextList.get(nextList.size() / 5).getY());
                    isRightDirection = initialPointOfPath.getX() > secondCorner.getX();
                    isTopDirection = initialPointOfPath.getY() < secondCorner.getY();
                    PathPoint thirdCorner = GetNextCorner(nextList, 2, isRightDirection, isTopDirection);
                    cornerPoints.add(thirdCorner);
                }
            }
        }
        return cornerPoints;
    }

    //get next corner of path
    public static PathPoint GetNextCorner(List<PathPoint> pointList, int cornerIndex, boolean isRightDirection, boolean isTopDirection) {
        PathPoint currentPoint;
        for (int i = 1; i <= pointList.size() - 1; i++) {
            PathPoint prevPoint = pointList.get(i - 1);
            currentPoint = pointList.get(i);
            if (isRightDirection && isTopDirection) {
                if ((currentPoint.x < prevPoint.x && MyPointClass.CheckNextFivePoints(pointList, i, "xdec")) ||
                        (currentPoint.y > prevPoint.y && MyPointClass.CheckNextFivePoints(pointList, i, "yinc"))) {
                    currentPoint.setPointIndex(i);
                    return currentPoint;
                }
            } else if (!isRightDirection && isTopDirection) {
                if ((currentPoint.x > prevPoint.x && MyPointClass.CheckNextFivePoints(pointList, i, "xdec")) ||
                        (currentPoint.y > prevPoint.y && MyPointClass.CheckNextFivePoints(pointList, i, "yinc"))) {
                    currentPoint.setPointIndex(i);
                    return currentPoint;
                }
            } else if (!isRightDirection && !isTopDirection) {
                if ((currentPoint.x > prevPoint.x && MyPointClass.CheckNextFivePoints(pointList, i, "xinc")) ||
                        (currentPoint.y < prevPoint.y && MyPointClass.CheckNextFivePoints(pointList, i, "ydec"))
                ) {
                    currentPoint.setPointIndex(i);
                    return currentPoint;
                }
            } else if (isRightDirection && !isTopDirection) {
                if ((currentPoint.x < prevPoint.x && MyPointClass.CheckNextFivePoints(pointList, i, "xdec")) ||
                        (currentPoint.y < prevPoint.y && MyPointClass.CheckNextFivePoints(pointList, i, "ydec"))) {
                    currentPoint.setPointIndex(i);
                    return currentPoint;
                }
            }
        }
        if (cornerIndex == 1) {
            currentPoint = null;
        } else {
            currentPoint = new PathPoint(pointList.get(pointList.size() - 1).x, pointList.get(pointList.size() - 1).y);
            currentPoint.setPointIndex(pointList.size() - 1);
        }
        return currentPoint;
    }
}
