package cn.wehax.whatup.model.chat;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.FunctionCallback;
import com.avos.avoscloud.LogUtil;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageHandler;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import cn.wehax.whatup.config.Constant;
import cn.wehax.whatup.config.IntentKey;
import cn.wehax.whatup.model.chatView.ChatMessage;
import cn.wehax.whatup.model.chatView.ChatMessageContent;
import cn.wehax.whatup.model.conversation.Conversation;
import cn.wehax.whatup.model.conversation.ConversationManager;
import cn.wehax.whatup.model.leancloud.LC;
import cn.wehax.whatup.support.db.DBSupportHelper;

/**
 * Created by Administrator on 2015/8/6 0006.
 */
@Singleton
public class MessageManager implements IMessageManager {

    private Application app;

    @Inject
    DBSupportHelper dbSupportHelper;

    @Inject
    MessageConverter messageConverter;

    @Inject
    ConversationManager conversationManager;

    private List<QueryCallback<ChatMessageContent>> messageReceiveList;

    private OnConversationsDataChangeListener conversationsListener;


    @Inject
    MessageManager(Provider<Application> application) {
        app = application.get();
        initReceiveMessage();
    }

    /**
     * 初始化消息接收监听
     */
    public void initReceiveMessage() {
        //该方法需要在open前调用！！！
        AVIMMessageManager.registerDefaultMessageHandler(new CustomMessageHandler());

    }


    @Override
    public void sendMessage(AVIMConversation avimConversation,  String message, final QueryCallback<ChatMessage> callback) {
        if (avimConversation == null) {
            throw new RuntimeException("sendMessage NullPointException:avimConversation is null!");
        }
        final AVIMMessage avimMessage = new AVIMMessage();
        LogUtil.log.e("avimMessage", avimMessage.getTimestamp() + "");
        avimMessage.setContent(message);
        avimConversation.sendMessage(avimMessage, AVIMConversation.RECEIPT_MESSAGE_FLAG, new AVIMConversationCallback() {

            @Override
            public void done(AVIMException e) {
                if (e != null) {
                    if (callback != null)
                        callback.done(null, false, e);
                    Log.e("chat", "发送失败" + e.getMessage());
                } else {
                    //发送成功
                    Log.e("chat", "####发送成功：id=" + avimMessage.getMessageId() + "  conId=" + avimMessage.getConversationId()
                            + "  from=" + avimMessage.getFrom() + "  ts=" + avimMessage.getTimestamp());

                    ChatMessage msg = messageConverter.converterMessageByAVIMMessage(avimMessage);
                    if (callback != null) {
                        callback.done(msg, false, null);

                    }
                    dbSupportHelper.saveMessageToDB(msg);
                    //通知ConversationManner关系数据发生了变化
                    if (conversationsListener != null) {
                        conversationsListener.onConversationsDataChanged(msg);
                    }
                }
            }
        });
    }

    @Override
    public List<ChatMessageContent> loadTextMsgList(int limit, Long fromTime, String conversationId) {
        List<ChatMessageContent> textMsgList = new ArrayList<>();
        List<ChatMessage> messageList = dbSupportHelper.queryLocalMessages(conversationId);
        for (ChatMessage chatMessage : messageList) {
            ChatMessageContent msg = messageConverter.convertMessageToContent(chatMessage);

            String messageType = msg.getType();
            if (ChatMessageContent.TYPE_TRUTH.equals(messageType) || ChatMessageContent.TYPE_TEXT.equals(messageType)) {
                textMsgList.add(msg);
            }
        }
        return textMsgList;

    }

    @Override
    public List<ChatMessageContent> loadGraffitiMsgList(String conversationId) {
        List<ChatMessageContent> graffitiMsgList = new ArrayList<>();
        List<ChatMessage> messageList = dbSupportHelper.queryLocalMessages(conversationId);
        for (ChatMessage chatMessage : messageList) {
            ChatMessageContent msg = messageConverter.convertMessageToContent(chatMessage);
            String messageType = msg.getType();
            if (messageType.equals(ChatMessageContent.TYPE_GRAFFITI)) {
                graffitiMsgList.add(msg);
            }
        }
        return graffitiMsgList;

    }

    @Override
    public void addOnReceiveMsg(final QueryCallback<ChatMessageContent> callback) {
        if(messageReceiveList == null){
            messageReceiveList = new ArrayList<>();
        }
        messageReceiveList.add(callback);
    }



