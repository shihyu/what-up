package cn.wehax.whatup.vp.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FunctionCallback;
import com.avos.avoscloud.LogUtil;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import cn.wehax.util.DialogUtil;
import cn.wehax.whatup.R;
import cn.wehax.whatup.config.Constant;
import cn.wehax.whatup.config.IntentKey;
import cn.wehax.whatup.config.PreferenceKey;
import cn.wehax.whatup.framework.presenter.BasePresenter;
import cn.wehax.whatup.model.chat.MessageManager;
import cn.wehax.whatup.model.chat.QueryCallback;
import cn.wehax.whatup.model.chatView.ChatMessage;
import cn.wehax.whatup.model.chatView.ChatMessageContent;
import cn.wehax.whatup.model.chatView.GraffitiAction;
import cn.wehax.whatup.model.conversation.Conversation;
import cn.wehax.whatup.model.conversation.ConversationManager;
import cn.wehax.whatup.model.file.FileManager;
import cn.wehax.whatup.model.leancloud.LC;
import cn.wehax.whatup.model.leancloud.RemoteHelper;
import cn.wehax.whatup.model.status.Status;
import cn.wehax.whatup.model.status.StatusManager;
import cn.wehax.whatup.model.user.UserManager;
import cn.wehax.whatup.support.db.DBSupportHelper;
import cn.wehax.whatup.support.util.PreferencesUtils;
import cn.wehax.whatup.vp.chat.widget.BubbleView;

/**
 * Created by howe on 15/5/26.
 * Email:howejee@gmail.com
 */
public class ChatPresenter extends BasePresenter<IChatView> {


   /* @Inject
    ChatManager chatManager;
*/

    @Inject
    FileManager fileManager;

    @Inject
    RemoteHelper remoteHelper;

    @Inject
    MessageManager messageManager;

    @Inject
    ConversationManager conversationManager;

    @Inject
    ChatUtils chatUtils;
    @Inject
    UserManager userManager;

    @Inject
    StatusManager statusManager;

    @Inject
    DBSupportHelper dbSupportHelper;

    //当前对话
    AVIMConversation curConversation;

    //对方的id
    private String targetUid;

    //自己id
    private String selfUid;

    //是否为系统推荐用户
    boolean systemRec;

    //背景图片id
    private String conversationId;

    private String statusId;

    private DisplayImageOptions options;

    RelativeLayout rl_textDraw;


    //对方用户头像
    private String targetAvatar;
    //状态文字
    private String text;

    //消息广播接收器
    private MessageReceiver messageReceiver;

    //TODO：对应混合版，可改成其他
    private String selfAvatarUrl = "img/default_avatar.png";


    Gson mGson = new Gson();


    public void setView(IChatView view, String selfUid, String targetUid, String bgId, String statusId, RelativeLayout rl_textDraw, boolean systemRec,String targetAvatar) {
        super.setView(view);
        this.targetUid = targetUid;
        this.selfUid = selfUid;
        this.conversationId = bgId;
        this.statusId = statusId;
        this.targetAvatar =targetAvatar;
        this.systemRec = systemRec;
        AVFile avFile = userManager.getCurrentUser().getAVFile("avatar");
        if (avFile == null) {
            this.selfAvatarUrl = "img/default_avatar.png";
        } else {
            this.selfAvatarUrl = avFile.getUrl();
        }
        this.rl_textDraw = rl_textDraw;
        init();
    }

    //初始化
    public void init() {

        if (!conversationManager.isChatConnect()) {
            //TODO:处理网络断开
           conversationManager.recoverConnectNet();

        }
        options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
        getTargetNicknameAndSex();
        //注册消息监听器
        registerMessageReceiver();
        doInChatPager();

    }

