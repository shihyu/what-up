package cn.wehax.whatup.support.util;

import android.text.TextUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by sanchibing on 2015/7/8.
 */
public class StringUtils {
    public static List<String> strToList(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            return Arrays.asList(msg.split("#"));
        }
        return null;
    }
}
