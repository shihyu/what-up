package cn.wehax.whatup;

import android.app.Application;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVOSCloud;

import cn.wehax.whatup.config.Constant;
import cn.wehax.whatup.config.Debug;
import cn.wehax.whatup.support.helper.ImageLoaderHelper;

public class WhatUpApplication extends Application {
    private static WhatUpApplication application;

    @Override
    public void onCreate() {
        super.onCreate();

        application = this;

        initLeanCloud();
        ImageLoaderHelper.initImageLoader(this);
    }

    private void initLeanCloud(){
        AVOSCloud.initialize(this, Constant.LEANCLOUD_APPID,
                Constant.LEANCLOUD_APPKEY);
        AVCloud.setProductionMode(Debug.productMode);
        AVAnalytics.enableCrashReport(this, true);
    }

    public static WhatUpApplication getApplication() {
        return application;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}
