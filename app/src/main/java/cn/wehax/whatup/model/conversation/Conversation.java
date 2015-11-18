package cn.wehax.whatup.model.conversation;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by howe on 15/6/9.
 * Email:howejee@gmail.com
 */
@DatabaseTable(tableName = "conversation")
public class Conversation implements Serializable {

    @DatabaseField(id = true)
    private String conversationId;

    @DatabaseField
    private String lastMessage;

    @DatabaseField
    private String bgUrl;

    @DatabaseField
    private String bgId;

    @DatabaseField
    private String initBgId;

    @DatabaseField
    private String statusId;

    @DatabaseField
    private String statusImageUrl;

    @DatabaseField
    private String targetUid;

    @DatabaseField
    private String targetAvatar;

    @DatabaseField
    private long lastCheckTime;


    @DatabaseField
    private boolean hasUnread;

    @DatabaseField
    private int targetSex;

    @DatabaseField
    private String targetNickname;

    @DatabaseField
    private long time;


    private AVIMConversation avimConversation;


    //是否为推荐用户
    private boolean temp;


    public String getConversationId() {
        return conversationId;
    }

    public boolean isTemp() {
        return temp;
    }

    public void setTemp(boolean temp) {
        this.temp = temp;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getTargetUid() {
        return targetUid;
    }

    public AVIMConversation getAvimConversation() {
        return avimConversation;
    }

    public void setAvimConversation(AVIMConversation avimConversation) {
        this.avimConversation = avimConversation;
    }

    public void setTargetUid(String targetUid) {
        this.targetUid = targetUid;
    }

    public String getTargetAvatar() {
        return targetAvatar;
    }

    public void setTargetAvatar(String targetAvatar) {
        this.targetAvatar = targetAvatar;
    }

    public int getTargetSex() {
        return targetSex;
    }

    public void setTargetSex(int targetSex) {
        this.targetSex = targetSex;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTargetNickname() {
        return targetNickname;
    }

    public void setTargetNickname(String targetNickname) {
        this.targetNickname = targetNickname;
    }

    public String getBgUrl() {
        return bgUrl;
    }

    public void setBgUrl(String bgUrl) {
        this.bgUrl = bgUrl;
    }

    public String getBgId() {
        return bgId;
    }

    public void setBgId(String bgId) {
        this.bgId = bgId;
    }

    public long getLastCheckTime() {
        return lastCheckTime;
    }

    public void setLastCheckTime(long lastCheckTime) {
        this.lastCheckTime = lastCheckTime;
    }

    public boolean hasUnread() {
        return hasUnread;
    }

    public void setHasUnread(boolean hasUnread) {
        this.hasUnread = hasUnread;
    }

    public String getInitBgId() {
        return initBgId;
    }

    public void setInitBgId(String initBgId) {
        this.initBgId = initBgId;
    }


    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public String getStatusImageUrl() {
        return statusImageUrl;
    }

    public void setStatusImageUrl(String statusImageUrl) {
        this.statusImageUrl = statusImageUrl;
    }
}
