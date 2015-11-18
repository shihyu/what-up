package cn.wehax.whatup.vp.login.login;

import com.google.inject.Inject;

import cn.wehax.whatup.framework.model.WXException;
import cn.wehax.whatup.framework.model.OnRequestListener;
import cn.wehax.whatup.framework.presenter.BasePresenter;
import cn.wehax.whatup.model.user.UserManager;
import cn.wehax.whatup.support.helper.CommonHelper;
import cn.wehax.whatup.support.helper.MoveToHelper;

public class LoginPresenter extends BasePresenter<LoginFragment> {
    @Inject
    UserManager userManager;

    /**
     * 手机号登录
     *
     * @param phoneNumber
     * @param password
     */
    void cellLogin(String phoneNumber, String password) {
        userManager.cellLogin(phoneNumber, password, new OnRequestListener() {
            @Override
            public void onSuccess() {
                loginSuccess();
                getActivity().finish();
            }

            @Override
            public void onError(WXException e) {
                CommonHelper.showErrorMsg(getActivity(), e);
            }
        });

    }

    private void loginSuccess(){
        // 如果用户信息完整，那么直接进入首页，否则完善用户资料
        if (userManager.isUserInformationIntegrity(userManager.getCurrentUser())) {
            MoveToHelper.moveToARMainView(getActivity());
        } else {
            MoveToHelper.moveToCompleteUserInfoView(getActivity());
        }
        getActivity().finish();
    }

    /**
     * 第三方登录
     * @param platform 登录平台
     */
    public void thirdPartyLogin(int platform) {
        userManager.thirdPartyLogin(platform,new UserManager.OnThirdPartyLoginListener() {
            @Override
            public void onSuccess() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loginSuccess();
                        getActivity().finish();
                    }
                });
            }

            @Override
            public void onError(final WXException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonHelper.showErrorMsg(getActivity(), e);
                    }
                });
            }

            @Override
            public void onCancel() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonHelper.showErrorMsg(getActivity(), "取消授权");
                    }
                });
            }
        });
    }
}
