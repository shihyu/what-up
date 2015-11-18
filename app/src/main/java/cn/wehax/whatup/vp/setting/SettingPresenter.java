package cn.wehax.whatup.vp.setting;

import android.content.DialogInterface;
import android.os.Environment;

import com.google.inject.Inject;

import java.io.File;

import cn.wehax.util.DialogUtil;
import cn.wehax.whatup.framework.presenter.BasePresenter;
import cn.wehax.whatup.model.user.UserManager;
import cn.wehax.whatup.push.PushManager;
import cn.wehax.whatup.support.helper.DataCleanManager;
import cn.wehax.whatup.support.util.ToastUtils;

public class SettingPresenter extends BasePresenter<SettingFragment> {
    @Inject
    UserManager userManager;

    @Inject
    PushManager pushManager;

    public void logout() {
        userManager.logout();
        pushManager.updateInstallationAsRemoveUid();

        mView.showLogoutConfirmDialog();
    }

    public void clearCache() {
        // TODO 清除缓存
        DialogUtil.showConfirmDialog(getActivity(), "清除缓存", "清除缓存后，所有收到的图片将被清除？", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DataCleanManager.cleanExternalCache(getActivity());
                DataCleanManager.deleteFilesByDirectory(new File(Environment.getExternalStorageDirectory() + "/android/data/" + getActivity().getPackageName() + "/photo"));
                ToastUtils.showToast(getActivity(), "缓存已被清除");
                dialog.dismiss();
            }
        }, null, true);
    }
}
