package cn.wehax.whatup.vp.login.login;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import cn.wehax.whatup.framework.fragment.WXSingleFragmentActivity;

public class LoginActivity extends WXSingleFragmentActivity {

    @Override
    protected Fragment onCreateFragment() {
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(getIntent().getExtras());
        return fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
