package cn.wehax.whatup.support.util;

import android.app.Activity;
import android.widget.Toast;

/**
 * 提供与Toast有关的实用方法
 */
public class ToastUtils {
    public static int LENGHT_MID = 2000; // Toast显示时间2秒

    public static void showToast(Activity ctx, int resID, int duration) {
        showToast(ctx, ctx.getString(resID), duration);
    }

    public static void showToast(Activity ctx, int resID) {
        showToast(ctx, resID, Toast.LENGTH_SHORT);
    }

    public static void showToast(Activity ctx, String text, int duration) {
        Toast.makeText(ctx, text, duration).show();
    }
    public static void showToast(final Activity activity, final String msg) {
        // 判断当前的线程到底是主线程还是子线程
        if ("main".equals(Thread.currentThread().getName())) {
            showToast(activity, msg, Toast.LENGTH_SHORT);
        } else {
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    showToast(activity, msg, Toast.LENGTH_SHORT);
                }
            });
        }

    }
}
