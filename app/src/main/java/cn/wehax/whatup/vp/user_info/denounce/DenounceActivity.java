package cn.wehax.whatup.vp.user_info.denounce;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import cn.wehax.whatup.framework.fragment.WXSingleFragmentActivity;

public class DenounceActivity extends WXSingleFragmentActivity {

    @Override
    protected Fragment onCreateFragment() {
        DenounceFragment fragment = new DenounceFragment();
        fragment.setArguments(getIntent().getExtras());
        return fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}
