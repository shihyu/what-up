package cn.wehax.whatup.vp.main.impl;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.avos.avoscloud.LogUtil;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import cn.wehax.util.DialogUtil;
import cn.wehax.whatup.R;
import cn.wehax.whatup.ar.ARRoboGuiceActivity;
import cn.wehax.whatup.ar.Config;
import cn.wehax.whatup.ar.marker.Marker;
import cn.wehax.whatup.ar.vision.ARConfig;
import cn.wehax.whatup.ar.vision.ARView;
import cn.wehax.whatup.ar.vision.PictureCallback;
import cn.wehax.whatup.config.IntentKey;
import cn.wehax.whatup.model.app_status.AppStatusManager;
import cn.wehax.whatup.model.chat.MessageManager;
import cn.wehax.whatup.model.chat.ReceiveMessageCallback;
import cn.wehax.whatup.model.chatView.ChatMessageContent;
import cn.wehax.whatup.model.conversation.ConversationManager;
import cn.wehax.whatup.model.user.UserManager;
import cn.wehax.whatup.push.PushManager;
import cn.wehax.whatup.support.db.DatabaseManager;
import cn.wehax.whatup.support.helper.MoveToHelper;
import cn.wehax.whatup.support.util.PreferencesUtils;
import cn.wehax.whatup.vp.chat.ChatActivity;
import cn.wehax.whatup.vp.guide.GuideUtils;
import cn.wehax.whatup.vp.main.IMainView;
import cn.wehax.whatup.vp.main.MainARRender;
import cn.wehax.whatup.vp.main.MainPresenter;
import roboguice.inject.InjectView;

/**
 * Created by mayuhan on 15/6/30.
 */
public class ARMainActivity extends ARRoboGuiceActivity implements
        IMainView,
        View.OnClickListener,
        View.OnTouchListener {

    // request code
    private static final int REQUEST_CODE_STATUS_DATA = 1;

    //新手引导本地存储标示
    public static final String NEW_GUIDE = "new_guide";

    public static final int REQUEST_CODE_SHOW_BUTTON = 299;

    private static final String TAG = "ARMainActivity";

    private List<Marker> markerList;

    private boolean isViewValidate;

    private MainARRender render;

    @javax.inject.Inject
    DatabaseManager databaseManager;

    @javax.inject.Inject
    UserManager userManager;

    @javax.inject.Inject
    ConversationManager conversationManager;

    @javax.inject.Inject
    MessageManager messageManager;


    @javax.inject.Inject
    MainPresenter mPresenter;

    @InjectView(R.id.ar_view)
    ARView arView;

    @InjectView(R.id.unread_message_view)
    View unreadMessageView;


    @InjectView(R.id.info)
    RelativeLayout info;

    @InjectView(R.id.info_btn)
    Button infoBtn;

    @InjectView(R.id.info_avatar)
    ImageView infoAvatar;

    @InjectView(R.id.info_avatar_container)
    RelativeLayout infoAvatarContiner;

    @InjectView(R.id.info_alert_btn)
    Button infoAlertBtn;

    @Inject
    PushManager pushManager;


    @InjectView(R.id.overturn_camera)
    ImageButton overturnCamera;

    @InjectView(R.id.relation_btn)
    Button relationBtn;

    @InjectView(R.id.take_picture_btn)
    Button takePictureBtn;

    @InjectView(R.id.status_thumbnail)
    ImageView statusThumb;

    private boolean isNewGuide;

    @InjectView(R.id.loading_view)
    ImageView loadingView;

    private void showLoadingView() {
        loadingView.setVisibility(View.VISIBLE);
    }

    private void hideLoadingView() {
        loadingView.setVisibility(View.GONE);
    }

    private GuideUtils guideUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_main);
