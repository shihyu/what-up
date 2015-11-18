package cn.wehax.whatup.vp.user_info.edit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.content.LocalBroadcastManager;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.google.inject.Inject;

import cn.wehax.whatup.R;
import cn.wehax.whatup.framework.model.OnRequestListener;
import cn.wehax.whatup.framework.model.WXException;
import cn.wehax.whatup.framework.presenter.BasePresenter;
import cn.wehax.whatup.model.leancloud.LC;
import cn.wehax.whatup.model.user.UserManager;
import cn.wehax.whatup.support.helper.CommonHelper;
import cn.wehax.whatup.support.helper.InputCheckHelper;
import cn.wehax.whatup.support.image.ImageUtils;
import cn.wehax.whatup.vp.user_info.personal.PersonalHomepagePresenter;


public class EditUserInfoPresenter extends BasePresenter<EditUserInfoFragment> {
    /**
     * 广播Action，修改用户信息成功
     */
    public static final String BC_ACT_UPDATE_USER_INFO_SUCCESS = "BC_ACT_UPDATE_USER_INFO_SUCCESS";

    @Inject
    UserManager userManager;

    @Inject
    PersonalHomepagePresenter presenter;

    LocalBroadcastManager localBroadcastManager;

    @Override
    public void init(EditUserInfoFragment view) {
        super.init(view);
        setUserInfo();

        localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
    }

    private void setUserInfo() {
        AVUser user = userManager.getCurrentUser();
        String nickname = user.getString(LC.table.User.nickname);

        String introduce = user.getString(LC.table.User.introduce);
        String defaultIntroduce = getActivity().getResources().getString(R.string.default_introduce);
        if (defaultIntroduce.equals(introduce)) {
            introduce = "";
        }

        AVFile avatar = user.getAVFile(LC.table.User.avatar);
        String avatarUrl = null;
        if (avatar != null) {
            int avatorSize = getActivity().getResources().getDimensionPixelSize(R.dimen.user_avatar_size);
            avatarUrl = avatar.getThumbnailUrl(false, avatorSize, avatorSize);
        }

        mView.setUserInfo(nickname,
                introduce,
                avatarUrl);
    }

    /**
     * 保存用户信息
     *
     * @param newNickname
     * @param newIntroduce
     */
    public void saveUserInfo(String newNickname, String newIntroduce) {
        if (!InputCheckHelper.checkInputNickname(getActivity(), newNickname)) {
            return;
        }

        userManager.updateUserInfo(newNickname, newIntroduce, new OnRequestListener() {
            @Override
            public void onSuccess() {
                CommonHelper.showErrorMsg(getActivity(), "修改成功");
                localBroadcastManager.sendBroadcast(new Intent(BC_ACT_UPDATE_USER_INFO_SUCCESS));
            }

            @Override
            public void onError(WXException e) {
                CommonHelper.showErrorMsg(getActivity(), "修改失败");
            }
        });
    }

    public void updateUserAvatar(Bitmap bitmap) {
        byte [] avatarData= ImageUtils.toBytes(bitmap,80);
        String fileName = System.currentTimeMillis() + ".png";
        AVFile avatar= new AVFile(fileName,avatarData);
        userManager.updateUserInfo(avatar, new OnRequestListener() {
            @Override
            public void onSuccess() {
                //CommonHelper.showErrorMsg(getActivity(), "头像更新成功");
            }

            @Override
            public void onError(WXException e) {
                //ToastUtils.showSafeToast(getActivity(),"头像更新失败");
            }
        });
    }
}
