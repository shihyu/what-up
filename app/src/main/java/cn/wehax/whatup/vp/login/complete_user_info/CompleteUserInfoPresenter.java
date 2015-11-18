package cn.wehax.whatup.vp.login.complete_user_info;

import android.graphics.Bitmap;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.SaveCallback;
import com.google.inject.Inject;

import cn.wehax.whatup.R;
import cn.wehax.whatup.framework.model.OnRequestListener;
import cn.wehax.whatup.framework.model.WXException;
import cn.wehax.whatup.framework.presenter.BasePresenter;
import cn.wehax.whatup.model.user.UserManager;
import cn.wehax.whatup.support.image.ImageUtils;
import cn.wehax.whatup.support.util.ToastUtils;

public class CompleteUserInfoPresenter extends BasePresenter<CompleteUserInfoFragment> {
    @Inject
    UserManager userManager;

    AVFile avatar;

    /**
     * 保存用户信息
     *
     * @param nickName
     * @param userSex
     * @param avatorBmp
     */
    public void saveUserInfo(final String nickName, final int userSex, Bitmap avatorBmp) {
        if (avatorBmp != null) {
            byte[] avatarData = ImageUtils.toBytes(avatorBmp, 80);
            String fileName = System.currentTimeMillis() + ".png";
            avatar = new AVFile(fileName, avatarData);
            avatar.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        completeUserInfo(nickName, userSex, avatar);
                    }else{
                        ToastUtils.showToast(getActivity(), R.string.complete_user_info_fail);
                    }
                }
            });
        }else{
            completeUserInfo(nickName, userSex, null);
        }
    }

    private void completeUserInfo(String nickName, Number userSex, AVFile avatar){
        userManager.completeUserInfo(userManager.getCurrentUser(), nickName, userSex, avatar, new OnRequestListener() {
            @Override
            public void onSuccess() {
                mView.moveToARMainView(getActivity());
            }

            @Override
            public void onError(WXException e) {
                ToastUtils.showToast(getActivity(), R.string.complete_user_info_fail);
            }
        });
    }
}