//        showLoadingView();
        pushManager.updateInstallationAsAddUid();
        mPresenter.setView(this);
        isNewGuide = userManager.isSendedStatus();
        info.setOnClickListener(this);
        infoBtn.setOnClickListener(this);
        infoAvatarContiner.setOnClickListener(this);
        infoAlertBtn.setOnClickListener(this);
        takePictureBtn.setOnClickListener(this);
        statusThumb.setOnClickListener(this);
        relationBtn.setOnClickListener(this);
        setConvertTapIntoTrigger(false);
        overturnCamera.setOnClickListener(this);


        Log.e("chat", "curUser=" + userManager.getCurrentUser().getObjectId());
        databaseManager.switchDatabase(userManager.getCurrentUser().getObjectId());
        conversationManager.login(userManager.getCurrentUser().getObjectId(), new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                if (e != null) {
                    e.printStackTrace();

                    Toast.makeText(ARMainActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ARMainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();

                }
            }
        });

        synchronousRemoteMessage();
        setReadMessageListener();

        ARConfig config = new ARConfig.Builder(Config.Z_NEAR,
                Config.Z_FAR,
                Config.CAMERA_Z)
                .build();

        arView.setConfig(config);
        render = new MainARRender(arView, mPresenter);
        arView.setRenderer(render);
        setCardboardView(arView);

        markerList = new ArrayList<Marker>();

        if (!isNewGuide) {
            //获取引导界面工具类的实例
            guideUtil = GuideUtils.getInstance();
            //设置context，防止Null
            guideUtil.setContext(this);
            //调用引导界面
            final View photographView = View.inflate(this, R.layout.guide_photograph, null);
            photographView.findViewById(R.id.take_picture_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    takePictureBtn.setEnabled(false);
                    guideUtil.removeGuideView(photographView);
                    arView.takePicture(ARMainActivity.this, new PictureCallback() {
                        @Override
                        public void onPictureTaken(String path) {
                            takePictureBtn.setEnabled(true);
                            MoveToHelper.moveToPreviewAndEditStatusViewForResult(ARMainActivity.this, REQUEST_CODE_STATUS_DATA, path);
                        }
                    });
                }
            });

            guideUtil.initGuide(this, photographView);
        }

        //先判断是否是第一次接收消息
        if (!isNewGuide) {
            mPresenter.registerMessageReceiver();
        }

        // 检查是否发送过状态，如果未发送过，不显示底部按钮，直到发送状态后显示
        if (!userManager.isSendedStatus()) {
            info.setVisibility(View.GONE);
            relationBtn.setVisibility(View.GONE);
            takePictureBtn.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");
//        mFov.stop();
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume");
        super.onResume();
        isViewValidate = false;
//        hideLoadingView();
    }

    @Override
    protected void onStart() {
        Log.e(TAG, "onStart");
        super.onStart();
        isViewValidate = true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.relation_btn:
                MoveToHelper.moveToRelation(ARMainActivity.this);
                break;
            case R.id.info_btn:
            case R.id.info_avatar_container:
            case R.id.info_alert_btn:
            case R.id.status_thumbnail:
                MoveToHelper.moveToPersonalHomepage(ARMainActivity.this);
                break;
            case R.id.overturn_camera:
                overturnCamera();
//                mFov.turnoverCamera();
                break;
            case R.id.take_picture_btn:
                takePictureBtn.setEnabled(false);
                arView.takePicture(this, new PictureCallback() {
                    @Override
                    public void onPictureTaken(String path) {
                        takePictureBtn.setEnabled(true);
                        MoveToHelper.moveToPreviewAndEditStatusViewForResult(ARMainActivity.this, REQUEST_CODE_STATUS_DATA, path);
                    }
                });
                break;
        }
    }

    private void overturnCamera() {
        if (arView.getCameraFacing() == Camera.CameraInfo.CAMERA_FACING_BACK) {
            arView.setCameraFacing(Camera.CameraInfo.CAMERA_FACING_FRONT);
        } else {
            arView.setCameraFacing(Camera.CameraInfo.CAMERA_FACING_BACK);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.log.e("resultCode",resultCode+"");
        // 状态预览界面返回数据
        if (requestCode == REQUEST_CODE_STATUS_DATA && resultCode == RESULT_OK) {
            String imgPath = data.getStringExtra(IntentKey.INTENT_KEY_STATUS_IMAGE_PATH);
            String text = data.getStringExtra(IntentKey.INTENT_KEY_TEXT);
            String coord = data.getStringExtra(IntentKey.INTENT_KEY_COORD);
            mPresenter.sendStatus(imgPath, text, coord);
            //获取新手引导本地存储
            //拍照正常返回，添加新手引导用户提示
            if (!isNewGuide) {
                final View statusView = View.inflate(this, R.layout.guide_send_state, null);
                guideUtil.initGuide(this, statusView);
                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        guideUtil.removeGuideView(statusView);
                    }
                }.sendEmptyMessageDelayed(0, 4000);


            }
        }
        //当拍照页发送状态返回时，不再调用状态引导
            if (resultCode == REQUEST_CODE_SHOW_BUTTON) {
                //显示下面三个按钮
                showPersonalAndRelationBtn();
                View statusView = View.inflate(ARMainActivity.this, R.layout.guide_scanning, null);
                guideUtil.initGuide(ARMainActivity.this, statusView);
                //返回后算发过状态,更新服务器数据
                userManager.saveSendedStatus();
            }

            showPersonalAndRelationBtn();

    }

    @Override
    public void refreshStatus(final ArrayList<Map> data) {
        Log.e(TAG, "refreshStatus:" + isFinishing());
        arView.refreshMarker(this, data);
    }

    @Override
    public void goToChatView(String targeUserId, String statusId) {
        Intent intent = new Intent(this, ChatActivity.class);
        // TODO: 调试聊天推送消息，将状态值改为常量
        intent.putExtra(IntentKey.INTENT_KEY_TARGET_UID, targeUserId);
        startActivity(intent);
    }

    @Override
    public void goToOtherHomePage(String targetUseId, String statusId) {
        MoveToHelper.moveToOtherHomepage(this, targetUseId, statusId);
    }

    @Inject
    AppStatusManager appStatusManager;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            DialogUtil.showConfirmDialog(this, "退出", "确定退出‘你在干嘛’？", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    ARMainActivity.this.finish();
                    System.exit(0);
                    Log.e("push", "App关闭");
                    appStatusManager.setAppStatus(AppStatusManager.APP_STATUS_CLOSED);
                }
            }, null, true);
        }
        return false;
    }

    @Override
    public void animToStartSendStatus() {
        animateOutView(infoAvatarContiner);
        animateInView(infoBtn);
    }

    @Override
    public void animToSendStatusSuccess() {
        if (!isNewGuide) {
            new Handler() {
                public void handleMessage(Message msg) {
                    animatePopupThumb();
                }
            }.sendEmptyMessageDelayed(0, 3500);
        } else {
            animatePopupThumb();
        }


    }

    @Override
    public void animToSendStatusError() {
        animateOutView(infoAlertBtn);
        animateInView(infoBtn);
    }


    @Override
    public void setUserAvatar(String url) {
        setNetImageToImageView(infoAvatar, url);
    }

    @Override
    public void showPersonalAndRelationBtn() {
        info.setVisibility(View.VISIBLE);
        relationBtn.setVisibility(View.VISIBLE);
        takePictureBtn.setVisibility(View.VISIBLE);

    }


    @Override
    public void setStatusThumbnail(String url) {
        setNetImageToImageView(statusThumb, url);
    }

    private void setNetImageToImageView(final ImageView imageView, String url) {
        ImageLoader.getInstance().loadImage(url, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                imageView.setImageResource(R.drawable.default_avatar);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                imageView.setImageBitmap(loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }


    private void animatePopupThumb() {

        statusThumb.clearAnimation();
        statusThumb.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_popup_thumb);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animToDismissThumbAndAvatar();
                statusThumb.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        statusThumb.setAnimation(animation);
        animation.start();
    }

    private void animToDismissThumbAndAvatar() {
        animateOutView(infoBtn);
        animateInView(infoAvatarContiner);
    }

    private void animateOutView(final View view) {
        view.clearAnimation();
        view.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_scale_out_simple);
        view.setAnimation(animation);
        animation.start();
    }

    private void animateInView(final View view) {
        view.clearAnimation();
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_scale_in_simple);
        view.setAnimation(animation);
        animation.start();
    }


    @Override
    public boolean onTouch(final View v, final MotionEvent event) {

        if (event != null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.e(TAG, "ACTION_DOWN");
                    break;
            }

        }
        return true;
    }


    @Override
    public void onCardboardTrigger() {
        super.onCardboardTrigger();
        Log.e(TAG, "onCardboardTrigger");
    }


    public void firstReceiveMessage() {
        GuideUtils guideUtil = GuideUtils.getInstance();
        //设置context，防止Null
        guideUtil.setContext(this);
        //调用引导界面
        View messageView = View.inflate(this, R.layout.guide_message, null);
        guideUtil.initGuide(this, messageView);
        PreferencesUtils.putBoolean(this, "messageGuide", false);
        mPresenter.unregisterReceiver();
    }


    public boolean isViewValidate() {
        return isViewValidate;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unregisterReceiver();
    }

    private void synchronousRemoteMessage() {
        Long time = conversationManager.getLocalLastMessageTime();
        messageManager.fetchUnreadMessageList(time, new ReceiveMessageCallback<List<ChatMessageContent>>() {
            @Override
            public void done(List<ChatMessageContent> message, Exception e) {
                LogUtil.log.e("countMessage", message.size() + "");
                doUnreadMessageView();

            }
        });

    }

    private void doUnreadMessageView() {
        if (messageManager.hasUnreadMessageInAll()) {
            unreadMessageView.setVisibility(View.VISIBLE);
        } else {
            unreadMessageView.setVisibility(View.INVISIBLE);
        }
    }


    private void setReadMessageListener() {
        conversationManager.setReadMessageChangedListener(new ConversationManager.ReadMessageChangedListener() {
            @Override
            public void onReadChanged() {
                doUnreadMessageView();
            }
        });
    }


}
