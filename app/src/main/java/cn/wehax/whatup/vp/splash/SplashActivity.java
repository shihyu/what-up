package cn.wehax.whatup.vp.splash;


import android.os.Bundle;

import javax.inject.Inject;

import cn.wehax.whatup.R;
import cn.wehax.whatup.framework.fragment.WXActivity;
import cn.wehax.whatup.support.helper.MoveToHelper;
import cn.wehax.whatup.support.util.PackageUtils;
import cn.wehax.whatup.support.util.PreferencesUtils;
import roboguice.inject.ContentView;

@ContentView(R.layout.activity_splash)
public class SplashActivity extends WXActivity {

    @Inject
    SplashPresenter mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.init(this);
        mPresenter.chooseMoveTo();

        if(!isExist()){
            PackageUtils.createShortCut(this, R.drawable.ic_launcher, R.string.app_name);
            PreferencesUtils.putBoolean(this, "shortCut", true);
        }

    }

    public void moveToMainView() {
        MoveToHelper.moveToARMainView(this);
        this.finish();
    }

    public void moveToGuideView() {
        MoveToHelper.moveToGuideView(this);
        this.finish();
    }

    public void moveToChooseLogin() {
        MoveToHelper.moveToChooseLoginView(this);
        this.finish();
    }

    /**
     * 判断快捷图标是否在数据库中已存在
     */
    private boolean isExist() {
       return  PreferencesUtils.getBoolean(this,"shortCut",false);
    }


}
