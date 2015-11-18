package cn.wehax.whatup.vp.login.register.cell_register;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import cn.wehax.whatup.framework.fragment.WXSingleFragmentActivity;

/**
 * 注册第一步：输入手机号码
 */
public class CellRegisterActivity extends WXSingleFragmentActivity {

    @Override
    protected Fragment onCreateFragment() {
        return new CellRegisterFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
