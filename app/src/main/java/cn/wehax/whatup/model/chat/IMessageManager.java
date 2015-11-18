package cn.wehax.whatup.model.chat;

import com.avos.avoscloud.im.v2.AVIMConversation;

import java.util.List;
import java.util.Map;

import cn.wehax.whatup.model.chatView.ChatMessage;
import cn.wehax.whatup.model.chatView.ChatMessageContent;


/**
 * Created by sanchibing on 2015/8/6.
 * Email:sanchibing@gmail.com
 */
public interface IMessageManager {

    /**
     * 发送消息
     * @param message 准备发送的消息
     * @param callback 消息是否发送成功的回调
     */
    void sendMessage(AVIMConversation avimConversation, String message, final QueryCallback<ChatMessage> callback);

    /**
     * 读取某会话某时间之前的文字消息
     * @param limit  消息条数
     * @param fromTime  获取此时间之前的消息
     * @param conversationId  对方id
     *
     */
    List<ChatMessageContent> loadTextMsgList(int limit, Long fromTime,String conversationId);

    /**
     * 读取某会话保存的涂鸦消息
     * @param conversationId  对方id
     *
     */
    List<ChatMessageContent> loadGraffitiMsgList(String conversationId);

    /**
     * 增加一个接收聊天消息的回调
     * @param callback 聊天消息回调
     */
    void addOnReceiveMsg(QueryCallback<ChatMessageContent> callback);

    /**
     * 获得某时间后未读的消息列表
     * @param timestamp  获得此时间后未读的消息列表
     * @param callback  获得的未读的消息列表回调
     */
    void fetchUnreadMessageList(Long timestamp,ReceiveMessageCallback<List<ChatMessageContent>> callback);

    /**
     * 获得总未读消息数量
     */
    boolean hasUnreadMessageInAll();

    /**
     * 获得某会话未读的消息数量
     * @param conversationId  会话id
     */
    boolean hasUnreadMessage(String conversationId);


    /**
     * 获取真心话数据
     * @param conversationId  对话id
     * @param callback  获得的真心话数据回调
     */
    void fetchTruthData(String conversationId, QueryCallback<Map> callback);

}
