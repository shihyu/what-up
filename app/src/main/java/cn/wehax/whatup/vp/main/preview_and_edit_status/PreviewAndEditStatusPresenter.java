package cn.wehax.whatup.vp.main.preview_and_edit_status;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.inject.Inject;

import cn.wehax.whatup.config.IntentKey;
import cn.wehax.whatup.framework.presenter.BasePresenter;
import cn.wehax.whatup.model.user.UserManager;

public class PreviewAndEditStatusPresenter extends BasePresenter<PreviewAndEditStatusFragment> {
    public static final String TAG = "PreviewAndEditStatusPresenter";

    @Inject
    UserManager userManager;

    String statusImagePath;

    @Override
    public void init(PreviewAndEditStatusFragment view, Bundle arguments) {
        super.init(view, arguments);

        statusImagePath = getArguments().getString(IntentKey.INTENT_KEY_STATUS_IMAGE_PATH);
        if (statusImagePath == null) {
            Log.e(TAG, "Intent参数不正确");
            getActivity().finish();
        }

        mView.setStatusImage(statusImagePath);
    }

    /**
     * 返回状态数据
     */
    public void returnStatusData() {
        Intent data = new Intent();
        data.putExtra(IntentKey.INTENT_KEY_STATUS_IMAGE_PATH, statusImagePath);
        getActivity().setResult(Activity.RESULT_OK, data);
        getActivity().finish();
    }

    /**
     * 返回状态数据
     *
     * @param statusText 状态文本
     * @param coord 编辑框位置字符串
     *
     * <p>coord格式：{"x":0.3,"y":0.4} <br>
     * （x,y）= 编辑框屏幕坐标 / 屏幕尺寸<br>
     * </p>
     */
    public void returnStatusData(String statusText, String coord) {
        Intent data = new Intent();
        data.putExtra(IntentKey.INTENT_KEY_STATUS_IMAGE_PATH, statusImagePath);
        data.putExtra(IntentKey.INTENT_KEY_TEXT, statusText);
        data.putExtra(IntentKey.INTENT_KEY_COORD, coord);
        getActivity().setResult(Activity.RESULT_OK, data);
        getActivity().finish();
    }
}
