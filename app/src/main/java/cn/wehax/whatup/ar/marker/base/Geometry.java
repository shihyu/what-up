package cn.wehax.whatup.ar.marker.base;


import android.opengl.Matrix;
import android.util.FloatMath;
import android.util.Log;

/**
 * Created by mayuhan on 15/7/2.
 * 为了做相交测试的各种神奇几何对象。
 */
public class Geometry {


    private static String TAG = "Geometry";

    public static class Vector {
        public final float x, y, z;

        public Vector(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Vector crossProduct(Vector other) {
            return new Vector(
                    (y * other.z) - (z * other.y),
                    (z * other.x) - (x * other.z),
                    (x * other.y) - (y * other.x)
            );
        }

        public float length() {
            return FloatMath.sqrt(x * x + y * y + z * z);
        }
    }

    public static class Point {
        public final float x, y, z;

        public Point(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Point translate(Vector v) {
            return new Point(x + v.x, y + v.y, z + v.z);
        }
    }


    public static class Ray {
        public final Point point;
        public final Vector vector;

        public Ray(Point point, Vector vector) {
            this.vector = vector;
            this.point = point;
        }
    }

    public static class Sphere {
        public final Point center;
        public final float radius;

        public Sphere(Point center, float radius) {
            this.center = center;
            this.radius = radius;
        }
    }

    public static Vector vectorBetween(Point from, Point to) {
        return new Vector(from.x - to.x, from.y - to.y, from.z - to.z);
    }

    public static boolean intersects(Sphere sphere, Ray ray) {
        return distancBetween(sphere.center, ray) < sphere.radius;
    }

    public static float distancBetween(Point point, Ray ray) {
        Vector p1ToPoint = vectorBetween(ray.point, point);
        Vector p2ToPoint = vectorBetween(point.translate(ray.vector), point);
        float areaOfTriangleTimesTwo = p1ToPoint.crossProduct(p2ToPoint).length();
        float lengthOfBase = ray.vector.length();
        float distanceFromPointToRay = areaOfTriangleTimesTwo / lengthOfBase;
        return distanceFromPointToRay;
    }



    public static Geometry.Ray converNormalized2DPointToRay(float normalizedX, float normalizedY, float[] intvertedViewProjectMatrix) {

        final float[] nearPointNdc = {normalizedX, normalizedY, -1, 1};
        final float[] farPointNdc = {normalizedX, normalizedY, 1, 1};
        final float[] nearPointWorld = new float[4];
        final float[] farPointWorld = new float[4];

        Matrix.multiplyMV(nearPointWorld, 0, intvertedViewProjectMatrix, 0, nearPointNdc, 0);
        Matrix.multiplyMV(farPointWorld, 0, intvertedViewProjectMatrix, 0, farPointNdc, 0);

        divideByW(nearPointWorld);
        divideByW(farPointWorld);

        Point nearPoint = new Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]);
        Point farPoint = new Point(farPointWorld[0], farPointWorld[1], farPointWorld[2]);

        Log.d(TAG, nearPoint.x + "," + nearPoint.y + "," + nearPoint.z);
        Log.d(TAG, farPoint.x + "," + farPoint.y + "," + farPoint.z);

        return new Ray(nearPoint, vectorBetween(nearPoint, farPoint));
    }

    private static void divideByW(float[] vector) {
        vector[0] /= vector[3];
        vector[1] /= vector[3];
        vector[2] /= vector[3];
    }


}
