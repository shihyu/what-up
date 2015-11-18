package cn.wehax.whatup.vp.guide;

import com.google.inject.Inject;

import cn.wehax.whatup.framework.presenter.BasePresenter;
import cn.wehax.whatup.model.user.UserManager;
import cn.wehax.whatup.support.helper.CommonHelper;

public class LastPagePresenter extends BasePresenter<ImageFragment> {
    @Inject
    UserManager userManager;
    /**
     * 引导页之后,应该显示的页面
     */
    public void chooseMoveTo() {
        CommonHelper.recordGuideShowed(getActivity());

        if (userManager.isNeedLoginManually()) {
            mView.moveToChooseLogin();
        } else {
             mView.moveToMainView();
        }
    }

}
