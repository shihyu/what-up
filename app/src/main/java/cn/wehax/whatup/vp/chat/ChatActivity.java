package cn.wehax.whatup.vp.chat;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.avos.avoscloud.LogUtil;
import com.google.inject.Inject;
import java.lang.reflect.Field;
import java.util.List;
import cn.wehax.util.DensityUtil;
import cn.wehax.util.DialogUtil;
import cn.wehax.whatup.R;
import android.graphics.Matrix;
import cn.wehax.whatup.config.Constant;
import cn.wehax.whatup.config.IntentKey;
import cn.wehax.whatup.framework.fragment.WXActivity;
import cn.wehax.whatup.model.chatView.ChatMessageContent;
import cn.wehax.whatup.model.chatView.GraffitiAction;
import cn.wehax.whatup.model.user.UserManager;
import cn.wehax.whatup.support.image.Config;
import cn.wehax.whatup.support.image.ImageUtils;
import cn.wehax.whatup.support.image.album.AlbumActivity;
import cn.wehax.whatup.support.image.camera.CameraActivity;
import cn.wehax.whatup.support.util.PreferencesUtils;
import cn.wehax.whatup.support.util.ToastUtils;
import cn.wehax.whatup.support.widget.ChooseImagePopWnd;
import cn.wehax.whatup.vp.chat.widget.ChatAdapter;
import cn.wehax.whatup.vp.chat.widget.ChatMessageList;
import cn.wehax.whatup.vp.chat.widget.ChatTopControlView;
import cn.wehax.whatup.vp.chat.widget.GraffitiControlView;
import cn.wehax.whatup.vp.chat.widget.GraffitiView;
import roboguice.inject.ContentView;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

/**
 * Created by howe on 15/5/26.
 * Email:howejee@gmail.com
 */
