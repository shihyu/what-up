package cn.wehax.whatup.support.helper;

import android.util.Log;

import com.avos.avoscloud.AVException;

import cn.wehax.whatup.framework.model.WXException;

/**
 * 本助手提供Log常用方法
 */
public class LogHelper {

    public static void e(String tag, String msg, AVException e) {
        Log.e(tag, msg + "[code=" + e.getCode() + ",message=" + e.getMessage() + "]");
    }

    public static void e(String tag, String msg, WXException e) {
        Log.e(tag, msg + e.toString());
    }
}
