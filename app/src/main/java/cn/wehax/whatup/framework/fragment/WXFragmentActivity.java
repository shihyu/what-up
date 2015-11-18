package cn.wehax.whatup.framework.fragment;

import android.content.Intent;
import android.os.Bundle;

import com.google.inject.Inject;

import cn.wehax.whatup.model.app_status.AppStatusManager;
import cn.wehax.whatup.push.PushReceiver;
import cn.wehax.whatup.support.util.AppUtils;
import roboguice.activity.RoboFragmentActivity;

public abstract class WXFragmentActivity extends RoboFragmentActivity {

    @Inject
    AppStatusManager appStatusManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (AppUtils.isAppInBackground(this)) {
            // App进入后台
            appStatusManager.setAppStatus(AppStatusManager.APP_STATUS_BACKGROUND);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(appStatusManager.getAppStatus() != AppStatusManager.APP_STATUS_RUNING){
            // App启动或者App从后台切换到台前
            appStatusManager.setAppStatus(AppStatusManager.APP_STATUS_RUNING);
            sendBroadcast(new Intent(PushReceiver.ACTION_APP_GOTO_RUNING));
        }
    }

}
