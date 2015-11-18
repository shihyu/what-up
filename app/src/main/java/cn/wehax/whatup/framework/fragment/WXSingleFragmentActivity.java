package cn.wehax.whatup.framework.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.inject.Inject;

import cn.wehax.common.R;
import cn.wehax.whatup.model.app_status.AppStatusManager;
import cn.wehax.whatup.push.PushReceiver;
import cn.wehax.whatup.support.util.AppUtils;
import roboguice.activity.RoboFragmentActivity;

/**
 * A BaseActivity that simply contains a single fragment. The intent
 * used to invoke this activity is forwarded to the fragment as arguments during
 * fragment instantiation. Derived activities should only need to implement onCreateFragment()
 */
public abstract class WXSingleFragmentActivity extends RoboFragmentActivity {
    private Fragment mFragment;

    @Inject
    AppStatusManager appStatusManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);

        if (savedInstanceState == null) {
            mFragment = onCreateFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.root_container, mFragment)
                    .commit();
        }
    }

    /**
     * 获取onCreatePane方法传入的Single Fragment
     * @return
     */
    protected Fragment getFragment(){
       return mFragment;
    }

    /**
     * 创建Activity包含的单个Fragment
     */
    protected abstract Fragment onCreateFragment();

    @Override
    protected void onStop() {
        super.onStop();

        if (AppUtils.isAppInBackground(this)) {
            // App进入后台
            Log.e("push", "进入后台");
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
