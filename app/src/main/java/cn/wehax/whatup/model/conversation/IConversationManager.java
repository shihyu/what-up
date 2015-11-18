package cn.wehax.whatup.model.conversation;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;

import java.util.List;
import java.util.Map;

import cn.wehax.whatup.model.chat.QueryCallback;

/**
 * Created by sanchibing on 2015/8/6.
 * Email:sanchibing@gmail.com
 */
public interface IConversationManager {
    /**

     获取会话列表

     fetchConversationList（local/remote/local&remote)

     开始/读取会话

     startConversation (conversationId?targetId,statusId)


     获取当前会话

     currentConversation ()
     */

    /**
     * 登录leancloud 聊天服务
     */
     void login(String selfId, AVIMClientCallback callback);

    /**
     * 登出聊天服务
     */
     void logout();

    /**
     *
     * 获取会话列表
     *  关系列表使用
     *  根据策略
     *  1. 获取本地保存的会话数据，
     *  2. 或者拉取云代码会话数据并与本地数据合并，
     *  3. 或者 1 然后 2
     * fetchConversationList（local/remote/local&remote)
     * @param strategy 策略
     * @param callback 会话列表数据回调
     */
    void fetchConversationList(int strategy,QueryCallback<List<Conversation>> callback);

    /**
     *
     *
     * 开始/读取会话
     * startConversation (conversationId?targetId,statusId)
     * 1. 根据缓存策略，获取本地conversation数据或者远程拉取服务器数据
     * 2. 根据获得的conversation数据，使用AVIM对应SDK创建AVIMConversation对象并缓存。
     * 3. 如果本地已经存在AVIMConversation对象，可根据缓存策略直接返回。
     * @param targetId 会话对方的用户Id
     * @param allStatusId 可选,如果传递的话,表示需要使用此status作为背景素材.
     * @param callback 获得的会话对象回调
     */
     void startConversation(int strategy, String targetId, String allStatusId ,QueryCallback<Conversation> callback);

    /**
     * 如果conversation为临时对话，调用云代码删除会话接口
     * 如果不为临时对话，清空本地聊天记录，以隐藏对应会话
     * @param conversationId 需要删除的会话id
     */
    void removeConversation(String conversationId);

    /**
     * 将一个会话最后阅读时间设置为传入时间，并将其设置为全部已读
     * @param lastReadTime
     * @param conversationId
     */
    void setConversationLastRead(long lastReadTime, String  conversationId);

    /**
     * 比较一个会话的lastReadTime与lastMessageTime，如果lastMessageTime大于lastReadTime，
     * 将其设置为未读。
     * @param lastMessageTime
     * @param conversationId
     */
    void setConversationUnread(long lastMessageTime, String conversationId);


    /**获取会话
     * @param conversationId 会话id
     * @param callback 获得的会话对象回调
     */
    void fetchAVIMConversationById(String conversationId, QueryCallback<AVIMConversation> callback);


}
