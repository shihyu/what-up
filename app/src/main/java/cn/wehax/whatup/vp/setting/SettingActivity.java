package cn.wehax.whatup.vp.setting;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import cn.wehax.whatup.framework.fragment.WXSingleFragmentActivity;

public class SettingActivity extends WXSingleFragmentActivity {

    @Override
    protected Fragment onCreateFragment() {
        return new SettingFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
