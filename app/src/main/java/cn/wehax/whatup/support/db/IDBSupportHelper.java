package cn.wehax.whatup.support.db;

import java.util.List;

import cn.wehax.whatup.model.chatView.ChatMessage;
import cn.wehax.whatup.model.conversation.Conversation;

/**
 * Created by sanchibing on 2015/8/5.
 * Email:sanchibing@gmail.com
 */
public interface IDBSupportHelper {

    /**
     * 保存会话关系到本地数据库
     */
    void saveConversationToDB(ChatMessage message,DBSupportHelper.ConversationSaveCallback callback);

    /**
     * 保存会话关系到本地数据库
     */

    void saveMessageToDB(ChatMessage message);

    void saveConversationToDB(final Conversation conversation);

    /**
     * 查找本地某条对话记录
     */
    Conversation queryLocalConversationById(String conversationId);

    /**
     * 查找本地聊天记录
     *
     * @param conversationId
     */
    List<ChatMessage> queryLocalMessages(String conversationId);

    /**
     * 删除本地关系表
     *
     * @param conversationId
     * @return
     */
    boolean removeLocalConversationById(String conversationId);


}
