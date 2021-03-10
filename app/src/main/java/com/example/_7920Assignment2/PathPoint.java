package com.example._7920Assignment2;

/* store coordinates of a paint on a path*/
public class PathPoint {
        int x;
        int y;
        int PointIndex = 0;

        // constructor
        public PathPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        //getters and setters for Path Point vlas fields
        public int getX() {
            return x;
        }

        public int getY() {
            return y;
    }

        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getPointIndex() {
            return PointIndex;
        }

        public void setPointIndex(int pointIndex) {
            this.PointIndex = pointIndex;
        }

}
