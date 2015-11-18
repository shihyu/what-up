package cn.wehax.whatup.push;

import android.app.Activity;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVPush;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SendCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.json.JSONObject;

import cn.wehax.util.StringUtils;
import cn.wehax.whatup.framework.model.WXException;
import cn.wehax.whatup.model.leancloud.LC;
import cn.wehax.whatup.model.user.UserManager;
import cn.wehax.whatup.support.helper.LogHelper;
import cn.wehax.whatup.vp.splash.SplashActivity;

/**
 * 用户管理器
 */
@Singleton
public class PushManager {
    public static final String TAG = "PushManager";
    private Activity context;

    @Inject
    UserManager userManager;

    /**
     * 初始化 LeanCloud Push服务，并启动服务
     *
     * @param activity
     */
    public void initialize(Activity activity) {
        context = activity;
        // 启动推送，设置默认打开的 Activity
        PushService.setDefaultPushCallback(context, SplashActivity.class);
        AVInstallation.getCurrentInstallation().saveInBackground();

        Log.e(TAG, "init: push init;");
    }

    /**
     * 更新installation表，保存当前登录用户的ID
     */
    public void updateInstallationAsAddUid() {
        Log.e("push", "updateInstallationAsAddUid");
         String curUid = userManager.getCurrentUser().getObjectId();
        String installationUid = AVInstallation.getCurrentInstallation().getString(LC.table.Installation.uid);

        if (curUid.equals(installationUid)) {
            return;
        }

        AVInstallation installation = AVInstallation.getCurrentInstallation();
        installation.put(LC.table.Installation.uid, curUid);
        installation.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                try{
                    if(e != null)
                        throw new WXException(e.getMessage());

                    Log.e("push", "installation保存成功");
                }catch (WXException ex){
                    ex.printStackTrace();
                    Log.e("push", "installation保存失败", ex);
                }
            }
        });
    }

    /**
     * 更新installation表，清除当前登录用户的ID
     */
    public void updateInstallationAsRemoveUid() {
        Log.e("push", "updateInstallationAsRemoveUid");
        AVInstallation installation = AVInstallation.getCurrentInstallation();
        installation.put(LC.table.Installation.uid, "");
        installation.saveInBackground();
    }

    /**
     * 推送消息
     *
     * @param msg 待推送的消息
     * @param lis
     */
    public void pushMessage(Message msg, final OnPushListener lis) {
        try {
            if(lis == null)
                return;

            if (msg == null) {
                lis.onFail(new WXException("参数不能为空"));
                return;
            }

            Log.e(TAG, "Message=" + msg.toString());

            if (StringUtils.isNullOrEmpty(msg.getUid())) {
                lis.onFail(new WXException("用户id不能为空"));
                return;
            }

            AVQuery pushQuery = AVInstallation.getQuery();
            pushQuery.whereEqualTo(LC.table.Installation.uid, msg.getUid());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(PushConstant.KEY_ACTION, PushConstant.PUSH_ACTION);
            jsonObject.put(PushConstant.KEY_ALERT, msg.getAlert());
            jsonObject.put(PushConstant.KEY_OBJECT, msg.getObj());
            jsonObject.put(PushConstant.KEY_USER_ID, userManager.getCurrentUser().getObjectId());
            if (msg.isBadge()) {
                jsonObject.put(PushConstant.KEY_BADGE, "Increment");
            }
            if (msg.isSound()) {
                jsonObject.put(PushConstant.KEY_SOUND, "default");
            }

            AVPush push = new AVPush();
            push.setData(jsonObject);
            push.setQuery(pushQuery);
            push.sendInBackground(new SendCallback() {

                @Override
                public void done(AVException e) {
                    if (e != null) {
                        LogHelper.e(TAG, "pushMessage-onFail", e);
                        lis.onFail(new WXException("推送失败"));
                    } else {
                        lis.onSuccess();
                    }
                }

            });
        } catch (Exception e) {
            Log.e(TAG, "pushMessage-onFail", e);
            lis.onFail(new WXException("推送失败"));
        }

    }



    /**
     * 推送事件监听器
     */
    public static interface OnPushListener {
        void onSuccess();

        void onFail(WXException e);
    }

    /**
     * 推送的消息数据
     */
    public static class Message {
        String alert;
        String uid; // 推送的目标用户ID
        String obj;
        boolean isBadge;
        boolean isSound;

        public Message() {
            alert = "你有新消息";
            obj = "{}";
            isBadge = false;
            isSound = false;
        }

        public String getAlert() {
            return alert;
        }

        public void setAlert(String alert) {
            this.alert = alert;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getObj() {
            return obj;
        }

        public void setObj(String obj) {
            this.obj = obj;
        }

        public boolean isBadge() {
            return isBadge;
        }

        public void setBadge(boolean isBadge) {
            this.isBadge = isBadge;
        }

        public boolean isSound() {
            return isSound;
        }

        public void setSound(boolean isSound) {
            this.isSound = isSound;
        }

        @Override
        public String toString() {
            return "Message{" +
                    "alert='" + alert + '\'' +
                    ", uid='" + uid + '\'' +
                    ", obj='" + obj + '\'' +
                    ", isBadge=" + isBadge +
                    ", isSound=" + isSound +
                    '}';
        }
    }
}