    private void getTargetNicknameAndSex() {
        remoteHelper.queryAVUser(targetUid, new QueryCallback<AVUser>() {
            @Override
            public void done(AVUser result, boolean isLocal, Exception e) {
                if (e == null) {
                    String nickname = result.getString("nickname");
                    int sex = result.getInt(LC.table.User.sex);
                    if (!TextUtils.isEmpty(nickname)) {
                        mView.setTitle(nickname, sex);
                    }
                    AVFile avatarFile = result.getAVFile("avatar");
                    if (avatarFile != null) {
                        targetAvatar = avatarFile.getUrl();
                    }

                }
            }
        });
    }

    /**
     * 从关系进来
     */
/*    private void doFromRelation() {
        //获取对话信息
        final Conversation conv = dbSupportHelper.queryLocalConversationById(conversationId);

        if (conv == null) {  //关系页时来，可能没有本地聊天记录
         chatManager.queryAVIMConversation(targetUid, new QueryCallback<AVIMConversation>() {
                @Override
                public void done(AVIMConversation avimConversation, boolean isLocal, Exception e) {
                    if (e != null) {
                        if (e instanceof ChatManager.ConversationNotExistException) {
                        } else {
                            doConversationReturnFailure(e);
                        }
                    } else {

                        String[] queryIncludes = {"backgroundFile", "status"};
                        chatManager.queryAVObject(conversationId, chatManager.LC_TABLE_CONVERSATION, queryIncludes, new QueryCallback<AVObject>() {
                            @Override
                            public void done(AVObject result, boolean isLocal, Exception e) {
                                if (e == null) {

                                    AVObject avObject = result.getAVObject("status");
                                    AVFile backgroundFile = result.getAVFile("backgroundFile");
                                    loadImageByUrl(backgroundFile.getUrl());
                                    if (avObject != null) {
                                        text = avObject.getString("text");
                                    }
                                }
                                //增加系统推荐提示
                                if (systemRec) {
                                    addSystemMessage(targetAvatar, text);
                                }

                            }
                        });
                        loadLocalData(conversationId);
                        drawText();
                        doConversationReturnSuccess(avimConversation, null);

                    }
                }
            });*//*
            conversationManager.startConversation(ConversationManager.STRATEGY_HYBRID, targetUid, null, new QueryCallback<AVIMConversation>() {
                @Override
                public void done(AVIMConversation avimConversation, boolean isLocal, Exception e) {
                    if (e != null) {
                        if (e instanceof ChatManager.ConversationNotExistException) {
                        } else {
                            doConversationReturnFailure(e);
                        }
                    } else {

                        String[] queryIncludes = {"backgroundFile", "status"};
                        chatManager.queryAVObject(conversationId, chatManager.LC_TABLE_CONVERSATION, queryIncludes, new QueryCallback<AVObject>() {
                            @Override
                            public void done(AVObject result, boolean isLocal, Exception e) {
                                if (e == null) {

                                    AVObject avObject = result.getAVObject("status");
                                    AVFile backgroundFile = result.getAVFile("backgroundFile");
                                    loadImageByUrl(backgroundFile.getUrl());
                                    if (avObject != null) {
                                        text = avObject.getString("text");
                                    }
                                }
                                //增加系统推荐提示
                                if (systemRec) {
                                    addSystemMessage(targetAvatar, text);
                                }

                            }
                        });
                        loadLocalData(conversationId);
                        drawText();
                        doConversationReturnSuccess(avimConversation, null);
                    }
                }
            });

        }
        if (conv != null) {
            if (!TextUtils.isEmpty(conv.getStatusImageUrl())) {
                //当状态背景与当前背景一致时，添加状态文本
                if (conv.getStatusImageUrl().equalsIgnoreCase(conv.getBgUrl())) {
                    //TODO:处理跟状态一致的情况
                    this.statusId = conv.getStatusId();
                    drawText();
                }
            }
            //TODO:获取聊天历史，获取对话
            loadLocalData(conv.getConversationId());
            loadImageByUrl(conv.getBgUrl());
            connect();

        }
    }


    //处理从状态进来
    private void doFromStatus() {
        final Conversation conv = chatManager.queryLocalConversationByStatusId(statusId);
        if (conv != null && !TextUtils.isEmpty(conv.getStatusImageUrl()) && conv.getStatusImageUrl().equals(conv.getBgUrl())) {
            //如果本地存在，则绘制
            loadLocalData(conv.getConversationId());
            loadImageByUrl(conv.getBgUrl());
            drawText();
            connect();
            return;
        }

        //本地不存在时从网上获取

        //先获取状态
        statusManager.queryStatusById(statusId, new QueryCallback<Status>() {
            @Override
            public void done(Status result, boolean isLocal, Exception e) {
                if (e != null) {
                    //TODO:没查找到或出错处理
                } else {
                    //显示背景
                    loadImageByUrl(result.getImageUrl());
                    drawText();
                    //状态获取成功，创建对话
                    connect(result);
                }
            }
        });
    }*/

