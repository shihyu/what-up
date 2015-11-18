package cn.wehax.whatup.vp.login.complete_user_info;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import cn.wehax.whatup.framework.fragment.WXSingleFragmentActivity;


public class CompleteUserInfoActivity extends WXSingleFragmentActivity{

    @Override
    protected Fragment onCreateFragment() {
        return new CompleteUserInfoFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getFragment().onActivityResult(requestCode, resultCode, data);
    }
}
