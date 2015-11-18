package cn.wehax.whatup.model.conversation;

import android.text.TextUtils;
import android.util.Log;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FunctionCallback;
import com.avos.avoscloud.LogUtil;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMClientEventHandler;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import cn.wehax.whatup.model.chat.MessageManager;
import cn.wehax.whatup.model.chat.QueryCallback;
import cn.wehax.whatup.model.chatView.ChatMessage;
import cn.wehax.whatup.model.leancloud.LC;
import cn.wehax.whatup.support.db.DBSupportHelper;
import cn.wehax.whatup.support.db.DatabaseManager;


@Singleton
public class ConversationManager implements IConversationManager {
    private final String TAG = "ConversationManager";

    public static final String LC_TABLE_CONVERSATION = "_Conversation";
    //查询方式1本地查询
    public static final int STRATEGY_LOCAL = 1;
    //查询方式2远程查询
    public static final int STRATEGY_REMOTE = 2;
    //查询方式3混合查询
    public static final int STRATEGY_HYBRID = 3;

    //本地聊天登录客户端对象
    AVIMClient imClient;

    //聊天连接是否可用
    private boolean isConnect = false;

    //本地是否登录
    private boolean isLogin = true;

    @Inject
    DBSupportHelper dbSupportHelper;

    @Inject
    MessageManager messageManager;

    @Inject
    DatabaseManager databaseManager;

    @Inject
    ConversationConverter conversationConverter;

    private ReadMessageChangedListener readMessageChangedListener;

    private String selfUid;

    //用户保存地址会话对象
    HashMap<String, AVIMConversation> avConversationCache = new HashMap<>();

    //用户有效关系列表，提供AVIMConversation对象
    List<Conversation> conversationList = new ArrayList<>();

    ConversationManager() {
        init();
    }

    private void init() {
        //初始化监视网络状态
        AVIMClient.setClientEventHandler(new CustomEventHandler());


    }

