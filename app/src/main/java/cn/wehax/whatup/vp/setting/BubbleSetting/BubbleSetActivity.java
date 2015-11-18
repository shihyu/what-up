package cn.wehax.whatup.vp.setting.BubbleSetting;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import cn.wehax.whatup.framework.fragment.WXSingleFragmentActivity;

/**
 * Created by Administrator on 2015/6/30.
 */
public class BubbleSetActivity extends WXSingleFragmentActivity {
    @Override
    protected Fragment onCreateFragment() {
        return new BubbleSetFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
