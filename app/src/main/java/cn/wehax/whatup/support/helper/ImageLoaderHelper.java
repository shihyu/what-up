package cn.wehax.whatup.support.helper;

import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * 本助手提供有关图片加载的辅助方法
 * 规定：App中所有图片加载操作均使用Universal Image Loader完成
 */
public class ImageLoaderHelper {
    public static final String RES_IMAGE_PREFIX = "drawable://"; // 资源图片URI前缀

    /**
     * 初始化Universal Image Loader
     * <p/>
     * <p>注：本方法必须ImageLoader使用前调用</>
     *
     * @param context
     */
    public static void initImageLoader(Context context) {
        if (!ImageLoader.getInstance().isInited()) {
            // This configuration tuning is custom. You can tune every option, you may tune some of them,
            // or you can create default configuration by
            //  ImageLoaderConfiguration.createDefault(this);
            // method.
            ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
            config.threadPriority(Thread.NORM_PRIORITY - 2);
            config.denyCacheImageMultipleSizesInMemory();
            config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
            config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
            config.tasksProcessingOrder(QueueProcessingType.LIFO);
            config.writeDebugLogs(); // Remove for release app

            // Initialize ImageLoader with configuration.
            ImageLoader.getInstance().init(config.build());
        }
    }

    /**
     * 对于SD卡中的图片，构造合法的Uri
     *
     * @param imagePath SD卡中的图片的绝对路径
     */
    public static String buildSDCardImageUri(String imagePath) {
        if (imagePath == null)
            return null;

        return "file://" + imagePath;
    }

    /**
     * 对于Asset中的图片，构造合法的Uri
     *
     * @param imagePath Asset中的图片的绝对路径
     */
    public static String buildAssetImageUri(String imagePath) {
        if (imagePath == null)
            return null;

        return  "assets://" + imagePath;
    }

    /**
     * 对于资源图片，构造合法的Uri
     *
     * @param drawableRes 项目图片资源
     */
    public static String buildDrawableImageUri(int drawableRes) {
        return "drawable://" + drawableRes;
    }
}