    @Override
    public void login(String selfId, AVIMClientCallback callback) {
        if (imClient != null) {
            logout();
        }
        if (TextUtils.isEmpty(selfId)) {
            selfId = AVUser.getCurrentUser().getObjectId();
        }
        this.selfUid = selfId;
        messageManager.initReceiveMessage();
        imClient = AVIMClient.getInstance(selfId);
        imClient.open(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                if (e != null) {
                    isConnect = false;
                    isLogin = false;
                } else {
                    isConnect = true;
                    isLogin = true;
                    //设置关系数据监听
                    messageManager.setOnConversationsDataChangeListener(new MessageManager.OnConversationsDataChangeListener() {
                        @Override
                        public void onConversationsDataChanged(final ChatMessage message) {
                            dbSupportHelper.saveConversationToDB(message, new DBSupportHelper.ConversationSaveCallback() {
                                @Override
                                public void onSaveSuccess() {
                                    if(!message.isSelf()){
                                        setConversationUnread(message.getTimestamp(), message.getConversationId());
                                    }

                                }
                            });

                            LogUtil.log.e("ChatMessage",message.getContent());
                        }
                    });
                }
            }
        });
        //清楚缓存的对话
        avConversationCache.clear();

    }

    @Override
    public void logout() {
        if (imClient == null) {
            return;
        }
        imClient.close(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                if (e != null) {
                    //退出失败
                    Log.e("chat", "聊天连接关闭失败：" + e.toString());
                }
            }
        });
        imClient = null;
        //清楚缓存的对话
        avConversationCache.clear();
    }

    @Override
    public void fetchConversationList(int strategy, final QueryCallback<List<Conversation>> callback) {
        switch (strategy) {
            //查询本地数据库中的数据
            case ConversationManager.STRATEGY_LOCAL:
                callback.done(dbSupportHelper.queryLocalConversations(), true, null);
                break;
            //通过云代码查询远程服务器上数据
            case ConversationManager.STRATEGY_REMOTE:
                getUserConversations(new FunctionCallback<List<Map>>() {
                    @Override
                    public void done(List<Map> maps, AVException e) {
                        List<Conversation> remoteList = conversationConverter.converterRemoteList(maps);
                        callback.done(remoteList, false, e);
                    }
                });
                break;
            //按照要求查询本地和服务器数据后显示数据
            case ConversationManager.STRATEGY_HYBRID:
                final List<Conversation> userRelationsList = new ArrayList();
                getUserConversations(new FunctionCallback<List<Map>>() {
                    @Override
                    public void done(List<Map> maps, AVException e) {
                        if (maps != null) {
                            List<Conversation> remoteList = conversationConverter.converterRemoteList(maps);
                            userRelationsList.clear();
                            for (Conversation conversation : remoteList) {
                                String conversationsId = conversation.getConversationId();
                                //查询本地和后台数据后，对无效数据进行过滤，最后返回有效数据，展示给用户
                                Conversation localConversation =dbSupportHelper.queryLocalConversationById(conversationsId);
                                if (localConversation!= null || conversation.isTemp()) {
                                    if(localConversation!= null && localConversation.hasUnread()){
                                        conversation.setHasUnread(true);
                                    }
                                    userRelationsList.add(conversation);
                                }
                            }

                        }

                        callback.done(userRelationsList, false, e);
                    }
                });
                break;

        }
    }

    private void getUserConversations(FunctionCallback<List<Map>> callback) {
        Map<String, Object> params = new HashMap<>();
        AVCloud.callFunctionInBackground(LC.method.GetMyConversationList.functionName, params, callback);
    }


    @Override
    public void startConversation(int strategy, final String targetId, String allStatusId, final QueryCallback<Conversation> callback) {
        if (TextUtils.isEmpty(this.selfUid) || imClient == null) {
            throw new RuntimeException("ChatManager NullPointException:selfUid is null or client is null,please call cellLogin first");
        }

        if (TextUtils.isEmpty(targetId)) {
            throw new RuntimeException("ChatManager NullPointException:target is null");
        }

        getStatusByRemote(targetId, allStatusId, new FunctionCallback<Map>() {
            @Override
            public void done(Map map, AVException e) {
                Conversation conversation = new Conversation();
                AVIMConversation avimConversation = null;
                if (map != null && map.containsKey(LC.method.LoadTargetUserConversation.keyId)) {
                    String conversationId = (String) map.get(LC.method.LoadTargetUserConversation.keyId);
                    if (avConversationCache.containsKey(targetId)) {
                        //TODO:本地缓存
                        avimConversation = avConversationCache.get(targetId);
                    } else {
                        avimConversation = imClient.getConversation(conversationId);
                    }
                    conversation.setAvimConversation(avimConversation);
                    conversation.setConversationId(conversationId);
                    AVFile backgroundFile = (AVFile) map.get(LC.method.LoadTargetUserConversation.keyBackgroundImage);
                    if (backgroundFile != null) {
                        conversation.setBgUrl(backgroundFile.getUrl());
                    }

                    callback.done(conversation, false, e);
                    avConversationCache.put(targetId, avimConversation);
                }
            }
        });

    }


    /**
     * 云代码方法：loadTargetUserConversation
     * 参数：
     * targetId：需要对话的目标用户
     * statusId：需要使用的AllStatus的Id，可选。如果不填，优先使用已有对话里保存的backgroundFile或者status来获取背景信息。如此两项也都不存在（新对话），使用对方最新发布的AllStatus来设置背景信息。
     * 返回：目标用户的信息，会话本身以及会话背景的信息
     * 字段以及范例：["id", "backgroundImage", "isStatus", "statusTextCoord", "statusText"],
     */
    private void getStatusByRemote(String targetId, String statusId, FunctionCallback<Map> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put(LC.method.LoadTargetUserConversation.paramTargetId, targetId);
        if (!TextUtils.isEmpty(statusId)) {
            params.put(LC.method.LoadTargetUserConversation.paramStatusId, statusId);
        }
        AVCloud.callFunctionInBackground(LC.method.LoadTargetUserConversation.functionName, params, callback);
    }

    public void getStatusByRemote(String targetId, FunctionCallback<Map> callback) {
        getStatusByRemote(targetId, null, callback);
    }

    @Override
    public void removeConversation(String conversationId) {
        dbSupportHelper.removeLocalConversationById(conversationId);
    }

    @Override
    public void setConversationLastRead(long lastReadTime, String conversationId) {
        Conversation conversation = dbSupportHelper
                .queryLocalConversationById(conversationId);
        if(conversation != null){
            conversation.setLastCheckTime(lastReadTime);
            conversation.setHasUnread(false);
            dbSupportHelper.saveConversationToDB(conversation);
            readMessageChangedListener.onReadChanged();
        }


    }


    @Override
    public void setConversationUnread(long lastMessageTime, String conversationId) {
        Conversation conversation = dbSupportHelper
                .queryLocalConversationById(conversationId);
        if(conversation != null ){
            if (conversation.getLastCheckTime() >= lastMessageTime) {
                //DO nothing
            } else {
                conversation.setHasUnread(true);
                dbSupportHelper.saveConversationToDB(conversation);
                readMessageChangedListener.onReadChanged();
            }
        }



    }

    @Override
    public void fetchAVIMConversationById(String conversationId, QueryCallback<AVIMConversation> callback) {
        for (String key : avConversationCache.keySet()) {
            if (conversationId.equals(avConversationCache.get(key).getConversationId())) {
                callback.done(avConversationCache.get(key), true, null);
                return;
            }
        }
        if (imClient != null) {
            callback.done(imClient.getConversation(conversationId), true, null);
            return;
        }

    }

    /**
     * 监听网络状态
     */
    private class CustomEventHandler extends AVIMClientEventHandler {
        @Override
        public void onConnectionPaused(AVIMClient avimClient) {
            isConnect = false;
            recoverConnectNet();
            Log.e("chat", "网络断开！！！");
        }

        @Override
        public void onConnectionResume(AVIMClient avimClient) {
            isConnect = true;
            Log.e("chat", "网络恢复！！！");
        }
    }

    //对外提供获取客户端对象方法，判断是否登录
    public AVIMClient getImClient() {
        return imClient;
    }

    //提供是否连接
    public boolean isChatConnect() {
        return isConnect;
    }

    public boolean isLogin() {
        return isLogin;
    }


    public void recoverConnectNet() {
        login(AVUser.getCurrentUser().getObjectId(), new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                if (e != null) {
                    LogUtil.log.e("chat", "网络连接失败，请检查网络");
                } else {
                    isConnect = true;
                    databaseManager.switchDatabase(AVUser.getCurrentUser().getObjectId());
                    Log.e("chat", "网络恢复！！！");
                }

            }
        });
    }

    public Long getLocalLastMessageTime(){
       List<ChatMessage> list = dbSupportHelper.queryLocalLastMessage();
        if(list != null && list.size()>0){
            return list.get(0).getTimestamp();
        }
        return null;
    }

    public interface ReadMessageChangedListener{
        void onReadChanged();

    }
    public void setReadMessageChangedListener(ReadMessageChangedListener readMessageChangedListener){
        this.readMessageChangedListener = readMessageChangedListener;
    }



}
