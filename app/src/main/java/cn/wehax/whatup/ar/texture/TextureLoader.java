package cn.wehax.whatup.ar.texture;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.HashMap;

/**
 * Created by mayuhan on 15/6/24.
 */
public class TextureLoader {

    private static HashMap<Integer, Integer> resTextureCache = new HashMap<Integer, Integer>();
    private static HashMap<String, Integer> pathTextureCache = new HashMap<String, Integer>();
    private static HashMap<String, Integer> urlTextureCache = new HashMap<String, Integer>();
//    private static HashMap<String, Integer> stringTextureCacahe = new HashMap<String, Integer>();

    /**
     * 加载一个纹理
     *
     * @param context
     * @param resourceId
     * @return 纹理id
     */
    public static int load(Context context, int resourceId) {
        if (resTextureCache.containsKey(resourceId) &&
                GLES20.glIsTexture(resTextureCache.get(resourceId))) {
            return resTextureCache.get(resourceId);
        } else {
            final int texture = loadTexture(context, resourceId);
            resTextureCache.put(resourceId, texture);
            return texture;
        }
    }

    public static int load(String path) {
        if (pathTextureCache.containsKey(path)) {
            GLES20.glIsTexture(pathTextureCache.get(path));
            return pathTextureCache.get(path);
        } else {
            final int texture = loadTexture(path);
            pathTextureCache.put(path, texture);
            return texture;
        }
    }

    public static void load(final GLSurfaceView surfaceView, final String url,
                            final LoadingNetTextureListener listener) {

        if (urlTextureCache.containsKey(url) && GLES20.glIsTexture(urlTextureCache.get(url))) {
            listener.onLoadingComplete(url, null, urlTextureCache.get(url));
        } else {
            ImageLoader.getInstance().loadImage(url, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(final String imageUri, final View view) {
                    surfaceView.queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            listener.onLoadingStarted(imageUri, view);
                        }
                    });

                }

                @Override
                public void onLoadingFailed(final String imageUri, final View view, final FailReason failReason) {
                    surfaceView.queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            listener.onLoadingFailed(imageUri, view, failReason);
                        }
                    });

                }

                @Override
                public void onLoadingComplete(final String imageUri, final View view, final Bitmap loadedImage) {

                    surfaceView.queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                int textureHandle = loadTextureByBitmap(loadedImage);
                                urlTextureCache.put(imageUri, textureHandle);
                                listener.onLoadingComplete(imageUri, view, textureHandle);

                            } catch (Exception e) {
                                listener.onLoadingFailed(imageUri, view, new FailReason(FailReason.FailType.IO_ERROR, e));
                            }

                        }
                    });

                }

                @Override
                public void onLoadingCancelled(final String imageUri, final View view) {
                    surfaceView.queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            listener.onLoadingCancelled(imageUri, view);
                        }
                    });

                }
            });
        }


    }

    public static int load(Bitmap bitmap) {
        return loadTextureByBitmap(bitmap);
    }

    private static int loadTextureByBitmap(Bitmap bitmap) {


        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);
        if (textureHandle[0] != 0) {

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                    GLES20.GL_LINEAR);

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                    GLES20.GL_LINEAR);

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        }
        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        }
        return textureHandle[0];
    }


    private static int loadTexture(String filePath) {

        final BitmapFactory.Options options = new BitmapFactory.Options();

        options.inScaled = false;

        final Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

        final int textureHandle = loadTextureByBitmap(bitmap);

        bitmap.recycle();

        return textureHandle;

    }

    private static int loadTexture(Context context, int resId) {

        final BitmapFactory.Options options = new BitmapFactory.Options();

        options.inScaled = false;

        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId, options);

        final int textureHandle = loadTextureByBitmap(bitmap);

        bitmap.recycle();

        return textureHandle;
    }


    /**
     * 将文字解析成纹理
     *
     * @param activity
     * @param surfaceView
     * @param str
     * @param textColor
     * @param listener    @return
     */
    public static void decodeStringToTexture(final Activity activity, final GLSurfaceView surfaceView,
                                             final String str, final String textColor,
                                             final DecodeStringTextureListener listener) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Button btn = new Button(activity);
                btn.setText(str);
                btn.setGravity(Gravity.TOP);
                btn.setSingleLine(true);
                btn.setBackgroundDrawable(null);
                btn.setPadding(0, 0, 0, 0);
                final int bgWidth = (int) Layout.getDesiredWidth(str, btn.getPaint());
                final int bgHeight = (int) (btn.getPaint().descent() - btn.getPaint().ascent());
                btn.setTextColor(Color.parseColor(textColor));
                final Bitmap bitmap = genStringBitmapWithButton(btn, bgWidth, bgHeight);


                if (surfaceView.isShown()) {
                    surfaceView.queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            final int textureHandle = loadTextureByBitmap(bitmap);
                            bitmap.recycle();
                            listener.onDone(textureHandle, bgWidth, bgHeight);
                        }
                    });
                }

            }
        });
    }

    public static Bitmap decodeStringToBitmap(final Activity activity, String str) {
        Button btn = new Button(activity);
        btn.setText(str);
        btn.setSingleLine(true);
        btn.setPadding(0, 0, 0, 0);
        final int bgWidth = (int) Layout.getDesiredWidth(str, btn.getPaint());
        final int bgHeight = (int) (btn.getPaint().descent() - btn.getPaint().ascent());
        return genStringBitmapWithButton(btn, bgWidth, bgHeight);
    }

    /**
     * 将按钮加载为Texture,可以ui线程调用，在GLSurfaceView queue 线程回调
     *
     * @param seedBtn
     * @param listener
     * @param surfaceView
     * @param activity
     */
    public static void loadBtnToTexture(final Button seedBtn,
                                        final DecodeStringTextureListener listener,
                                        final GLSurfaceView surfaceView, Activity activity) {


        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final String str = (String) seedBtn.getText();
                final int bgWidth = (int) Layout.getDesiredWidth(str, seedBtn.getPaint());
                final int bgHeight = (int) (seedBtn.getPaint().descent() - seedBtn.getPaint().ascent());
                final Bitmap bm = genStringBitmapWithButton(seedBtn, bgWidth, bgHeight);
                surfaceView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        final int textureHandle = loadTextureByBitmap(bm);
                        listener.onDone(textureHandle, bgWidth, bgHeight);
                    }
                });
            }
        });


//        return loadTextureByBitmap(bm);
    }


    public static Bitmap genStringBitmapWithButton(Button seedBtn, int bgWidth, int bgHeight) {
        seedBtn.setDrawingCacheEnabled(true);
        seedBtn.layout(0, 0, bgWidth, bgHeight);
        seedBtn.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(seedBtn.getDrawingCache());
        seedBtn.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public static void clearStaticTexture() {
        for (int texture : resTextureCache.values()) {
            if (GLES20.glIsTexture(texture)) {
                GLES20.glDeleteTextures(1, new int[]{texture}, 0);
            }
        }
        resTextureCache.clear();
    }
}