    private void doInChatPager(){
        if(TextUtils.isEmpty(targetUid)){
            return;
        }
        if(!TextUtils.isEmpty(statusId)){
            final Conversation conv = dbSupportHelper.queryLocalConversationByStatusId(statusId);
            if (conv != null && !TextUtils.isEmpty(conv.getStatusImageUrl()) && conv.getStatusImageUrl().equals(conv.getBgUrl())) {
                //如果本地存在，则绘制
                loadLocalData(conv.getConversationId());
                loadImageByUrl(conv.getBgUrl());
                drawText();
                connect();
                return;
            }
        }
        if(!TextUtils.isEmpty(conversationId)){
            final Conversation conv = dbSupportHelper.queryLocalConversationById(conversationId);
            if (conv != null) {
                if (!TextUtils.isEmpty(conv.getStatusImageUrl())) {
                    //当状态背景与当前背景一致时，添加状态文本
                    if (conv.getStatusImageUrl().equalsIgnoreCase(conv.getBgUrl())) {
                        //TODO:处理跟状态一致的情况
                        this.statusId = conv.getStatusId();
                        drawText();
                    }
                }
                //TODO:获取聊天历史，获取对话
                loadLocalData(conv.getConversationId());
                loadImageByUrl(conv.getBgUrl());
                connect();
                return;

            }

        }

        conversationManager.startConversation(ConversationManager.STRATEGY_HYBRID, targetUid, statusId, new QueryCallback<Conversation>() {
            @Override
            public void done(Conversation conversation, boolean isLocal, Exception e) {
                    loadImageByUrl(conversation.getBgUrl());
                    if(TextUtils.isEmpty(conversationId)){
                        conversationId =conversation.getConversationId();
                        loadLocalData(conversationId);
                    }

                if (systemRec) {
                    addSystemMessage(targetAvatar, text);
                }
                drawText();
                doConversationReturnSuccess(conversation.getAvimConversation(), null);

            }
        });

    }

    //绘制状态文本
    private void drawText() {
        conversationManager.getStatusByRemote(targetUid, new FunctionCallback<Map>() {
            @Override
            public void done(Map maps, AVException e) {
                LogUtil.log.e("getStatusByRemote", maps.toString());
                boolean isStatus = false;
                if (maps != null && maps.size() > 0) {
                    if (maps.containsKey(LC.method.LoadTargetUserConversation.keyIsStatus)) {
                        isStatus = (Boolean) maps.get(LC.method.LoadTargetUserConversation.keyIsStatus);
                    }
                }
                if (isStatus && maps != null) {
                    Map coord = (Map) maps.get(LC.method.LoadTargetUserConversation.keyStatusTextCoord);
                    String text = (String) maps.get(LC.method.LoadTargetUserConversation.keyStatusText);
                    double x = 0.0;
                    double y = 0.0;
                    if (coord != null && text != null) {
                        try {
                            x = ((BigDecimal) coord.get("x")).doubleValue();
                            y = ((BigDecimal) coord.get("y")).doubleValue();
                        }catch (Exception exception){

                        }

                    }

                    WindowManager wm = (WindowManager) getActivity().getSystemService(getActivity().WINDOW_SERVICE);
                    int screenWidth = wm.getDefaultDisplay().getWidth();
                    int screenHeight = wm.getDefaultDisplay().getHeight();
                    double Xpx = screenWidth * x;
                    double Ypx = screenHeight * y;
                    if (rl_textDraw != null) {
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.topMargin = (int) Ypx;
                        params.leftMargin = (int) Xpx;
                        TextView drawText = new TextView(getActivity());
                        drawText.setTextColor(Color.WHITE);
                        drawText.setText(text);
                        rl_textDraw.addView(drawText, params);

                    }

                }

            }
        });
    }


