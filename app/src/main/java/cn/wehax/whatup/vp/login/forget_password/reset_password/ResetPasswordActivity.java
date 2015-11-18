package cn.wehax.whatup.vp.login.forget_password.reset_password;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import cn.wehax.whatup.framework.fragment.WXSingleFragmentActivity;

/**
 * 重置密码
 */
public class ResetPasswordActivity extends WXSingleFragmentActivity{

    @Override
    protected Fragment onCreateFragment() {
        ResetPasswordFragment fragment = new ResetPasswordFragment();
        fragment.setArguments(getIntent().getExtras());
        return fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}
