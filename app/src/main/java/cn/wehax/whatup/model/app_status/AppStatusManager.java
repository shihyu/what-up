package cn.wehax.whatup.model.app_status;

import com.google.inject.Singleton;

/**
 * 保存App当前状态
 */
@Singleton
public class AppStatusManager {
    public static final String TAG = "AppStatusManager";

    /**
     * App正在运行
     */
    public static final int APP_STATUS_RUNING = 1;
    /**
     * App在后台运行
     */
    public static final int APP_STATUS_BACKGROUND = 2;
    /**
     * App处于关闭状态
     */
    public static final int APP_STATUS_CLOSED = 3;

    /**
     * App当前状态
     */
    int appStatus = APP_STATUS_CLOSED;

    public int getAppStatus() {
        return appStatus;
    }

    public void setAppStatus(int appStatus) {
        this.appStatus = appStatus;
    }
}