    /**
     * 创建对话或登录聊天服务
     */
    private void connect() {
        this.connect(null);
    }

    /**
     * 创建对话或登录聊天服务
     *
     * @param status 新创建对话需要
     */
    private void connect(Status status) {
        if (conversationManager.isLogin()) {
            //已登录就直接进入聊天室
            queryOrCreateConversation(status);

        } else {
            login(status);
        }
    }

    private void login(final Status status) {

        conversationManager.login(selfUid, new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                if (e != null) {
                    e.printStackTrace();

                    Toast.makeText(mView.getActivity(), "登录失败", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mView.getActivity(), "登录成功", Toast.LENGTH_SHORT).show();
                    queryOrCreateConversation(status);
                }
            }
        });
    }

    //创建对话
    private void queryOrCreateConversation(final Status status) {


        /*chatManager.queryAVIMConversation(targetUid, new QueryCallback<AVIMConversation>() {
            @Override
            public void done(AVIMConversation avimConversation, boolean isLocal, Exception e) {
                if (e != null) {
                    if (e instanceof ChatManager.ConversationNotExistException && status != null) {
                        //不存在则创建新的
                        createConversation(targetUid, status);
                    } else {
                        doConversationReturnFailure(e);
                    }
                } else {
                    doConversationReturnSuccess(avimConversation, status);

                }
            }
        });*/
        conversationManager.startConversation(ConversationManager.STRATEGY_HYBRID, targetUid, null, new QueryCallback<Conversation>() {
            @Override
            public void done(Conversation conversation, boolean isLocal, Exception e) {
                if (e != null) {
                    if ( status != null) {
                        //不存在则创建新的
                        //createConversation(targetUid, status);

                    } else {
                        doConversationReturnFailure(e);
                    }
                } else {
                    doConversationReturnSuccess(conversation.getAvimConversation(), status);

                }
            }
            });
    }

