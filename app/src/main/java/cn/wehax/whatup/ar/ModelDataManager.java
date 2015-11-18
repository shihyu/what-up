package cn.wehax.whatup.ar;

/**
 * Created by mayuhan on 15/6/27.
 */
public class ModelDataManager {

    private static final float[] mOriginalSquare = {
            // X, Y, Z,
            -1f, 1f, 1f,   //lt
            -1f, -1f, 1f,   //lb
            1f, 1f, 1f,    //rt
            1f, -1f, 1f,    //rb
    };

    private static final float[] mAvatarBorderSquare = {
            // X, Y, Z,
            -0.9f, 1f, 1f,   //lt
            -0.9f, -1f, 1f,   //lb
            0.9f, 1f, 1f,    //rt
            0.9f, -1f, 1f,    //rb
    };

    private static final float[] mAvatarSquare = {
            // X, Y, Z,
            -1f, 1f, 1f,   //lt
            -1f, -1f, 1f,   //lb
            1f, 1f, 1f,    //rt
            1f, -1f, 1f,    //rb
    };

    private static final float[] mStatusBorderSquare = {
            // X, Y, Z,
            -0.91f, 1f, 1f,   //lt
            -0.91f, -1f, 1f,   //lb
            0.91f, 1f, 1f,    //rt
            0.91f, -1f, 1f,    //rb
    };

    private static final float[] mStatusThumbnailSquare = {
            -1.09f, 1.43f, 1f,   //lt
            -1.09f, -0.77f, 1f,   //lb
            1.11f, 1.43f, 1f,    //rt
            1.11f, -0.77f, 1f,    //rb
    };


    public static float[] getAvatarBorderVertexData() {
        return mAvatarBorderSquare.clone();
    }

    public static float[] getStatusBorderVertexData() {
        return mStatusBorderSquare.clone();
    }

    public static float[] getAvatarVertexData() {
        return mAvatarSquare.clone();
    }

    public static float[] getStatusVertexData() {
        return mStatusThumbnailSquare;
    }

    public static float[] getOriginalSquare() {
        return mOriginalSquare;
    }

}
