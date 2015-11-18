package cn.wehax.whatup.model.chatView;

import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.List;

/**
 * Created by howe on 15/5/29.
 * Email:howejee@gmail.com
 * 消息主体内容类,该类对应ChatMessage里content
 */
public class ChatMessageContent implements Serializable{

    //消息类型：涂鸦
    public static final String TYPE_GRAFFITI = "graffiti";
    //消息类型：文本
    public static final String TYPE_TEXT = "text";
    //消息类型：撤销
    public static final String TYPE_GRAFFITI_REVOKE = "drawbackGraffiti";
    //消息类型：更换背景
    public static final String TYPE_CHANGE_BG = "changeBg";
    //消息类型：真心话
    public static  final String TYPE_TRUTH="truth";


    //========以下为所有消息类型共有

    //消息的类型，上面四种
    private String type;

    //对方屏幕高度
    private int screenHeight;

    //对方屏幕宽度
    private int screenWidth;

    //=========以下修改背景消息特有
    //背景图片地址
    private String url;


    //========以下为涂鸦消息特有
    //涂鸦点坐标
    private List<Integer> data;

    //涂鸦样式
    private String strokeStyle;


    //=========以下为文本消息特有
    //文本
    private String text;
    //文本到顶部距离
     private String bubbleStyle;

    //=====以下为真心话特有
    private String answers;

    public boolean isReply() {
        return isReply;
    }

    public void setIsReply(boolean isReply) {
        this.isReply = isReply;
    }

    private boolean isReply;
    private String question;

    //头像
    private String avatar;

    //是否自己发送的消息
    boolean isSelf;


    //消息时间
    @DatabaseField
    private long time;

    boolean isSystemMsg;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }
    public String getAnswers() {
        return answers;
    }

    public void setAnswer(String answers) {
        this.answers = answers;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Integer> getData() {
        return data;
    }

    public void setData(List<Integer> data) {
        this.data = data;
    }

    public String getStrokeStyle() {
        return strokeStyle;
    }

    public void setStrokeStyle(String strokeStyle) {
        this.strokeStyle = strokeStyle;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getBubbleStyle() {
        return bubbleStyle;
    }

    public void setBubbleStyle(String bubbleStyle) {
        this.bubbleStyle = bubbleStyle;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public void setSelf(boolean isSelf) {
        this.isSelf = isSelf;
    }

    public boolean isSystemMsg() {
        return isSystemMsg;
    }

    public void setIsSystemMsg(boolean isSystemMsg) {
        this.isSystemMsg = isSystemMsg;
    }

    @Override
    public String toString() {
        return "ChatMessage{" + '\'' +
                ", type='" + type + '\'' +
                ", screenHeight=" + screenHeight +
                ", screenWidth=" + screenWidth +
                ", url='" + url + '\'' +
                ", data=" + data +
                ", strokeStyle='" + strokeStyle + '\'' +
                ", text='" + text + '\'' +
                  ", bubbleStyle='" + bubbleStyle + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