@ContentView(R.layout.activity_chat)
public class ChatActivity extends WXActivity implements IChatView, GraffitiView.OnGraffitiListener,
        View.OnClickListener, ChooseImagePopWnd.OnChooseListener,
        GraffitiControlView.OnGraffitiControlListener, ChatTopControlView.OnTopViewListener {

    //单位dp
    private static final int GRAFFITI_CONTROL_VIEW_MARGE_BOTTOM = 180;
    private static final int BOTTOM_LAYOUT_EXPAND_HEIGHT = 135;
    private static final int BOTTOM_LAYOUT_NORMAL_HEIGHT = 50;


    /**
     * 从图库或相册获取的图片质量
     */
    private final static int IMAGE_QUALITY = 80;

    @InjectView(R.id.chat_graffiti_view)
    GraffitiView mGraffitiView;

    @InjectView(R.id.chat_input_menu_switch)
    ImageView mMenuSwitch;

    @InjectView(R.id.rl_text_draw_bg)
    RelativeLayout rl_textDraw;

    private InputMethodManager imm;

    @InjectView(R.id.chat_content_list)
    ChatMessageList chat_list;

    @InjectView(R.id.chat_menu_layout)
    LinearLayout mMenuLayout;

    @InjectView(R.id.chat_menu_truth)
    LinearLayout mTruthBtn;

    @InjectView(R.id.chat_menu_change_bg)
    LinearLayout mChangeBgBtn;

    @InjectView(R.id.chat_bottom_layout)
    LinearLayout mBottomLayout;

    ChatTopControlView mTopControlView;

    GraffitiControlView mGraffitiControlView;

    @InjectView(R.id.chat_progress)
    LinearLayout mProgressLayout;

    @InjectView(R.id.chat_input_et)
    EditText mMessageEdit;

    @Inject
    ChatPresenter presenter;

    ChatAdapter chatAdapter;
    private String titleStr;
    private int sex = -1;
    private boolean isSoftKeyShow;
    private String avatarUrl;

    /**
     * 对方的id
     */
    @InjectExtra(IntentKey.INTENT_KEY_TARGET_UID)
    String targetUid;

    /**
     * 状态的id(从首页状态进来时传状态id)
     */
    String statusId;

    /**
     * 对话id(从关系进来时传对话id)
     */
    String conversationId;

    boolean systemRec;

    @Inject
    UserManager userManager;

    public int screenWidth;
    public int screenHeight;

    ChooseImagePopWnd chooseImagePopWnd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("chat", "chat onCreate");
        //当从首页点状态进来是传入statusId,从关系进来时传入conversationId
        statusId = getIntent().getStringExtra(IntentKey.INTENT_KEY_STATUS_ID);
        conversationId = getIntent().getStringExtra(IntentKey.INTENT_KEY_CONVERSATION_ID);
        systemRec = getIntent().getBooleanExtra(IntentKey.INTENT_KEY_SYSTEM_REC, false);
        avatarUrl  = getIntent().getStringExtra(IntentKey.INTENT_KEY_TARGET_AVATARURL);
        if (TextUtils.isEmpty(targetUid)) {
            Toast.makeText(this, R.string.chat_parameter_error, Toast.LENGTH_SHORT).show();
            this.finish();
        }
        LogUtil.log.e("chatActivityTargetUid",targetUid);
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();
        screenHeight = wm.getDefaultDisplay().getHeight();
        Log.e("chat", "Screen width=" + screenWidth + "  height=" + screenHeight);
        chatAdapter = new ChatAdapter(this, new ChatAdapter.OnButtonClickListen() {
            @Override
            public void onButtonClick(String msg,boolean flag) {
               if(flag){
                   presenter.sendTextMessage(msg);
               }else {
                showConfirmTruth(msg);
               }

            }
        });
        chat_list.setAdapter(chatAdapter);
        //chat_list.setEnabled(false);
        presenter.setView(this, userManager.getCurrentUser().getObjectId(), targetUid, conversationId, statusId, rl_textDraw, systemRec,avatarUrl);
        initView();
        mGraffitiView.setSize(5);
        mGraffitiView.setColor(getResources().getColor(R.color.chat_graffiti_red));
        mGraffitiView.setOnDrawListener(this);

    }


    private void initView() {
        mMenuSwitch.setOnClickListener(this);
        mTruthBtn.setOnClickListener(this);
        mChangeBgBtn.setOnClickListener(this);
        imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        mMessageEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {

                    //TODO:发送文本处理
                    String msg = mMessageEdit.getText().toString();

                    if (TextUtils.isEmpty(msg)) {
                        Toast.makeText(ChatActivity.this, R.string.chat_message_not_null, Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    presenter.sendTextMessage(msg);
                    mMessageEdit.setText("");

                }

                return false;
            }
        });

    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mBottomLayout.getViewTreeObserver().addOnGlobalLayoutListener(bottomLayoutGlobalListener);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mBottomLayout.getViewTreeObserver().removeGlobalOnLayoutListener(bottomLayoutGlobalListener);
    }

    //监听处理键盘弹出和消失
    ViewTreeObserver.OnGlobalLayoutListener bottomLayoutGlobalListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            // 键盘弹出时把涂鸦控制按钮隐藏
            mBottomLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int[] loc = new int[2];
                    mBottomLayout.getLocationOnScreen(loc);
                    if ((screenHeight - loc[1]) > (mBottomLayout.getHeight() * 3)) {
                        //键盘弹出
                        mGraffitiControlView.dismiss();
                        isSoftKeyShow= true;
                        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mGraffitiView.getLayoutParams();
                        if (mMenuLayout.getVisibility() == View.VISIBLE) {
                            mMenuLayout.setVisibility(View.GONE);
                            mMenuSwitch.setImageResource(R.drawable.menu_open_bg);
                            lp.height = screenHeight - DensityUtil.dp2px(ChatActivity.this, BOTTOM_LAYOUT_NORMAL_HEIGHT);
                            mGraffitiView.setLayoutParams(lp);
                            mGraffitiView.invalidate();
                            mGraffitiView.refresh();
                        }
                    } else {
                        //键盘消失
                        showGraffitiControlView();
                        isSoftKeyShow =false;
                    }
                    Log.e("chat", "Bottom size=" + loc[0] + "-" + loc[1]);
                }
            }, 100);
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chat_input_menu_switch:
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mGraffitiView.getLayoutParams();
                if (mMenuLayout.getVisibility() == View.VISIBLE) {
                    mMenuLayout.setVisibility(View.GONE);
                    mMenuSwitch.setImageResource(R.drawable.menu_open_bg);
                    lp.height = screenHeight - DensityUtil.dp2px(ChatActivity.this, BOTTOM_LAYOUT_NORMAL_HEIGHT);
                } else {
                    mMenuLayout.setVisibility(View.VISIBLE);
                    mMenuSwitch.setImageResource(R.drawable.menu_close_bg);
                    lp.height = screenHeight - DensityUtil.dp2px(ChatActivity.this, BOTTOM_LAYOUT_EXPAND_HEIGHT);
                    imm.hideSoftInputFromWindow(mMessageEdit.getWindowToken(), 0);
                }
                mGraffitiView.setLayoutParams(lp);
                mGraffitiView.invalidate();
                mGraffitiView.refresh();
                break;

            case R.id.chat_menu_truth:
                LogUtil.log.e("conversationId",conversationId);
                if(!TextUtils.isEmpty(conversationId)){

                }
               presenter.getTruth();
                break;

            case R.id.chat_menu_change_bg:
                if (chooseImagePopWnd == null) {
                    chooseImagePopWnd = new ChooseImagePopWnd(ChatActivity.this);
                    chooseImagePopWnd.setOnChooseListener(this);
                }
                chooseImagePopWnd.show(mGraffitiView);

                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unregisterReceiver();
        if (chooseImagePopWnd != null) {
            chooseImagePopWnd.dismiss();
            chooseImagePopWnd = null;
        }

        if (mTopControlView != null) {
            mTopControlView.dismiss();
            mTopControlView = null;
        }

        if (mGraffitiControlView != null) {
            mGraffitiControlView.dismiss();
            mGraffitiControlView = null;
        }
        Log.e("chat", "ChatActivity onDestroy");
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isSoftKeyShow) {
            imm.hideSoftInputFromWindow(mMessageEdit.getWindowToken(), 0);
            return true;
        } else {
            return mGraffitiView.onTouchEvent(event);
        }

    }
    /**
     * GraffitiView回调方法
     * 当涂鸦结束后回调
     */
    @Override
    public void onDrawEnd(GraffitiAction action) {
        presenter.sendMessage(action);
    }

    /**
     * GraffitiView回调方法
     * 涂鸦控件撤销成功后回调，用于发送消息给对方
     */
    @Override
    public void onLocalRevoke() {
        presenter.sendRevokeMessage();

    }


    /**
     * GraffitiView回调方法
     * surfaceView created时调用
     */
    @Override
    public void onViewCreated() {
        //该方法在SurfaceCreated后才调用，以保证涂鸦不会覆盖以下控件

        showGraffitiControlView();

        showTopControlView();

    }

    /**
     * 显示涂鸦操作按钮控件
     */
    private void showGraffitiControlView() {
        if (mGraffitiControlView == null) {
            mGraffitiControlView = new GraffitiControlView(
                    ChatActivity.this,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);

            mGraffitiControlView.setOnGraffitiControlListener(this);
        }

        mGraffitiControlView.showAtLocation(
                getWindow().getDecorView(),
                Gravity.RIGHT | Gravity.BOTTOM,
                0,
                DensityUtil.dp2px(this, GRAFFITI_CONTROL_VIEW_MARGE_BOTTOM));
    }

    /**
     * 显示顶部控件
     */
    private void showTopControlView() {
        if (mTopControlView != null) {
            mTopControlView.dismiss();
            mTopControlView = null;
        }

        mTopControlView = new ChatTopControlView(
                ChatActivity.this,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        mTopControlView.setOnTopViewListener(this);
        if (!TextUtils.isEmpty(titleStr)) {
            mTopControlView.setTitle(titleStr);
        }
        if (sex != -1) {
            mTopControlView.setSex(sex);
        }
        mTopControlView.showAtLocation(
                getWindow().getDecorView(),
                Gravity.TOP,
                0,
                getStatusBarHeight(this));
    }

    /**
     * TopControlView 回调方法
     * 返回按钮回调
     */
    @Override
    public void onBack() {
        ChatActivity.this.finish();
    }

    /**
     * GraffitiControlView回调方法
     * 撤销回调，调用涂鸦控件撤销方法
     */
    @Override
    public void onRevoke() {
        mGraffitiView.revokeLocal();
    }

    @Override
    public void onGraffitiSwitch(CheckBox graffitiSwitch) {
        boolean status = graffitiSwitch.isChecked();
        if (status) {
            ToastUtils.showToast(this, Constant.GRAFFITI_OFF);
            PreferencesUtils.putBoolean(this, "graffitiSwitch", true);

        } else {
            ToastUtils.showToast(this, Constant.GRAFFITI_ON);
            PreferencesUtils.putBoolean(this, "graffitiSwitch", false);
        }
        chat_list.setGraffitiSwitch(status);
        mGraffitiControlView.setControlViewAble(!status);

    }

    /**
     * GraffitiControlView回调方法
     * 选择涂鸦颜色回调
     */
    @Override
    public void onSelectColor(int colorResId) {
        mGraffitiView.setColor(getResources().getColor(colorResId));
    }


    /**
     * ChooseImagePopWnd回调方法
     * 打开相机
     */
    @Override
    public void openCamera() {

        Intent intent = new Intent(ChatActivity.this, CameraActivity.class);

        intent.putExtra(Config.INTENT_VALUE_NEED_CROP, false);

        ChatActivity.this.startActivityForResult(intent, Config.REQUEST_CODE_CALL_CAMERA);

    }

    /**
     * ChooseImagePopWnd回调方法
     * 打开相册
     */
    @Override
    public void openAlbum() {

        Intent intent = new Intent(ChatActivity.this, AlbumActivity.class);

        intent.putExtra(Config.INTENT_VALUE_NEED_CROP, false);

        ChatActivity.this.startActivityForResult(intent, Config.REQUEST_CODE_CALL_ALBUM);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                //获取图片返回
                case Config.REQUEST_CODE_CALL_CAMERA:
                case Config.REQUEST_CODE_CALL_ALBUM:


                    String path = ImageUtils.getLocalImagePath(ChatActivity.this) + "/" + ImageUtils.getImageName(Config.ENCODINGTYPE_JPEG);

                    byte[] result = null;

                    Log.e("crop", "data is null!!!!!!!!!!!");
                    Bitmap bitmap = ImageUtils.doImageFromPick(this, data.getData(),
                            path);
                    bitmap = ImageUtils.scaleBitmap(bitmap);
                    Log.e("IMAGE", "原图宽：" + bitmap.getWidth() + "  高：" + bitmap.getHeight());
                    result = ImageUtils.toBytes(bitmap, IMAGE_QUALITY);
                    if (result != null) {
                        presenter.uploadConversationBg(bitmap, result);
                    } else {
                        Toast.makeText(ChatActivity.this, R.string.chat_select_image_error, Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
        }
    }

    @Override
    public void setTitle(String title, int sex) {
        if (mTopControlView != null) {
            mTopControlView.setTitle(title);
            mTopControlView.setSex(sex);
        }
        titleStr = title;
        this.sex = sex;
    }

    @Override
    public void setBackgroundImage(Bitmap bitmap) {

      /*  Bitmap newBitmap =null;
        if(screenWidth>=bitmap.getWidth()||screenHeight>= bitmap.getHeight()){
            newBitmap = bitmap;
        }else {
            newBitmap = Bitmap.createBitmap(bitmap, 0, 0, screenWidth, screenHeight);
        }
        getWindow().setBackgroundDrawable(new BitmapDrawable(newBitmap));
        LogUtil.log.e("bitmap", "width:" + bitmap.getWidth() + ":" + "height" + bitmap.getHeight());*/
        //通知栏高度
       int i = getStatusBarHeight(this);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        int height = metric.heightPixels;   // 屏幕高度（像素）
        Bitmap newBitmap =bitmap;
        if(bitmap.getWidth()<width || bitmap.getHeight()<height){
            Matrix matrix = new Matrix();
            float scalX = (float)width/ bitmap.getWidth();
            float scalY =(float) height/ bitmap.getHeight();
            if(scalX>=scalY){
                matrix.postScale(scalX,scalX);
            }else {
                matrix.postScale(scalY,scalY);
            }
            newBitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        }
        if(newBitmap.getHeight()>=height && newBitmap.getWidth()>=width) {
            Bitmap bm1 = bitmap.createBitmap(newBitmap, 0, 0, width, height - i);
            Drawable bd = new BitmapDrawable(getResources(), bm1);
            getWindow().setBackgroundDrawable(bd);
        }else {
            getWindow().setBackgroundDrawable(new BitmapDrawable(bitmap));
        }



    }


    @Override
    public void showProgress() {
        mProgressLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mProgressLayout.setVisibility(View.GONE);


    }

    @Override
    public void addTextMessage(ChatMessageContent message) {
        chatAdapter.addMessage(message);
        chat_list.setSelection(chatAdapter.getDataList().size() - 1);
    }

    @Override
    public void addTextMessage(List<ChatMessageContent> chatMessageContents) {
        chatAdapter.addMessage(chatMessageContents);
        chat_list.setSelection(chatAdapter.getDataList().size() - 1);
    }

    @Override
    public void revokeRemoteGraffiti() {
        mGraffitiView.revokeRemote();
    }

    @Override
    public void clearGraffiti() {
        mGraffitiView.clear();
    }

    @Override
    public void refreshGraffiti() {
        mGraffitiView.refresh();
    }

    @Override
    public void drawLocalGraffiti(List<GraffitiAction> actions) {
        mGraffitiView.drawLocal(actions);
    }

    @Override
    public void drawRemoteGraffiti(List<Point> points, int color) {
        mGraffitiView.drawRemote(points, color);
    }

    public void showConfirmTruth(final String msg){
        DialogUtil.showConfirmDialog(this, Constant.TRUTH_ANSWER, Constant.TRUTH_ANSWER_DES, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                presenter.sendTextMessage(msg);
                dialogInterface.dismiss();
            }
        },null,false);
    }

    @Override
    public int getScreenWidth() {
        return screenWidth;
    }

    @Override
    public int getScreenHeight() {
        return screenHeight;
    }

    @Override
    public int getGraffitiViewWidth() {
        return mGraffitiView.getWidth();
    }

    @Override
    public int getGraffitiViewHeight() {
        return mGraffitiView.getHeight();
    }

    @Override
    public Activity getActivity() {
        return ChatActivity.this;
    }

    //通过反射拿到通知栏的高度
    public  int getStatusBarHeight(Context context){
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    @Override
    protected void onStart() {
        super.onStart();
        PreferencesUtils.putBoolean(this,"chatActivityShow",true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferencesUtils.putBoolean(this, "chatActivityShow", false);
    }
}
