package cn.wehax.whatup.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.avos.avoscloud.AVOSCloud;
import com.google.inject.Inject;

import org.json.JSONObject;

import cn.wehax.whatup.R;
import cn.wehax.whatup.config.IntentKey;
import cn.wehax.whatup.model.app_status.AppStatusManager;
import cn.wehax.whatup.vp.relation.RelationActivity;
import cn.wehax.whatup.vp.splash.SplashActivity;
import roboguice.receiver.RoboBroadcastReceiver;

/**
 * LeanCloud推送接收者
 */
public class PushReceiver extends RoboBroadcastReceiver {
    public final String TAG = "push";

    /**
     * Action:App状态变为运行状态
     */
    public static final String ACTION_APP_GOTO_RUNING = "cn.wehax.whatup.appgotoruning";


    public static final String EXTRA_FROM_PUSH_REVEIVER = "EXTRA_FROM_PUSH_REVEIVER";

    @Inject
    AppStatusManager appStatusManager;

    final int MSG_TYPE_GRAFFITI = 1; // 涂鸦
    final int MSG_TYPE_CHANGE_BACKGROUND = 2;// 换背景
    final int MSG_TYPE_CHAT = 3; // 聊天消息
    final int MSG_TYPE_TRUTH = 4; // 真心话
    final int MSG_TYPE_ADVERTISEMENT = 5; // 推广

    int notificationId = 10086;

    @Override
    protected void handleReceive(Context context, Intent intent) {
        super.handleReceive(context, intent);
        Log.e(TAG, "push receiver!" + intent.getAction());
        // 仅处理指定的推送消息
        if (PushConstant.PUSH_ACTION.equals(intent.getAction())) {
            processPush(intent);
        } else if (ACTION_APP_GOTO_RUNING.equals(intent.getAction())) {
            removeNotification();
        }
    }

    /**
     * 删除通知栏通知
     */
    private void removeNotification() {
        NotificationManager notifyMgr =
                (NotificationManager) AVOSCloud.applicationContext
                        .getSystemService(
                                Context.NOTIFICATION_SERVICE);
        notifyMgr.cancel(notificationId);
    }


    /**
     * 处理接受到的推送消息
     *
     * @param intent
     */
    private void processPush(Intent intent) {
        // 假如App位于后台或者已关闭，使用通知显示推送消息。
        try {
            // 解析并获取消息数据
            JSONObject json = new JSONObject(intent.getStringExtra(PushConstant.KEY_DATA));
            Log.e(TAG, "push receiver=" + json.toString());

            // 假如用户正在使用App，不显示推送消息
            if (appStatusManager.getAppStatus() == AppStatusManager.APP_STATUS_RUNING) {
                Log.e(TAG, "用户正在使用App，不显示推送消息");
                return;
            }

            final String userId = json.optString(PushConstant.KEY_USER_ID); // 发送人ID
            final String userName = json.optString(PushConstant.KEY_USERNAME); // 发送人ID
            final int type = json.optInt(PushConstant.KEY_TYPE); // 消息类型
            final String message = json.optString(PushConstant.KEY_MESSAGE); // 聊天消息内容

            // 根据消息数据，做相应处理
            String notificationContent = "";
            Class<?> targetActivityClx = RelationActivity.class;

            if("".equals(userName)){
                switch (type) {
                    case MSG_TYPE_GRAFFITI:
                        notificationContent = "您收到一条涂鸦消息";
                        break;
                    case MSG_TYPE_CHANGE_BACKGROUND:
                        notificationContent = "您收到一条换背景消息";
                        break;
                    case MSG_TYPE_CHAT:
                        notificationContent = message;
                        break;
                    case MSG_TYPE_TRUTH:
                        notificationContent = "您收到一条真心话消息";
                        break;
                    case MSG_TYPE_ADVERTISEMENT:
                        notificationContent = message;
                        targetActivityClx = SplashActivity.class;
                        break;
                }
            }else{
                notificationContent = userName + "：";
                switch (type) {
                    case MSG_TYPE_GRAFFITI:
                        notificationContent += "涂鸦";
                        break;
                    case MSG_TYPE_CHANGE_BACKGROUND:
                        notificationContent += "换背景";
                        break;
                    case MSG_TYPE_CHAT:
                        notificationContent += message;
                        break;
                    case MSG_TYPE_TRUTH:
                        notificationContent += "真心话";
                        break;
                    case MSG_TYPE_ADVERTISEMENT:
                        notificationContent = message;
                        targetActivityClx = SplashActivity.class;
                        break;
                }
            }

            Intent resultIntent = new Intent(AVOSCloud.applicationContext, targetActivityClx);
            resultIntent.putExtra(IntentKey.INTENT_KEY_FROM, EXTRA_FROM_PUSH_REVEIVER);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(AVOSCloud.applicationContext, 0,resultIntent ,
                            PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(AVOSCloud.applicationContext)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentTitle(AVOSCloud.applicationContext.getResources().getString(R.string.app_name))
                            .setContentText(notificationContent)
                            .setTicker(notificationContent)
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS);
            mBuilder.setContentIntent(pendingIntent);
            mBuilder.setAutoCancel(true);


            NotificationManager notifyMgr =
                    (NotificationManager) AVOSCloud.applicationContext
                            .getSystemService(
                                    Context.NOTIFICATION_SERVICE);
            notifyMgr.notify(notificationId, mBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception:" + e.toString());
        }
    }
}
