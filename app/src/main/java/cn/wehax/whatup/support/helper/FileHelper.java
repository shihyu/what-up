package cn.wehax.whatup.support.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.wehax.util.ImageUtil;
import cn.wehax.util.SystemUtil;

/**
 * 本助手提供App常用方法
 */
public class FileHelper {
    public static final String TAG = "FileHelper";

    /**
     * 保存拍照图片的临时文件路径（相对路径）
     */
    private static final String TEMP_FILE_NAME = "temp.jpeg";


    private static File createStorageFile(Context context) {
        File file;
        // 如果SD卡可用，保存到SD卡，否则保存到项目数据目录
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                    + "whatup" + File.separator + "picture" + File.separator + TEMP_FILE_NAME;

            file = new File(path);
        } else {
            String path = context.getApplicationInfo().dataDir + File.separator + "picture"
                    + File.separator + TEMP_FILE_NAME;
            file = new File(path);
        }

        // 如果父目录不存在，创建之
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            Log.e(TAG, "savePictureToTempFile/mkdirs");
            parentDir.mkdirs();
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "savePictureToTempFile/createNewFile");
                e.printStackTrace();
            }
        }
        return file;
    }

    public static String savePictureToTempFile(Context context, Bitmap bitmap) {
        File file = createStorageFile(context);

        // 缩放图片（保持图片比例）
        float scale = SystemUtil.getScreenWidth(context) * 1.2f / bitmap.getWidth();
        bitmap = ImageUtil.scaleImage(bitmap, scale, scale);

        FileOutputStream outStream = null;
        try {
            // 打开指定文件对应的输出流
            outStream = new FileOutputStream(file);
            // 把位图输出到指定文件中
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85,
                    outStream);
            outStream.flush();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
            return null;
        }
        return file.getAbsolutePath();
    }

}
