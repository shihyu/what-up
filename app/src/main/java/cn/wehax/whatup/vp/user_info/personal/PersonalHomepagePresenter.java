package cn.wehax.whatup.vp.user_info.personal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.google.inject.Inject;

import cn.wehax.util.StringUtils;
import cn.wehax.whatup.R;
import cn.wehax.whatup.framework.presenter.BasePresenter;
import cn.wehax.whatup.model.leancloud.LC;
import cn.wehax.whatup.model.user.UserManager;
import cn.wehax.whatup.support.helper.CommonHelper;
import cn.wehax.whatup.vp.user_info.edit.EditUserInfoPresenter;

public class PersonalHomepagePresenter extends BasePresenter<PersonalHomepageFragment> {

    public static final int UPDATE_DATA = 556;

    @Inject
    UserManager userManager;

    String avatarUrl;

    LocalBroadcastManager localBroadcastManager;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(EditUserInfoPresenter.BC_ACT_UPDATE_USER_INFO_SUCCESS.equals(intent.getAction())){
                setUserInfo();
            }
        }
    };

    @Override
    public void init(PersonalHomepageFragment view) {
        super.init(view);
        setUserInfo();

        initBroadcast();
    }

    private void initBroadcast() {
        localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(EditUserInfoPresenter.BC_ACT_UPDATE_USER_INFO_SUCCESS);
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    public void onDestroy(){
        if(broadcastReceiver != null){
            localBroadcastManager.unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }

    private void setUserInfo() {
        AVUser user = userManager.getCurrentUser();
        String nickname = user.getString(LC.table.User.nickname);
        Integer sex = user.getInt(LC.table.User.sex);

        String introduce = user.getString(LC.table.User.introduce);
        if (StringUtils.isNullOrEmpty(introduce)) {
            introduce = getActivity().getResources().getString(R.string.default_introduce);
        }

        AVFile avatar = user.getAVFile(LC.table.User.avatar);
        if (avatar != null) {
            int avatarSize = getActivity().getResources().getDimensionPixelSize(R.dimen.user_avatar_size);
            avatarUrl = avatar.getThumbnailUrl(false, avatarSize, avatarSize);
        }

        mView.setUserInfo(nickname, sex, avatarUrl, introduce);
    }

    public String getMineUserId() {
        return userManager.getCurrentUser().getObjectId();
    }

    /**
     * 浏览图片
     */
    public void viewImage() {
        if (avatarUrl != null)
            CommonHelper.viewSingleImage(getActivity(), avatarUrl);
    }

    /**
     * 检查用户头像是否能够点击
     */
    public void checkAvatarClickable() {
        // 如果用户未设置头像，不能点击头像
        if(avatarUrl == null){
            mView.setAvatarClickable(false);
        }
    }
}
