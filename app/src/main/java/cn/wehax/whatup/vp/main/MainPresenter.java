package cn.wehax.whatup.vp.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FunctionCallback;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.Map;
import cn.wehax.whatup.config.Constant;
import cn.wehax.whatup.config.IntentKey;
import cn.wehax.whatup.framework.model.OnRequestDataListener;
import cn.wehax.whatup.framework.model.WXException;
import cn.wehax.whatup.framework.presenter.BasePresenter;
import cn.wehax.whatup.model.leancloud.LC;
import cn.wehax.whatup.model.location.LocationManager;
import cn.wehax.whatup.model.status.StatusManager;
import cn.wehax.whatup.model.user.UserManager;
import cn.wehax.whatup.support.helper.CommonHelper;
import cn.wehax.whatup.support.helper.LogHelper;
import cn.wehax.whatup.vp.main.impl.ARMainActivity;

/**
 * Created by mayuhan on 15/6/10.
 */
public class MainPresenter extends BasePresenter<IMainView> {
    public static final String TAG = "MainPresenter";

    @Inject
    StatusManager statusManager;

    @Inject
    LocationManager locationManager;

    @Inject
    UserManager userManager;

    private LocationManager.LocationResult locationResult;
    private FirstMessageReceiver messageReceiver;

    private ArrayList<Map> statusDataList = new ArrayList<>();

    public void refreshStatus() {
        if (locationResult != null) {
            requestNearByStatus();
        } else {
            location();
        }

        AVFile avatar = (AVFile) userManager.getCurrentUser().get(LC.table.User.avatar);
        if(avatar != null){
            mView.setUserAvatar(avatar.getThumbnailUrl(false, 100, 100));
        }

    }

    private void location() {
        locationManager.requestLocation(LocationManager.STRATEGY_NETWORK, new LocationManager.OnLocationListener() {
            @Override
            public void onSuccess(LocationManager.LocationResult result, Boolean fromNetwork) {
                locationResult = result;
                requestNearByStatus();

                if (fromNetwork) {
                    userManager.updateUserLocation(result.getLocation(), result.getCity());
                }
            }

            @Override
            public void onFail(WXException e) {

            }
        });
    }

    private void requestNearByStatus() {
        statusManager.getNearByStatus(locationResult.getLocation()
                , locationResult.getCity(), new StatusFuncCallback());
    }


    public void sendStatus(String imgPath, String text, String coord) {
        AVUser user = userManager.getCurrentUser();
        mView.showPersonalAndRelationBtn();
        mView.animToStartSendStatus();
        statusManager.sendStatus(
                user.getAVGeoPoint(LC.table.User.location),
                imgPath,
                text,
                coord,
                new OnRequestDataListener<AVFile>() {

                    @Override
                    public void onSuccess(AVFile imgFile) {
                        // 记录当前用户发送过状态
                        if (!userManager.isSendedStatus()) {
                            userManager.saveSendedStatus();
                        }
//                        Log.e(TAG, "onSuccess=" + data.toString());
                        CommonHelper.showErrorMsg(getActivity(), "发送成功");
                        mView.setStatusThumbnail(imgFile.getThumbnailUrl(false, 100, 100));
                        mView.animToSendStatusSuccess();
                    }

                    @Override
                    public void onError(WXException e) {
                        Log.e(TAG, "发送状态失败=", e);
                        // 记录当前用户发送过状态
                        if (!userManager.isSendedStatus()) {
                            userManager.saveSendedStatus();
                        }

                        mView.animToSendStatusError();
                        CommonHelper.showErrorMsg(getActivity(), "发送状态失败");
                    }
                });
        if (!userManager.isSendedStatus()) {
            userManager.saveSendedStatus();
        }
    }

    private class StatusFuncCallback extends FunctionCallback<ArrayList<Map>> {

        @Override
        public void done(ArrayList<Map> maps, AVException e) {
            if (e == null) {
                statusDataList.clear();
                statusDataList.addAll(maps);
                mView.refreshStatus(maps);
            } else {
                LogHelper.e("dss", "StatusFuncCallback", e);
                e.printStackTrace();
            }
        }
    }

    public void onClickStatus(int position) {
        Map userInfo = statusDataList.get(position);
        String statusId = (String) userInfo.get(LC.method.GetNearbyStatus.keyId);
        String targetId = (String)userInfo.get(LC.method.GetNearbyStatus.keyTargetId);
        mView.goToChatView(targetId, statusId);
    }

    //第一次接到消息的时间，添加新手引导界面。
    private class FirstMessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }

            if (Constant.RECEIVE_MESSAGE_ACTION.equals(action)) {
                String convId = intent.getStringExtra(IntentKey.INTENT_KEY_CONVERSATION_ID);
                if (TextUtils.isEmpty(convId)) {
                    return;
                }
            }

            if (getActivity() instanceof ARMainActivity) {
                ARMainActivity activity = (ARMainActivity) getActivity();
                if (activity.isViewValidate()) {
                    activity.firstReceiveMessage();
                }

            }

        }
    }

    /**
     * 注册消息接收器
     */
    public void registerMessageReceiver() {
        messageReceiver = new FirstMessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.RECEIVE_MESSAGE_ACTION);
        getActivity().registerReceiver(messageReceiver, filter);
    }

    public void unregisterReceiver() {
        if (messageReceiver != null) {
            getActivity().unregisterReceiver(messageReceiver);
        }
    }

    public double distanceToMe(AVGeoPoint point) {
        return locationResult.getLocation().distanceInMilesTo(point);
    }

    public void goToChatView(String targetUid, String statusId) {
        mView.goToChatView(targetUid, statusId);
    }

    public void setSendStatus(){
        if (!userManager.isSendedStatus()) {
            userManager.saveSendedStatus();
        }
    }

}

