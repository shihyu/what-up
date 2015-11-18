package cn.wehax.whatup.vp.login.forget_password.retrieve_password;

import com.google.inject.Inject;

import cn.wehax.whatup.R;
import cn.wehax.whatup.framework.model.OnRequestResultListener;
import cn.wehax.whatup.framework.model.WXException;
import cn.wehax.whatup.framework.presenter.BasePresenter;
import cn.wehax.whatup.model.user.UserManager;
import cn.wehax.whatup.support.helper.CommonHelper;
import cn.wehax.whatup.support.helper.InputCheckHelper;
import cn.wehax.whatup.support.helper.MoveToHelper;
import cn.wehax.whatup.support.util.ToastUtils;

public class RetrievePasswordPresenter extends BasePresenter<RetrievePasswordFragment> {
    @Inject
    UserManager userManager;

    public void clickNext(final String phoneNumber) {
        // 检查手机号码是否正确
        if (!InputCheckHelper.checkInputPhoneNumber(getActivity(), phoneNumber)) {
            return;
        }

        // 检查网络
        if(!CommonHelper.checkNetworkAvailability(getActivity())){
            return;
        }

        // 只有注册过的手机号码，才能使用找回密码功能
        userManager.checkPhoneNumberRegistered(phoneNumber, new OnRequestResultListener() {
            @Override
            public void onResult(Boolean result) {
                if (result == true) {
                    MoveToHelper.moveToResetPasswordView(getActivity(), phoneNumber);
                } else {
                    ToastUtils.showToast(getActivity(), R.string.phone_number_is_not_registered);
                }
            }

            @Override
            public void onError(WXException wxe) {

            }
        });
    }
}
