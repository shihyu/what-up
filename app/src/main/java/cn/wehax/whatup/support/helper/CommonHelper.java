package cn.wehax.whatup.support.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.util.List;

import cn.wehax.util.NetworkUtils;
import cn.wehax.whatup.R;
import cn.wehax.whatup.config.PreferenceKey;
import cn.wehax.whatup.framework.model.WXException;
import cn.wehax.whatup.support.util.PackageUtils;
import cn.wehax.whatup.support.util.PreferencesUtils;
import cn.wehax.whatup.support.util.ToastUtils;
import cn.wehax.whatup.vp.imageviewer.ImageViewerActivity;

/**
 * 本助手提供App常用方法
 */
public class CommonHelper {
    /**
     * 使用Toast显示错误信息
     *
     * @param context
     * @param msg
     */
    public static void showErrorMsg(Activity context, String msg) {
        ToastUtils.showToast(context, msg, ToastUtils.LENGHT_MID);
    }

    public static void showErrorMsg(Activity context, WXException e) {
        showErrorMsg(context, e.getDescription());
    }


    /**
     * 如果需要显示引导页面，返回true
     */
    public static boolean isNeedShowGuide(Context context) {
        PreferencesUtils.setDefaultPreferenceName();

        // 检查引导页面显示时的App版本号
        int versionCodeWhenGuideShow = PreferencesUtils.getInt(context, PreferenceKey.VERSION_CODE_WHEN_GUIDE_SHOW, -1);

        // 如果首次启动App，此时SharePreference未保存引导页面相关信息，显示引导页
        if (versionCodeWhenGuideShow == -1) {
            return true;
        }

        // 如果App版本升级，版本升级后，再次显示引导页
        if (versionCodeWhenGuideShow != PackageUtils.getAppVersionCode(context)) {
            return true;
        } else {
            // 如果引导页面已经显示过，则不再显示；否则，显示
            boolean isGuideShowed = false;
            isGuideShowed = PreferencesUtils.getBoolean(context, PreferenceKey.GUIDE_SHOWED, false);
            return !isGuideShowed;
        }
    }

    /**
     * 记录引导页已显示
     *
     * @param context
     */
    public static void recordGuideShowed(Context context) {
        PreferencesUtils.setDefaultPreferenceName();
        PreferencesUtils.putInt(context, PreferenceKey.VERSION_CODE_WHEN_GUIDE_SHOW, PackageUtils.getAppVersionCode(context));
        PreferencesUtils.putBoolean(context, PreferenceKey.GUIDE_SHOWED, true);
    }

    /**
     * 检查网络是否可用
     *
     * @param activity
     * @return 可用返回true；不可用返回false，并且弹toast提示错误信息
     */
    public static boolean checkNetworkAvailability(Activity activity) {
        if(NetworkUtils.isNetworkAvailable(activity)){
            return true;
        }else{
            ToastUtils.showToast(activity, R.string.network_not_available);
            return false;
        }
    }

    /**
     * 浏览单个图片
     * @param act
     * @param imgUrl
     */
    public static void viewSingleImage(Activity act, String imgUrl) {
        String[] array = new String[]{imgUrl};
        viewMultiImage(act, array, 0);
    }

    /**
     * 浏览多个图片
     * @param act
     * @param imgUrls
     * @param position
     */
    public static void viewMultiImage(Activity act, List<String> imgUrls, int position) {
        String[] array = new String[imgUrls.size()];
        imgUrls.toArray(array);
        viewMultiImage(act, array, position);
    }

    public static void viewMultiImage(Activity act,
                                      String[] imgUrls, int position) {
        Intent intent = new Intent(act, ImageViewerActivity.class);
        intent.putExtra(ImageViewerActivity.KEY_IMG_URLS, imgUrls);
        intent.putExtra(ImageViewerActivity.KEY_POSITON, position);
        act.startActivity(intent);
    }
}
