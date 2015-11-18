package cn.wehax.whatup.vp.login.register.cell_register;

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

public class CellRegisterPresenter extends BasePresenter<CellRegisterFragment> {
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

        // 只能使用未注册过的手机进行注册
        userManager.checkPhoneNumberRegistered(phoneNumber, new OnRequestResultListener() {
            @Override
            public void onResult(Boolean result) {
                if (result == true) {
                    ToastUtils.showToast(getActivity(), R.string.USER_MOBILE_PHONENUMBER_TAKEN);
                } else {
                    MoveToHelper.moveToSetPasswordView(getActivity(), phoneNumber);
                }
            }

            @Override
            public void onError(WXException wxe) {

            }
        });
    }

}
