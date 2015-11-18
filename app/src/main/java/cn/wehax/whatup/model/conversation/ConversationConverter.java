package cn.wehax.whatup.model.conversation;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import cn.wehax.whatup.model.leancloud.LC;

/**
 * Created by sanchibing on 2015/8/8.
 * Email:sanchibing@gmail.com
 */
public class ConversationConverter {
    public List<Conversation> converterRemoteList(List<Map> maps) {
        List<Conversation> conversationList = new ArrayList<>();
        Conversation conversation = null;
        if (maps != null && maps.size() > 0) {
            conversationList.clear();
            for (Map map : maps) {
                conversation = new Conversation();
                conversation.setConversationId((String) map.get(LC.method.GetMyConversationList.keyId));
                Integer sex = (Integer)map.get(LC.method.GetMyConversationList.keyTargetSex);
                if(sex != null){
                    conversation.setTargetSex(sex);
                }
                conversation.setTargetNickname((String) map.get(LC.method.GetMyConversationList.keyTargetNickName));
                conversation.setTime((long) map.get(LC.method.GetMyConversationList.keyUpdatedAt));
                conversation.setTargetUid((String) map.get(LC.method.GetMyConversationList.keyTargetId));
                conversation.setTemp((boolean) map.get(LC.method.GetMyConversationList.keyTemp));
                AVFile avatar = (AVFile) map.get(LC.method.GetMyConversationList.keyTargetAvatar);
                if (avatar != null) {
                    conversation.setTargetAvatar(avatar.getUrl());
                }
                conversationList.add(conversation);
            }
        }
        return conversationList;
    }
}
