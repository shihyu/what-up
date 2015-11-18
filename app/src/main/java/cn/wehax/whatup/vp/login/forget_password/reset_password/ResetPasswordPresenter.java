package cn.wehax.whatup.vp.login.forget_password.reset_password;

import android.widget.Toast;

import com.google.inject.Inject;

import cn.wehax.whatup.R;
import cn.wehax.whatup.framework.model.OnRequestListener;
import cn.wehax.whatup.framework.model.WXException;
import cn.wehax.whatup.framework.presenter.BasePresenter;
import cn.wehax.whatup.model.user.UserManager;
import cn.wehax.whatup.support.helper.CommonHelper;
import cn.wehax.whatup.support.util.ToastUtils;

public class ResetPasswordPresenter extends BasePresenter<ResetPasswordFragment> {

    @Inject
    UserManager userManager;


    /**
     * 请求服务器给指定手机号码，发送重置密码短信验证码
     *
     * @param phoneNumber
     */
    public void requestPasswordResetSmsCode(String phoneNumber) {
        // 检查网络
        if (!CommonHelper.checkNetworkAvailability(getActivity())) {
            return;
        }

        // 按钮设为发送中状态
        mView.setBtnSendingStatus();

        userManager.requestPasswordResetSmsCode(phoneNumber, new OnRequestListener() {
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
     * 重置密码
     *
     * @param verifyCode
     * @param newPassword
     */
    public void resetPasswordBySmsCode(String verifyCode, final String newPassword) {
        // 检查网络
        if (!CommonHelper.checkNetworkAvailability(getActivity())) {
            return;
        }

        userManager.resetPasswordBySmsCode(verifyCode, newPassword, new OnRequestListener() {
            @Override
            public void onSuccess() {
                // 密码重置成功，跳转到选择登录页面
                ToastUtils.showToast(getActivity(), R.string.reset_password_success, Toast.LENGTH_LONG);
                mView.moveToChooseLoginOrRegisterView();
            }

            @Override
            public void onError(WXException e) {
                CommonHelper.showErrorMsg(getActivity(), "重置密码失败");
            }
        });
    }
}