/*    private void createConversation(String targetUid, final Status status) {
        chatManager.createAVIMConversation(targetUid, status, new QueryCallback<AVIMConversation>() {
            @Override
            public void done(AVIMConversation avimConversation, boolean isLocal, Exception e) {
                if (e != null) {
                    doConversationReturnFailure(e);
                } else {
                    doConversationReturnSuccess(avimConversation, status);
                }
            }
        });
    }*/

    private void doConversationReturnSuccess(AVIMConversation avimConversation, Status status) {
        //TODO:修改状态，如隐藏进度条
        mView.hideProgress();
        // Toast.makeText(mView.getActivity(), "创建对话成功！！", Toast.LENGTH_SHORT).show();

        //更新本次关系会话为已读
        conversationManager.setConversationLastRead(System.currentTimeMillis(),avimConversation.getConversationId());
        curConversation = avimConversation;
        if (status != null) {
            //将状态更新到对话
            //从首页进来会触发
            updateConversationWithStatus(avimConversation.getConversationId(), status);
        }
    }

    private void doConversationReturnFailure(Exception e) {
        //如果这里接收到ConversationNotExistException，说明登录出问题了！！
        if (e != null) {
            //TODO:检查登录或其他处理
            Log.e("chat", "登录出现问题了或者连接断开");
            return;
        }
        Toast.makeText(mView.getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        Log.e("chat", "获取对话出错：" + e.toString());
        getActivity().finish();
        //TODO：处理对话获取失败
    }

    public void loadLocalData(String conversationId) {
        List<ChatMessage> list = dbSupportHelper.queryLocalMessages(conversationId);
        if (list != null) {
            chatUtils.doHistoryMessage(list, mView, targetAvatar);

        }
    }

    private void updateConversationWithStatus(final String conversationId, final Status status) {
        //加载当前背景，聊天内容等
        String[] queryIncludes = {"initBackground", "backgroundFile", "status", "imageData"};
        remoteHelper.queryAVObject(conversationId, ConversationManager.LC_TABLE_CONVERSATION, queryIncludes, new QueryCallback<AVObject>() {
            @Override
            public void done(AVObject result, boolean isLocal, Exception e) {
                if (e != null || result == null) {
                    //TODO:处理错误
                } else {

                    AVFile bg = result.getAVFile("backgroundFile");
                    //对比状态图片地址是否与对话当前背景地址一致，不一致更新为状态地址和更新对话当前状态
                    if (bg == null || !bg.getUrl().equals(status.getImageUrl())) {

                        //更新对话里的当前状态
                        HashMap<String, Object> data = new HashMap<String, Object>();
                        data.put("status", AVObject.createWithoutData("AllStatus", statusId));
                        data.put("backgroundFile", AVObject.createWithoutData("_File", status.getImageId()));
                        remoteHelper.updateAVObject(result, data);

                        //发送更换背景
                        sendChangeBgMessage(status.getImageUrl());
                        Log.e("chat", "改变背景");
                    } else {
                        //一致时不做处理
                    }
                }
            }
        });


    }

    /**
     * 注册消息接收器
     */
    private void registerMessageReceiver() {
        messageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.RECEIVE_MESSAGE_ACTION);
        mView.getActivity().registerReceiver(messageReceiver, filter);
    }

    public void unregisterReceiver() {
        if (messageReceiver != null) {
            mView.getActivity().unregisterReceiver(messageReceiver);
        }
    }


    private void loadImageByUrl(String url) {
        ImageLoader.getInstance().loadImage(url, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mView.setBackgroundImage(loadedImage);
                mView.refreshGraffiti();
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }


    private class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }

            if (Constant.RECEIVE_MESSAGE_ACTION.equals(action)) {
                String convId = intent.getStringExtra(IntentKey.INTENT_KEY_CONVERSATION_ID);
                if (TextUtils.isEmpty(convId)
                        || curConversation == null
                        || !convId.equals(curConversation.getConversationId())) {
                    return;
                }



                //处理收到的消息
                ChatMessageContent msg = (ChatMessageContent) intent.getSerializableExtra(IntentKey.INTENT_KEY_AVIMMESSAGE);

                conversationManager.setConversationLastRead(msg.getTime(),convId);

                msg.setAvatar(targetAvatar);
                if (ChatMessageContent.TYPE_GRAFFITI.equals(msg.getType())) {
                    //处理涂鸦消息
                    drawRemote(msg, Color.parseColor(msg.getStrokeStyle()));

                } else if (ChatMessageContent.TYPE_TEXT.equals(msg.getType()) || ChatMessageContent.TYPE_TRUTH.equals(msg.getType())) {
                    //处理文本消息
                    mView.addTextMessage(msg);

                } else if (ChatMessageContent.TYPE_GRAFFITI_REVOKE.equals(msg.getType())) {
                    //处理涂鸦撤销
                    mView.revokeRemoteGraffiti();

                } else if (ChatMessageContent.TYPE_CHANGE_BG.equals(msg.getType())) {
                    //处理更换背景
                    String url = msg.getUrl();
                    loadImageByUrl(url);
                    //清空原有涂鸦
                    mView.clearGraffiti();

                } else if (ChatMessageContent.TYPE_TRUTH.equals(msg.getType())) {
                    //增加时间显示
//                    msg.setTime(message.getTimestamp());
//                    LogUtil.log.e("time", message.getTimestamp() + "");
                    //处理真心话功能
                    mView.addTextMessage(msg);

                }
            }
        }
    }

    private List<Point> getPointsInMessage(ChatMessageContent message) {
        List<Point> points = new ArrayList<>();

        for (Integer i : message.getData()) {
            float[] point = ChatUtils.unzipData(i);
            float[] imgPointRatio = ChatUtils.convertRemoteCoordToImageCoordRatio(point, 0.662f, message.getScreenWidth(), message.getScreenHeight());
            int[] localPoint = ChatUtils.convertImageCoordToLoclCoord(imgPointRatio, 0.662f, mView.getScreenWidth(), mView.getScreenHeight());

            points.add(new Point(localPoint[0], localPoint[1]));

        }
        return points;
    }


    private void drawRemote(ChatMessageContent message, int color) {
        List<Point> points = getPointsInMessage(message);
        mView.drawRemoteGraffiti(points, color);
    }

    public void sendMessage(GraffitiAction action) {
        ChatMessageContent content = new ChatMessageContent();
        content.setType(ChatMessageContent.TYPE_GRAFFITI);
        content.setScreenWidth(mView.getGraffitiViewWidth());
        content.setScreenHeight(mView.getGraffitiViewHeight());
        String colorStr = String.format("#%06X", 0xFFFFFF & action.getColor());

        content.setStrokeStyle(colorStr);
        List<Integer> data = new ArrayList<>();
        int time = 0;
        for (int i = 0; i < action.getPoints().size(); i++) {
            Point p = action.getPoints().get(i);
            int value = ChatUtils.zipData(p.x, p.y, time);
            data.add(value);
            time += 4;
        }
        content.setData(data);
        String msg = mGson.toJson(content);
        Log.e("chat", "###組裝后的message=" + msg);
        sendMessage(curConversation, msg, null);
    }

    public void sendRevokeMessage() {
        ChatMessageContent content = new ChatMessageContent();
        content.setType(ChatMessageContent.TYPE_GRAFFITI_REVOKE);
        String msg = mGson.toJson(content);
        Log.e("chat", "###組裝后的message=" + msg);
        sendMessage(curConversation, msg, null);
    }

    public void sendChangeBgMessage(String url) {
        ChatMessageContent content = new ChatMessageContent();
        content.setType(ChatMessageContent.TYPE_CHANGE_BG);
        content.setUrl(url);
        String msg = mGson.toJson(content);
        sendMessage(curConversation, msg, null);
        mView.clearGraffiti();
    }

    public void sendTextMessage(String text) {

        String bubbleStyle = PreferencesUtils.getString(mView.getActivity(), PreferenceKey.PREFERENCE_KEY_BUBBLE_STYLE, BubbleView.BubbleStyle.BUBBLE_HAMBURG);
        ChatMessageContent content = new ChatMessageContent();
        content.setType(ChatMessageContent.TYPE_TEXT);
        content.setText(text);
        content.setAvatar(selfAvatarUrl);
        content.setBubbleStyle(bubbleStyle);
        content.setTime(System.currentTimeMillis());
        content.setSelf(true);
        String msg = mGson.toJson(content);
        sendMessage(curConversation, msg, null);
        mView.addTextMessage(content);
    }

    //重载方法，发送真心话消息。
    public void sendTextMessage(String text, String answer) {
        //从本地设置获取气泡样式
        String bubbleStyle = PreferencesUtils.getString(mView.getActivity(), PreferenceKey.PREFERENCE_KEY_BUBBLE_STYLE, BubbleView.BubbleStyle.BUBBLE_HAMBURG);
        ChatMessageContent content = new ChatMessageContent();
        content.setType(ChatMessageContent.TYPE_TRUTH);
        content.setQuestion(text);
        content.setAvatar(selfAvatarUrl);
        content.setAnswer(answer);
        content.setSelf(true);
        content.setTime(System.currentTimeMillis());
        content.setBubbleStyle(bubbleStyle);
        String msg = mGson.toJson(content);
        sendMessage(curConversation, msg, null);
        mView.addTextMessage(content);
    }

    private void sendMessage(AVIMConversation avimConversation, String msg, QueryCallback callback) {
        if (avimConversation == null || !conversationManager.isChatConnect()) {
            Toast.makeText(mView.getActivity(), R.string.chat_message_not_send, Toast.LENGTH_SHORT).show();
            conversationManager.recoverConnectNet();
            return;
        }

        messageManager.sendMessage(curConversation, msg, callback);
    }


    public void uploadConversationBg(Bitmap bitmap, final byte[] imgData) {
        if (curConversation == null || !conversationManager.isChatConnect()) {
            Toast.makeText(mView.getActivity(), R.string.chat_upload_failure, Toast.LENGTH_SHORT).show();
            //TODO:重连操作
            return;
        }
        mView.setBackgroundImage(bitmap);
        mView.refreshGraffiti();

        remoteHelper.queryAVObject(
                curConversation.getConversationId(),
                ConversationManager.LC_TABLE_CONVERSATION,
                null,
                new QueryCallback<AVObject>() {
                    @Override
                    public void done(AVObject avObject, boolean isLocal, Exception e) {
                        if (e != null) {
                            Log.e("chat", "updateConversatonbg 获取Conversation失败：" + e.toString());
                        } else {
                            String fileName = System.currentTimeMillis() + ".jpeg";
                            final AVFile avFile = new AVFile(fileName, imgData);

                            avObject.put("backgroundFile", avFile);
                            avObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if (e == null) {
                                        sendChangeBgMessage(avFile.getUrl());
                                        Log.e("chat", "对话更新成功" + avFile.getUrl());
                                    } else {
                                        Log.e("chat", "ono，上传失败");
                                    }
                                }
                            });
                        }
                    }
                });


    }

   public void getTruth() {
       messageManager.fetchTruthData(conversationId, new QueryCallback<Map>() {
           @Override
           public void done(Map result, boolean isLocal, Exception e) {
               if(result != null && result.size()>0){

                   final String question =(String)result.get(LC.method.GetTruth.keyQuestion);
                   final String answer =(String)result.get(LC.method.GetTruth.keyAnswer);
                   boolean isFirst = PreferencesUtils.getBoolean(getActivity(), targetUid, true);
                   if (isFirst) {
                       DialogUtil.showConfirmDialog(getActivity(), Constant.TRUTH_ACTION, Constant.TRUTH_DES, new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {
                               sendTextMessage(question, answer);
                               PreferencesUtils.putBoolean(getActivity(), targetUid, false);
                               dialogInterface.dismiss();

                           }
                       }, null, false);
                   } else {
                       sendTextMessage(question, answer);
                   }
               }
           }
       });
    }

    public void addSystemMessage(String targetAvatarUrl, String statusText) {
        ChatMessageContent messageContent = new ChatMessageContent();
        messageContent.setType(ChatMessageContent.TYPE_TEXT);
        messageContent.setTime(System.currentTimeMillis());
        messageContent.setSelf(false);
        messageContent.setIsSystemMsg(true);
        if (TextUtils.isEmpty(statusText)) {
            messageContent.setText(getActivity().getResources().getString(R.string.chat_release_pic));
        } else {
            messageContent.setText(statusText);
        }
        if (!TextUtils.isEmpty(targetAvatarUrl)) {
            messageContent.setAvatar(targetAvatarUrl);
        }
        mView.addTextMessage(messageContent);
    }

    //处理进入聊天页面会话ID或状态ID都为空的情况
   /* public void doFromTarget(String targetUid) {
        Conversation con = chatManager.queryLocalConversationByTargetId(targetUid);
        if (con != null) {
            conversationId = con.getConversationId();
            doFromRelation();
        } else {
            chatManager.getStatusByRemote(targetUid, new FunctionCallback<Map>() {
                @Override
                public void done(Map map, AVException e) {
                    if (map != null && map.size() > 0) {
                        conversationId = (String) map.get(LC.method.LoadTargetUserConversation.keyId);
                        doFromRelation();
                    } else {
                        getActivity().finish();
                    }
                }
            });
        }

    }*/


}
