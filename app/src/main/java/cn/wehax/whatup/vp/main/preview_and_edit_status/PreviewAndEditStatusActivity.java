package cn.wehax.whatup.vp.main.preview_and_edit_status;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

import cn.wehax.whatup.framework.fragment.WXSingleFragmentActivity;
import cn.wehax.whatup.vp.main.impl.ARMainActivity;

public class PreviewAndEditStatusActivity extends WXSingleFragmentActivity {

    @Override
    protected Fragment onCreateFragment() {
        PreviewAndEditStatusFragment fragment = new PreviewAndEditStatusFragment();
        fragment.setArguments(getIntent().getExtras());
        return fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(ARMainActivity.REQUEST_CODE_SHOW_BUTTON,null);
        }
        return super.onKeyDown(keyCode, event);
    }
}
