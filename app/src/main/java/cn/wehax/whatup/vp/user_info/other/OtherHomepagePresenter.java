package cn.wehax.whatup.vp.user_info.other;

import com.avos.avoscloud.AVFile;
import com.google.inject.Inject;

import java.util.Map;

import cn.wehax.util.StringUtils;
import cn.wehax.whatup.R;
import cn.wehax.whatup.framework.model.OnRequestDataListener;
import cn.wehax.whatup.framework.model.WXException;
import cn.wehax.whatup.framework.presenter.BasePresenter;
import cn.wehax.whatup.model.leancloud.LC;
import cn.wehax.whatup.model.user.UserManager;
import cn.wehax.whatup.support.helper.CommonHelper;
import cn.wehax.whatup.support.helper.MoveToHelper;

public class OtherHomepagePresenter extends BasePresenter<OtherHomepageFragment> {
    @Inject
    UserManager userManager;

    String targetId;
    String nickname;
    Integer sex;
    String avatarUrl;
    String introduce;

    public void requestUserInformation(String userId) {
        mView.showLoadingView();

        if (!CommonHelper.checkNetworkAvailability(getActivity())) {
            mView.showReloadView();
            return;
        }
        targetId = userId;
        userManager.requestUserById(userId, new OnRequestDataListener<Map>() {
            @Override
            public void onSuccess(Map user) {
                nickname = (String) user.get(LC.method.GetUserInfo.keyNickname);
                sex = (Integer) user.get(LC.method.GetUserInfo.keySex);

                introduce = (String) user.get(LC.method.GetUserInfo.keyIntroduce);
                if (StringUtils.isNullOrEmpty(introduce)) {
                    introduce = getActivity().getResources().getString(R.string.default_introduce);
                }

                AVFile avatar = (AVFile) user.get(LC.method.GetUserInfo.keyAvatar);
                if (avatar != null) {
                    int avatorSize = getActivity().getResources().getDimensionPixelSize(R.dimen.user_avatar_size);
                    avatarUrl = avatar.getThumbnailUrl(false, avatorSize, avatorSize);
                }

                mView.setMyInformation(nickname, sex, avatarUrl, introduce);
                mView.showContentView();
            }

            @Override
            public void onError(WXException wxe) {
                mView.showReloadView();
            }
        });
    }

    public void moveToDenounceView() {
        MoveToHelper.moveToDenounceView(getActivity(), targetId, nickname, sex, avatarUrl);
    }

    /**
     * 浏览图片
     */
    public void viewImage() {
        if (avatarUrl != null)
            CommonHelper.viewSingleImage(getActivity(), avatarUrl);
    }
}
