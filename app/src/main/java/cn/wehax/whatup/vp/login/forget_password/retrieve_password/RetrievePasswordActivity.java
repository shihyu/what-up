package cn.wehax.whatup.vp.login.forget_password.retrieve_password;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import cn.wehax.whatup.framework.fragment.WXSingleFragmentActivity;

public class RetrievePasswordActivity extends WXSingleFragmentActivity {

    @Override
    protected Fragment onCreateFragment() {
        return new RetrievePasswordFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}
