package cn.wehax.whatup.ar;

/**
 * Created by mayuhan on 15/6/27.
 */
public class TextureDataManager {

    private static final float[] originalData = {
            //U ,V
            0f, 0f,                 //lt
            0f, 1f,                 //lb
            1f, 0f,                 //rt
            1f, 1f,                 //rb
    };

    private static final float[] avatarBorderData = {
            //U ,V
            -0.05f, 0f,                 //lt
            -0.05f, 1f,                 //lb
            1.05f, 0f,                 //rt
            1.05f, 1f,                 //rb
    };

    private static final float[] avatarData = {
            //U ,V
            -0.2f, -0.125f,                 //lt
            -0.2f, 1.275f,                 //lb
            1.2f, -0.125f,                 //rt
            1.2f, 1.275f,                 //rb
    };

    private static final float[] statusBorderData = {
            //U ,V
            -0.045f, 0f,                 //lt
            -0.045f, 1f,                 //lb
            1.045f, 0f,                 //rt
            1.045f, 1f,                 //rb
    };

    private static final float[] statusThumbData = {
            //U ,V
            -0.075f, -0.025f,                 //lt
            -0.075f, 1.125f,                 //lb
            1.075f, -0.025f,                 //rt
            1.075f, 1.125f,                 //rb
    };

//    private static final float[] distanceData = {
//            //U ,V
//            0f, -3f,                 //lt
//            0f, 1f,                 //lb
//            1f, -3f,                 //rt
//            1f, 1f,                 //rb
//    };


    /**
     * @return 按素材原图比例做纹理映射
     */
    public static float[] getOriginalData() {
        return originalData.clone();
    }

    public static float[] getAvatarBorderData() {
        return avatarBorderData.clone();
    }

    public static float[] getAvatarData() {
        return avatarData.clone();
    }

    public static float[] getStatusBorderData() {
        return statusBorderData.clone();
    }

    public static float[] getStatusThumbData() {
        return statusThumbData.clone();
    }

    public static float[] getDistanceData(float width, float height) {

        final float[] data = originalData.clone();
        final float aspect = width / height;
        float h = 1f;
        float scaleY = 4.5f;
        float scaleX = scaleY / aspect;
        float originCenter = 0.5f;
        float paddingTop = 0.75f;
        float paddingBottom = 0.05f;

        // V轴 relative to top

        data[1] = originCenter - h / 2 - paddingTop * scaleY;
        data[5] = data[1];

        data[3] = originCenter + h / 2 + paddingBottom;
        data[7] = data[3];

        //U轴

        data[0] = originCenter - scaleX / 2;
        data[2] = data[0];

        data[4] = originCenter + scaleX / 2;
        data[6] = data[4];

        return data;
    }


    public static float[] getContentData(float width, float height, int line) {
        line = line % 3;
        float[] data = originalData.clone();
        final float aspect = width / height;
        float h = 1f;
        float scaleY = 4.5f;
        float scaleX = scaleY / aspect;
        float paddingTop = line * h;

        data[1] = -paddingTop;
        data[5] = data[1];
        data[3] = scaleY - paddingTop - h;
        data[7] = data[3];

        data[4] = scaleX;
        data[6] = data[4];


        return data;
    }
}
