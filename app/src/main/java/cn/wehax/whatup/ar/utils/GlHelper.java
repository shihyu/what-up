package cn.wehax.whatup.ar.utils;

import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLU;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;

import cn.wehax.whatup.ar.Config;
import cn.wehax.whatup.ar.marker.base.BorderSprite;
import cn.wehax.whatup.ar.marker.base.Geometry;

/**
 * Created by mayuhan on 15/6/11.
 */
public class GlHelper {

    private final static String TAG = "GlHelper";

    /**
     * 检查GL内部报错
     *
     * @param action
     */
    public static void checkGlError(String action) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, action + ": glError " + error);
            throw new RuntimeException(action + ": glError " + error);
        }
    }

    /**
     * 新建管线程序
     *
     * @param vertexSource   vertexShader glsl
     * @param fragmentSource fragmentShader glsl
     * @return 管线程序id
     */
    public static int createProgram(String vertexSource, String fragmentSource) {

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }

        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (fragmentShader == 0) {
            return 0;
        }

        return createProgram(vertexShader, fragmentShader);

    }

    /**
     * @param vertexShader   vertexShader handle
     * @param fragmentShader fragmentShader handle
     * @return
     */
    public static int createProgram(int vertexShader, int fragmentShader) {

        int program = GLES20.glCreateProgram();
        if (program != 0) {

            GLES20.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");

            GLES20.glAttachShader(program, fragmentShader);
            checkGlError("glAttachShader");

            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e(TAG, "Could not link program: ");
                Log.e(TAG, GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }


    private static int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                Log.e(TAG, "Could not compile shader " + shaderType + ":");
                Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }


    /**
     * 获得摄像头支持的最高预览分辨率index
     *
     * @param parameters
     * @return index
     */
    public static int maxPreviewSizeIndex(Camera.Parameters parameters) {
        int maxIndex = 0;
        int quality = 0;
        Camera.Size curSize;
        List<Camera.Size> supportSize = parameters.getSupportedPictureSizes();
        for (int i = 0; i < supportSize.size(); i++) {
            curSize = supportSize.get(i);
            if (curSize.width * curSize.height > quality) {
                quality = curSize.width * curSize.height;
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    public static FloatBuffer createBuffer(int length) {
        return ByteBuffer.allocateDirect(Config.VERTEXS_BUFFER_LENGHT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
    }

    /**
     * Convert the 4D input into 3D space (or something like that, otherwise the gluUnproject values are incorrect)
     *
     * @param v 4D input
     * @return 3D output
     * @author http://stackoverflow.com/users/1029225/mh
     */
    public static float[] fixW(float[] v) {
        float w = v[3];
        for (int i = 0; i < 4; i++)
            v[i] = v[i] / w;
        return v;
    }

    /**
     * check if a given point in space collides with a given object center  with a given radius
     * based on ios code from: http://blog.nova-box.com/2010/05/iphone-ray-picking-glunproject-sample.html
     *
     * @param point  point to check for collision with object x,y,z
     * @param center center of the object x,y,z
     * @param radius
     * @return true on collision, false on no collision
     */
    public static Boolean poinSphereCollision(float[] point, float[] center, float radius) {

        return ((point[0] - center[0]) * (point[0] - center[0]) +
                (point[1] - center[1]) * (point[1] - center[1]) +
                (point[2] - center[2]) * (point[2] - center[2]) < (radius * radius));
    }


    /**
     * 点击事件的交叉测试
     *
     * @param x      touchEvent.getX();
     * @param y      touchEvent.getY();
     * @param sprite target
     * @return isCollision
     */
    public static boolean checkCollision(float x, float y, BorderSprite sprite) {

//        y = mViewport[3] - y;

        float[] nearPoint = {0f, 0f, 0f, 0f};
        float[] farPoint = {0f, 0f, 0f, 0f};
        float[] rayVector = {0f, 0f, 0f};

        //Retreiving position projected on near plane
        GLU.gluUnProject(x, y, -1f, sprite.getModelViewMtx(), 0, sprite.getProject(), 0, sprite.getViewPort(), 0, nearPoint, 0);

        //Retreiving position projected on far plane
        GLU.gluUnProject(x, y, 1f, sprite.getModelViewMtx(), 0, sprite.getProject(), 0, sprite.getViewPort(), 0, farPoint, 0);

        // extract 3d Coordinates put of 4d Coordinates
        nearPoint = GlHelper.fixW(nearPoint);
        farPoint = GlHelper.fixW(farPoint);

        //Processing ray vector
        rayVector[0] = farPoint[0] - nearPoint[0];
        rayVector[1] = farPoint[1] - nearPoint[1];
        rayVector[2] = farPoint[2] - nearPoint[2];

        // calculate ray vector length
        float rayLength = (float) Math.sqrt((rayVector[0] * rayVector[0]) + (rayVector[1] * rayVector[1]) + (rayVector[2] * rayVector[2]));

        //normalizing ray vector
        rayVector[0] /= rayLength;
        rayVector[1] /= rayLength;
        rayVector[2] /= rayLength;


        final Geometry.Point rayPoint = new Geometry.Point(nearPoint[0], nearPoint[1], nearPoint[2]);
        final Geometry.Vector rayVec = new Geometry.Vector(rayVector[0], rayVector[1], rayVector[2]);
        final Geometry.Ray touchRay = new Geometry.Ray(rayPoint, rayVec);
        final float[] center = sprite.getCenter();
        final Geometry.Point spriteCenter = new Geometry.Point(center[0], center[1], center[2]);

        if (Geometry.distancBetween(spriteCenter, touchRay) < sprite.getRadius()) {
            return true;
        }


//        final Geometry.Ray normalizedRay = new Geometry.Ray(new Geometry.Point(sprite.getCenter()[0],))

//        float[] collisionPoint = {0f, 0f, 0f};
//        float[] objectCenter = sprite.getCenter();
//
//        //Iterating over ray vector to check for collisions
//        for (int i = 0; i < Config.RAY_ITERATIONS; i++) {
//            collisionPoint[0] = rayVector[0] * rayLength / Config.RAY_ITERATIONS * i;
//            collisionPoint[1] = rayVector[1] * rayLength / Config.RAY_ITERATIONS * i;
//            collisionPoint[2] = rayVector[2] * rayLength / Config.RAY_ITERATIONS * i;
//
//            if (GlHelper.poinSphereCollision(collisionPoint, objectCenter, Config.COLLISION_RADIUS)) {
//                return true;
//            }
//        }
        return false;
    }


}