    /**
     * 用于接收远程的消息
     */
    private class CustomMessageHandler extends AVIMMessageHandler {
        @Override
        public void onMessage(AVIMMessage avimMessage, AVIMConversation conversation, AVIMClient client) {

            //如果不是当前登录用户的消息就忽略
            if (conversationManager.getImClient() == null || !conversationManager.getImClient().getClientId().equals(client.getClientId())) {
                return;
            }
            //处理接收到的消息
            Log.e("消息", avimMessage.toString());

            //将消息保存到数据库
            ChatMessage chatMessage = messageConverter.converterMessageByAVIMMessage(avimMessage);
            dbSupportHelper.saveMessageToDB(chatMessage);

            //通知ConversationManner关系数据发生了变化
            if (conversationsListener != null) {
                conversationsListener.onConversationsDataChanged(chatMessage);
            }
            LogUtil.log.e("onMessage", chatMessage.getContent());
            ChatMessageContent msg = messageConverter.convertMessageToContent(chatMessage);


            if (messageReceiveList != null) {
                for(QueryCallback<ChatMessageContent> receive:messageReceiveList){
                    receive.done(msg,false,null);
                }
            }
            //将消息广播出去
            Intent intent = new Intent();
            intent.setAction(Constant.RECEIVE_MESSAGE_ACTION);
            intent.putExtra(IntentKey.INTENT_KEY_AVIMMESSAGE, msg);
            intent.putExtra(IntentKey.INTENT_KEY_CONVERSATION_ID, conversation.getConversationId());
            app.sendBroadcast(intent);
            LogUtil.log.e("conversation", avimMessage.getFrom());

        }


    }


    @Override
    public void fetchUnreadMessageList(Long timestamp, final ReceiveMessageCallback<List<ChatMessageContent>> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put(LC.method.GetAllChatList.paramOnlyOther, false);
        params.put(LC.method.GetAllChatList.paramStartTime, timestamp);
        AVCloud.callFunctionInBackground(LC.method.GetAllChatList.functionName, params, new FunctionCallback<List<Map<String, Object>>>() {
            @Override
            public void done(List<Map<String, Object>> maps, AVException e) {
                List<ChatMessageContent> contentList = new ArrayList<>();
                if(maps != null) {
                    for (Map<String, Object> map : maps) {
                        if (map != null) {
                            ChatMessage message = messageConverter.convertMessageFromRemote(map);
                            dbSupportHelper.saveMessageToDB(message);
                            contentList.add(messageConverter.convertMessageToContent(message));
                            if (conversationsListener != null) {
                                conversationsListener.onConversationsDataChanged(message);
                            }

                        }
                    }
                }

                callback.done(contentList,null);

            }

        });

    }

    @Override
    public boolean hasUnreadMessageInAll() {
        return dbSupportHelper.queryLocalConversationUnread() > 0;
    }


    @Override
    public boolean hasUnreadMessage(String conversationId) {
        Conversation conversation = dbSupportHelper.queryLocalConversationById(conversationId);
        return conversation.hasUnread();
    }


    @Override
    public void fetchTruthData(String conversationId, final QueryCallback<Map> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put(LC.method.GetTruth.paramConversationId, conversationId);
        AVCloud.callFunctionInBackground(LC.method.GetTruth.functionName, params, new FunctionCallback<Map>() {

            @Override
            public void done(Map map, AVException e) {
                if (map != null) {
                    final String question = (String) map.get(LC.method.GetTruth.keyQuestion);
                    ArrayList<String> list = (ArrayList<String>) map.get(LC.method.GetTruth.keyAnswer);
                    StringBuilder sb = new StringBuilder();
                    if (list != null) {
                        for (int i = 0; i < list.size(); i++) {
                            if (i != (list.size() - 1)) {
                                sb.append(list.get(i) + "#");
                            } else {
                                sb.append(list.get(i));
                            }
                        }
                    }
                    final String answer = sb.toString();
                    Map<String, String> resultMap = new HashMap<>();
                    resultMap.put("question", question);
                    resultMap.put("answer", answer);
                    callback.done(resultMap, false, e);
                }

            }
        });

    }

    public void setOnConversationsDataChangeListener(OnConversationsDataChangeListener listener) {
        this.conversationsListener = listener;
    }


    public interface OnConversationsDataChangeListener {
        void onConversationsDataChanged(ChatMessage message);
    }


}
