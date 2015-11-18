package cn.wehax.whatup.support.image;

/**
 * Created by howe on 15/3/27.
 */
public class Config {
    public static final String INTENT_VALUE_NEED_CROP = "INTENT_VALUE_NEED_CROP";
    public static final String INTENT_VALUE_ENCODINGTYPE = "INTENT_VALUE_ENCODINGTYPE";
    public static final String INTENT_VALUE_RETURNTYPE = "INTENT_VALUE_RETURNTYPE";
    public static final String INTENT_VALUE_MAXCOUNT = "INTENT_VALUE_MAXCOUNT";
    public static final String INTENT_VALUE_URI_LIST = "INTENT_VALUE_URI_LIST";
    public static final String INTENT_VALUE_CROP_WIDTH = "INTENT_VALUE_CROP_WIDTH";
    public static final String INTENT_VALUE_CROP_HEIGHT = "INTENT_VALUE_CROP_HEIGHT";
    public static final String INTENT_VALUE_CROP_PATH = "INTENT_VALUE_CROP_PATH";
    public static final String INTENT_VALUE_IS_PREVIEW = "INTENT_VALUE_IS_PREVIEW";

    public static final int ENCODINGTYPE_JPEG = 0;                  // Take a picture of type JPEG
    public static final int ENCODINGTYPE_PNG = 1;                   // Take a picture of type PNG

    public static final int RETURNTYPE_DATA = 0;              // Return base64 encoded string
    public static final int RETURNTYPE_URI = 1;              // Return file uri (content://media/external/images/media/2 for Android)
    public static final int RETURNTYPE_NATIVE_URI = 2;                    // On Android, this is the same as FILE_URI


    public static final int REQUEST_CODE_ALBUM_TO_SELECT_IMG = 0x2015;
    public static final int REQUEST_CODE_ALBUM_TO_IMG_CROP = REQUEST_CODE_ALBUM_TO_SELECT_IMG + 1;
    public static final int REQUEST_CODE_CALL_ALBUM = REQUEST_CODE_ALBUM_TO_IMG_CROP + 1;
    public static final int REQUEST_CODE_CAMERA_TO_SYSTEM_CAMERA = REQUEST_CODE_CALL_ALBUM + 1;
    public static final int REQUEST_CODE_CAMERA_TO_IMG_CROP = REQUEST_CODE_CAMERA_TO_SYSTEM_CAMERA + 1;
    public static final int REQUEST_CODE_CALL_CAMERA = REQUEST_CODE_CAMERA_TO_IMG_CROP + 1;
    public static final int REQUEST_CODE_CALL_PREVIEW = REQUEST_CODE_CALL_CAMERA + 1;
}
