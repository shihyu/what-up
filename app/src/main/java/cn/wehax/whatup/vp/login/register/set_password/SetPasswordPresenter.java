package cn.wehax.whatup.vp.login.register.set_password;

import com.google.inject.Inject;

import cn.wehax.whatup.R;
import cn.wehax.whatup.framework.model.OnRequestListener;
import cn.wehax.whatup.framework.model.WXException;
import cn.wehax.whatup.framework.presenter.BasePresenter;
import cn.wehax.whatup.model.user.UserManager;
import cn.wehax.whatup.support.helper.CommonHelper;
import cn.wehax.whatup.support.helper.InputCheckHelper;
import cn.wehax.whatup.support.helper.MoveToHelper;
import cn.wehax.whatup.support.util.ToastUtils;

public class SetPasswordPresenter extends BasePresenter<SetPasswordFragment> {
    @Inject
    UserManager userManager;

    /**
     * 标记手机注册是否成功
     */
    boolean isSignUpSuccess = false;


    /**
     * 请求服务器给指定手机号码，发送重置密码短信验证码
     *
     * @param phoneNumber
     */
    public void requestPhoneVerifyCode(String phoneNumber) {
        if (!CommonHelper.checkNetworkAvailability(getActivity())) {
            return;
        }

        // 按钮设为发送中状态
        mView.setBtnSendingStatus();
        userManager.requestSMSCode(phoneNumber, new OnRequestListener() {
            @Override
            public void onSuccess() {
                ToastUtils.showToast(getActivity(), R.string.verify_code_sended);
                // 按钮设为倒计时状态
                mView.setBtnCountDownStatus();
            }

            @Override
            public void onError(WXException e) {
                ToastUtils.showToast(getActivity(), R.string.verify_code_send_fail);
                // 按钮设为重新发送状态
                mView.setBtnResendStatus();
            }
        });
    }

    /**
     * 注册或登录，并设置密码
     *
     * @param phoneNumber
     * @param verifyCode
     * @param password
     */
    public void signUpOrLoginAndSetPassword(final String phoneNumber, final String verifyCode, final String password) {
        // 检查验证码
        if (!InputCheckHelper.checkInputPassword(getActivity(), verifyCode)) {
            return;
        }

        // 检查密码
        if (!InputCheckHelper.checkInputPassword(getActivity(), password)) {
            return;
        }

        userManager.signUpOrLoginByMobilePhone(phoneNumber, verifyCode, new OnRequestListener() {
            @Override
            public void onSuccess() {
                userManager.requestSetUsernameAndPassword(userManager.getCurrentUser(), phoneNumber, password);
                MoveToHelper.moveToCompleteUserInfoView(getActivity());
            }

            @Override
            public void onError(WXException e) {
                ToastUtils.showToast(getActivity(), R.string.verify_code_fail);
            }
        });
    }
}
