package cn.wehax.whatup.support.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * 提供与Activity有关的实用方法
 */
public class ActivityUtils {

    /**
     * 跳转到指定活动页面
     * @param activity
     * @param targetActivityClass
     */
    public static void moveToActivity(Activity activity, Class targetActivityClass) {
        moveToActivity(activity, targetActivityClass, null);
    }

    /**
     * 携带数据，跳转到指定活动页面
     * @param activity
     * @param targetActivityClass
     * @param bundle
     */
    public static void moveToActivity(Activity activity, Class targetActivityClass, Bundle bundle) {
        if (activity == null || activity.isFinishing()) {
            return;
        }

        Intent intent = new Intent(activity, targetActivityClass);
        if (bundle != null) {
            intent.putExtras(bundle);
        }

        activity.startActivity(intent);
    }

    /**
     * 跳转到指定活动页面，请求指定数据
     * @param activity
     * @param targetActivityClass
     */
    public static void moveToActivityForResult(Activity activity, Class targetActivityClass, int requestCode) {
        moveToActivityForResult(activity, targetActivityClass, requestCode, null);
    }

    /**
     * 携带数据，跳转到指定活动页面，请求指定数据
     * @param activity
     * @param targetActivityClass
     * @param requestCode
     * @param bundle
     */
    public static void moveToActivityForResult(Activity activity, Class targetActivityClass, int requestCode, Bundle bundle) {
        if (activity == null || activity.isFinishing()) {
            return;
        }

        Intent intent = new Intent(activity, targetActivityClass);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        activity.startActivityForResult(intent, requestCode);
    }
}
