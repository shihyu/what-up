package cn.wehax.whatup.model.chatView;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by howe on 15/6/4.
 * Email:howejee@gmail.com
 * 数据库数据类，聊天消息类
 */
@DatabaseTable(tableName = "message")
public class ChatMessage implements Serializable {

    //消息的id
    @DatabaseField(id = true)
    private String messageId;

    //对话的id
    @DatabaseField
    private String conversationId;

    //消息体，具体内容
    @DatabaseField
    private String content;

    //消息发送时间戳
    @DatabaseField
    private long timestamp;

    //是否自己发送的消息
    @DatabaseField
    private boolean isSelf;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public void setSelf(boolean isSelf) {
        this.isSelf = isSelf;
    }


}
