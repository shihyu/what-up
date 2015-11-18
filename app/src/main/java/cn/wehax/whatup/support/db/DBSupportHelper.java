package cn.wehax.whatup.support.db;

import android.util.Log;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogUtil;
import com.avos.avoscloud.im.v2.AVIMMessage;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import cn.wehax.whatup.model.chat.QueryCallback;
import cn.wehax.whatup.model.chatView.ChatMessage;
import cn.wehax.whatup.model.conversation.Conversation;
import cn.wehax.whatup.model.leancloud.RemoteHelper;

/**
 * Created by sanchibing on 2015/8/6.
 * Email:sanchibing@gmail.com
 */
public class DBSupportHelper implements IDBSupportHelper {

    public static final String LC_TABLE_CONVERSATION = "_Conversation";

    @Inject
    DatabaseManager databaseManager;

    @Inject
    RemoteHelper remoteHelper;

    @Override
    public void saveConversationToDB(final Conversation conversation) {
        try {
            databaseManager.getDaoByClass(Conversation.class).getRawDao().update(conversation);
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
      Conversation co= queryLocalConversationById(conversation.getConversationId());
        LogUtil.log.e("co",String.valueOf(co.hasUnread()));

    }


    @Override
    public void saveConversationToDB(final ChatMessage message , final ConversationSaveCallback callback){
        String[] queryIncludes = {"avatar", "from", "to", "status", "imageData"};
        remoteHelper.queryAVObject(message.getConversationId(), LC_TABLE_CONVERSATION, queryIncludes, new QueryCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, boolean isLocal, Exception e) {
                if (e == null) {
                    Conversation conv = new Conversation();

                    AVFile file = avObject.getAVFile("backgroundFile");
                    conv.setBgId(file.getObjectId());
                    conv.setBgUrl(file.getUrl());
                    AVObject target = avObject.getAVObject("from");
                    if (AVUser.getCurrentUser().getObjectId().equals(target.getObjectId())) {
                        target = avObject.getAVObject("to");
                    }
                    conv.setTargetUid(target.getObjectId());
                    AVObject status = avObject.getAVObject("status");
                    if (status != null) {
                        conv.setStatusId(status.getObjectId());

                        AVFile image = status.getAVFile("imageData");
                        conv.setStatusImageUrl(image.getUrl());
                        Log.e("chat", "当前要保存的对话里状态图片地址：" + image.getUrl());
                    }

                    AVFile avatarFile = target.getAVFile("avatar");
                    if (avatarFile != null) {
                        conv.setTargetAvatar(avatarFile.getUrl());
                    }
                    conv.setTargetNickname(target.getString("nickname"));
                    conv.setTargetSex(target.getInt("sex"));
                    conv.setInitBgId(avObject.getString("initBackground"));
                    conv.setConversationId(message.getConversationId());
                    conv.setTime(message.getTimestamp());
                    conv.setLastMessage(message.getContent());

                    try {
                        databaseManager.getDaoByClass(Conversation.class).getRawDao().createOrUpdate(conv);
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    e.printStackTrace();
                }
                callback.onSaveSuccess();
            }
        });

    }


    @Override
    public void saveMessageToDB(ChatMessage message) {
        try {
            databaseManager.getDaoByClass(ChatMessage.class).getRawDao().createOrUpdate(message);
        } catch (SQLException e1) {
            e1.printStackTrace();
            Log.e("chat", "保存消息出错：" + e1.getMessage());
        }

    }

    @Override
    public Conversation queryLocalConversationById(String conversationId) {
        Conversation conv = null;
        try {
            conv = databaseManager.getDaoByClass(Conversation.class).getRawDao().queryForId(conversationId);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("chat", "查询某条对话出错：" + e.toString());
        }
        return conv;
    }

    @Override
    public List<ChatMessage> queryLocalMessages(String conversationId) {
        List<ChatMessage> list = null;
        try {
            list = databaseManager.getDaoByClass(ChatMessage.class).getRawDao()
                    .queryBuilder()
                    .orderBy("timestamp", true)//时间升序
                    .where().eq("conversationId", conversationId).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean removeLocalConversationById(String conversationId) {
        List<Conversation> list = null;
        try {
            list = databaseManager.getDaoByClass(Conversation.class).getRawDao()
                    .queryBuilder()
                    .where().eq("conversationId", conversationId).query();
            if (list != null && list.size() > 0) {
                for (Conversation conversation : list) {
                    databaseManager.getDaoByClass(Conversation.class).getRawDao().delete(conversation);
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Conversation queryLocalConversationByStatusId(String statusId) {
        try {
            List<Conversation> list = databaseManager.getDaoByClass(Conversation.class).getRawDao().queryForEq("statusId", statusId);
            //保证唯一性
            if (list != null && list.size() == 1) {
                return list.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("chat", "查询某条对话出错：" + e.toString());
        }
        return null;
    }

    public List<Conversation> queryLocalConversations() {
        List<Conversation> list = null;
        try {
            list = databaseManager.getDaoByClass(Conversation.class).getRawDao()
                    .queryBuilder()
                    .orderBy("time", false)
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("chat", "查询对话列表出错：" + e.toString());
        }
        return list;
    }


    public long queryLocalConversationUnread() {
        long count = 0;
        try {
            count = databaseManager.getDaoByClass(Conversation.class).getRawDao()
                    .queryBuilder()
                    .where().eq("hasUnread", true)
                    .countOf();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("chat", "查询对话列表出错：" + e.toString());
        }
        return count;
    }

    public List<ChatMessage> queryLocalLastMessage() {
        List<ChatMessage> list =null;
        try {
            list = databaseManager.getDaoByClass(ChatMessage.class).getRawDao()
                    .queryBuilder()
                     .orderBy("timestamp",false)
                    .limit(1L).query();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("chat", "查询对话列表出错：" + e.toString());
        }
        return list;
    }

    public interface ConversationSaveCallback{
        void onSaveSuccess();
    }


}
