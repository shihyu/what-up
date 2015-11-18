package cn.wehax.whatup.support.image;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by howe on 15/3/27.
 */
public class ImageUtils {


//    public static String scaleBitmap(Bitmap bitmap,int targetWidth,int targetHeight){
//        int width = bitmap.getWidth();
//        int height = bitmap.getHeight();
//        float scale = 1.0f;
//        float widthScale = (float)targetWidth / (float)width;
//
//        float heightScale = (float)targetHeight / (float)height;
//
//        if(widthScale > heightScale){
//            scale = heightScale;
//        }else{
//            scale = widthScale;
//        }
//
//    }

    public static Bitmap scaleBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
//        int mTargetWidth = screenWidth * 3;
//        int mTargetHeight = screenHeight * 3;
        //TODO:这里现在默认是480 x 800 ，extractThumbnail当设置的分辨率大于图片本身，会有黑边，这里需要自己实现！！
//        if (mTargetWidth > 0 && mTargetHeight > 0) {
//
//            bitmap = ThumbnailUtils.extractThumbnail(bitmap, mTargetWidth, mTargetHeight);
//            Log.e("IMAGE", "targetw：" + mTargetWidth + "  targeth：" + mTargetHeight);
//            Log.e("IMAGE", "缩放宽：" + bitmap.getWidth() + "  高：" + bitmap.getHeight());
//        } else {

        bitmap = ThumbnailUtils.extractThumbnail(bitmap, 480, 800);
//        }

        return bitmap;
    }

    public static String toBase64(Bitmap bitmap, int encodingType, int quality) {
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        if (encodingType == Config.ENCODINGTYPE_PNG) {
            format = Bitmap.CompressFormat.PNG;
            quality = 100;
        }
        ByteArrayOutputStream jpeg_data = new ByteArrayOutputStream();
        try {
            if (bitmap.compress(format, quality, jpeg_data)) {
                byte[] code = jpeg_data.toByteArray();
                String js_out = Base64.encodeToString(code, Base64.DEFAULT);

                code = null;
                return js_out;
            }
        } catch (Exception e) {
            //                this.failPicture("Error compressing image.");
        }
        return null;
    }

    public static byte[] toBytes(Bitmap bitmap, int quality) {
        Bitmap.CompressFormat format = Bitmap.CompressFormat.PNG;
        ByteArrayOutputStream jpeg_data = new ByteArrayOutputStream();
        try {
            if (bitmap.compress(format, quality, jpeg_data)) {
                byte[] code = jpeg_data.toByteArray();
                return code;
            }
        } catch (Exception e) {
            //                this.failPicture("Error compressing image.");
        }
        return null;
    }

    public static String getLocalImagePath(Context context) {
        File file = new File(
                Environment.getExternalStorageDirectory() + "/android/data/" + context.getPackageName() + "/photo");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getPath();
    }

    public static String getImageName(int encodingType) {
        String suffix = ".jpg";
        if (encodingType == Config.ENCODINGTYPE_PNG) {
            suffix = ".png";
        }
        return "img_" + System.currentTimeMillis() + suffix;
    }

    public static boolean saveImage(Bitmap bitmap, String imagePath, int quality, int encodingType) {
        FileOutputStream out = null;
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        if (encodingType == Config.ENCODINGTYPE_PNG) {
            format = Bitmap.CompressFormat.PNG;
        }
        try {
            File file = new File(imagePath);
            if (file.exists()) {
                file.delete();
            }

            out = new FileOutputStream(file, false);
            bitmap.compress(format, quality, out);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String convertDataFromUri(Activity activity, Uri uri, int quality, int width, int height, int encodingType) {
        if (width <= 0 || height <= 0) {
            width = 480;
            height = 800;
        }
        Bitmap bitmap = null;
        try {
            byte[] array = getImageByteArrayFromUri(activity, uri);
            if (array == null) {
                return null;
            }
            bitmap = convertBitmapFromByteArray(array, width, height);
            int digree = 0;
            try {

                ExifInterface exif = new ExifInterface(uri.getPath());
                int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);

                switch (ori) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        digree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        digree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        digree = 270;
                        break;
                    default:
                        digree = 0;
                        break;
                }
            } catch (Exception e) {

            }
            if (digree != 0) {
                Matrix m = new Matrix();
                m.postRotate(digree);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
            }
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height);
            String result = ImageUtils.toBase64(bitmap, encodingType, quality);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
        return null;
    }

    public static Bitmap doImageFromPick(Activity activity, Uri uri, String imagePath) {

        Bitmap bitmap = null;

        try {

            byte[] array = getImageByteArrayFromUri(activity, uri);
            if (array == null) {
                return null;
            }
            bitmap = convertBitmapFromByteArray(array, 480, 800);

            int digree = 0;
            try {

                ExifInterface exif = new ExifInterface(uri.getPath());
                int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);

                switch (ori) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        digree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        digree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        digree = 270;
                        break;
                    default:
                        digree = 0;
                        break;
                }
            } catch (Exception e) {

            }
            if (digree != 0) {
                Matrix m = new Matrix();
                m.postRotate(digree);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
            }

            saveImage(bitmap, imagePath, 50, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public static Bitmap getImageByUri(Activity activity, Uri uri) {
        Bitmap bitmap;
        try {
            byte[] array = getImageByteArrayFromUri(activity, uri);
            Log.e("crop", "array = " + array);
            if (array == null) {
                return null;
            }
            bitmap = convertBitmapFromByteArray(array, 480, 800);
            Log.e("crop", "bitmap1=" + bitmap);
            int digree = 0;
            try {

                ExifInterface exif = new ExifInterface(uri.getPath());
                int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);

                switch (ori) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        digree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        digree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        digree = 270;
                        break;
                    default:
                        digree = 0;
                        break;
                }
            } catch (Exception e) {

            }
            if (digree != 0) {
                Matrix m = new Matrix();
                m.postRotate(digree);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
            }
        } catch (Exception e) {
            Log.e("crop", "Exception:" + e.toString());
            return null;
        }
        Log.e("crop", "bitmap2 = " + bitmap);
        return bitmap;
    }

    public static byte[] getImageByteArrayFromUri(Activity activity, Uri uri) throws Exception {

        ContentResolver resolver = activity.getContentResolver();

        return readByteArryFromStream(resolver.openInputStream(uri));
    }

    public static byte[] readByteArryFromStream(InputStream inStream) throws Exception {
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return data;

    }

    /**
     * 根据路径获得突破并压缩返回bitmap用于显示
     *
     * @return
     */
    public static Bitmap convertBitmapFromByteArray(byte[] bytes, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

    /**
     * 计算图片的缩放值
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    /**
     * 根据原图生成圆形图，要求SDK 18，若低于18会有明显锯齿
     *
     * @param bitmap
     * @return
     */
    public static Bitmap toOvalBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        }
        paint.setAntiAlias(true);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        canvas.drawOval(rectF, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        paint.setFilterBitmap(true);
        canvas.drawBitmap(bitmap, rect, rectF, paint);
        return output;
    }
}
