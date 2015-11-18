package cn.wehax.whatup.vp.user_info.other;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import cn.wehax.whatup.framework.fragment.WXSingleFragmentActivity;

/**
 * 重置密码
 */
public class OtherHomepageActivity extends WXSingleFragmentActivity {

    @Override
    protected Fragment onCreateFragment() {
        OtherHomepageFragment fragment = new OtherHomepageFragment();
        fragment.setArguments(getIntent().getExtras());
        return fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}
