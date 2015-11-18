package cn.wehax.whatup.ar;

/**
 * Created by mayuhan on 15/7/9.
 */
public class Config {

    public static final float Z_NEAR = 0.1f;
    public static final float Z_FAR = 100.0f;
    public static final float CAMERA_Z = 1f;

    public static float MARKERS_DISTRIBUTE_DISTANCE = 20f;
    public static float MARKERS_LEVEL_RANGE = 0.2f;
    public static float NEAR_Y = -3f;
    public static float FAR_Y = 0.5f;

    public static int BYTES_PER_FLOAT = 4;
    public static int VERTEXS_BUFFER_LENGHT = 3 * 4 * BYTES_PER_FLOAT;

    public static int BORDER_TEXTURE_BUFFER_LENGTH = 2 * 4 * BYTES_PER_FLOAT;
    public static int IMAGE_TEXTURE_BUFFER_LENGTH = 2 * 4 * BYTES_PER_FLOAT;
    public static int CONTENT_TEXTURE_BUFFER_LENGTH = 2 * 4 * BYTES_PER_FLOAT;


    public static int RAY_ITERATIONS = 1000;
    public static float COLLISION_RADIUS = 0.1f;

}
