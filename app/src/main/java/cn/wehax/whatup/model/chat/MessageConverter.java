package cn.wehax.whatup.model.chat;

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.google.gson.Gson;

import java.util.Map;

import javax.inject.Singleton;

import cn.wehax.whatup.model.chatView.ChatMessage;
import cn.wehax.whatup.model.chatView.ChatMessageContent;
import cn.wehax.whatup.model.leancloud.LC;
import cn.wehax.whatup.support.util.StringUtils;

/**
 * Created by sanchibing on 2015/8/8.
 * Email:sanchibing@gmail.com
 */
@Singleton
public class MessageConverter {

    private Gson mGson = new Gson();


    //转换服务器消息为本地消息保存
    public ChatMessage converterMessageByAVIMMessage(AVIMMessage avimMessage) {
        Long timestamp = avimMessage.getTimestamp();
        String conversationId = avimMessage.getConversationId();
        String setMessageId = timestamp + conversationId;
        ChatMessage msg = new ChatMessage();
        msg.setContent(avimMessage.getContent());
        msg.setSelf(AVUser.getCurrentUser().getObjectId().equals(avimMessage.getFrom()));
        msg.setConversationId(conversationId);
        msg.setMessageId(setMessageId);
        msg.setTimestamp(timestamp);
        return msg;
    }


    public ChatMessage convertMessageFromRemote(Map<String, Object> map) {
        Long timestamp = (Long) map.get(LC.method.GetAllChatList.keyTimestamp);
        String conversationId = (String) map.get(LC.method.GetAllChatList.keyConversationId);
        String setMessageId = timestamp + conversationId;
        ChatMessage msg = new ChatMessage();
        msg.setContent((String)map.get(LC.method.GetAllChatList.keyMessage));
        msg.setSelf(AVUser.getCurrentUser().getObjectId().equals(map.get(LC.method.GetAllChatList.keyFromID)));
        msg.setConversationId(conversationId);
        msg.setMessageId(setMessageId);
        msg.setTimestamp(timestamp);
        return msg;
    }

    public ChatMessageContent convertMessageToContent(ChatMessage message) {
        ChatMessageContent msg = mGson.fromJson(message.getContent(), ChatMessageContent.class);
        msg.setSelf(message.isSelf());
        msg.setTime(message.getTimestamp());

        return msg;
    }

}
