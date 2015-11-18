package cn.wehax.whatup.vp.user_info.denounce;

import android.os.Bundle;

import com.google.inject.Inject;

import cn.wehax.whatup.config.IntentKey;
import cn.wehax.whatup.framework.model.OnRequestListener;
import cn.wehax.whatup.framework.model.WXException;
import cn.wehax.whatup.framework.presenter.BasePresenter;
import cn.wehax.whatup.model.user.UserManager;
import cn.wehax.whatup.support.helper.CommonHelper;

public class DenouncePresenter extends BasePresenter<DenounceFragment> {

    @Inject
    UserManager userManager;

    String userId; // 被举报人ID
    String nickname;
    Integer sex;
    String avatarUrl;

    @Override
    public void init(DenounceFragment view, Bundle arguments) {
        super.init(view, arguments);

        userId = getArguments().getString(IntentKey.INTENT_KEY_TARGET_UID);
        nickname = getArguments().getString(IntentKey.INTENT_KEY_NICKNAME);
        sex = getArguments().getInt(IntentKey.INTENT_KEY_SEX);
        avatarUrl = getArguments().getString(IntentKey.INTENT_KEY_AVATAR_URL);

        mView.setUserInformation(nickname, sex, avatarUrl);
    }

    public void denounce(String reason) {
        userManager.denounce(reason, userId, new OnRequestListener() {
            @Override
            public void onSuccess() {
                CommonHelper.showErrorMsg(getActivity(), "举报成功");
            }

            @Override
            public void onError(WXException e) {
                CommonHelper.showErrorMsg(getActivity(), "举报失败");
            }
        });
    }
}
