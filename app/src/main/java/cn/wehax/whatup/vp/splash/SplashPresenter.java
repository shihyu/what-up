package cn.wehax.whatup.vp.splash;

import android.os.Handler;

import com.google.inject.Inject;

import cn.wehax.whatup.framework.presenter.BasePresenter;
import cn.wehax.whatup.model.user.UserManager;
import cn.wehax.whatup.push.PushManager;
import cn.wehax.whatup.support.helper.CommonHelper;

public class SplashPresenter extends BasePresenter<SplashActivity> {
    @Inject
    UserManager userManager;

    @Inject
    PushManager pushManager;

    @Override
    public void init(SplashActivity view) {
        super.init(view);
        pushManager.initialize(getActivity());
    }

    /**
     * 页面默认停留时间
     */
    private static int PAGE_STAY_TIME = 1000;

    /**
     * 延迟指定时间后跳转到相应页面
     *
     * @param delay
     */
    public void delayToMove(final MOVABLE_PAGE page, int delay) {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                switch (page) {
                    case GUIDE_VIEW:
                        mView.moveToGuideView();
                        break;
                    case CHOOSE_LOGIN_VIEW:
                        mView.moveToChooseLogin();
                        break;
                    case AR_MAIN_VIEW:
                        mView.moveToMainView();
                        break;
                }
            }
        }, delay);
    }

    public void delayToMove(final MOVABLE_PAGE page) {
        delayToMove(page, PAGE_STAY_TIME);
    }

    /**
     * 启动页之后,应该显示的页面
     */
    public void chooseMoveTo() {
        if (CommonHelper.isNeedShowGuide(getActivity())) {
            delayToMove(MOVABLE_PAGE.GUIDE_VIEW);
        } else if (userManager.isNeedLoginManually()) {
            delayToMove(MOVABLE_PAGE.CHOOSE_LOGIN_VIEW);
        } else {
            delayToMove(MOVABLE_PAGE.AR_MAIN_VIEW);
        }
    }

    /**
     * 可以跳转的页面
     */
    private enum MOVABLE_PAGE {
       GUIDE_VIEW, CHOOSE_LOGIN_VIEW, AR_MAIN_VIEW
    }
}
