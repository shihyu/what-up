package cn.wehax.whatup.vp.login.register.set_password;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import cn.wehax.whatup.framework.fragment.WXSingleFragmentActivity;

/**
 * 注册第二步：设置密码
 */
public class SetPasswordActivity extends WXSingleFragmentActivity {

    @Override
    protected Fragment onCreateFragment() {
        SetPasswordFragment fragment = new SetPasswordFragment();
        fragment.setArguments(getIntent().getExtras());
        return fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }
}
