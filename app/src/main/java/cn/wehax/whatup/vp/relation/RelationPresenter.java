package cn.wehax.whatup.vp.relation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.avos.avoscloud.LogUtil;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import cn.wehax.whatup.config.Constant;
import cn.wehax.whatup.config.IntentKey;
import cn.wehax.whatup.framework.presenter.BasePresenter;
import cn.wehax.whatup.model.chatView.ChatMessageContent;
import cn.wehax.whatup.model.chat.QueryCallback;
import cn.wehax.whatup.model.conversation.Conversation;
import cn.wehax.whatup.model.conversation.ConversationManager;
import cn.wehax.whatup.support.db.DBSupportHelper;
import cn.wehax.whatup.support.util.PreferencesUtils;

/**
 * Created by howe on 15/6/9.
 * Email:howejee@gmail.com
 */
public class RelationPresenter extends BasePresenter<RelationActivity> {

    @Inject
    DBSupportHelper dbSupportHelper;

    private Gson gson = new Gson();

    @Inject
    ConversationManager conversationManager;

    RelationAdapter adapter;

    private List<Conversation> userRelationsList = new ArrayList<>();

    private MessageReceiver messageReceiver;

    private RelationActivity activity;

    @Override
    public void setView(RelationActivity view) {
        super.setView(view);
        activity = view;
        adapter = new RelationAdapter(this,view.mRelationListView.getRightViewWidth());
        view.setAdapter(adapter);

        removeConversationList();

        registerReceiver();
    }


    public void loadData() {
        getMyRelations();
        if (userRelationsList != null) {
            adapter.setDate(userRelationsList);
            adapter.notifyDataSetChanged();
            LogUtil.log.e("userRelationsList", userRelationsList.toString());
        }

    }

    public void showTips(RelationActivity view){
        view.mRelationListView.setVisibility(View.INVISIBLE);
        view.tipsTextView.setVisibility(View.VISIBLE);
    }
    public void hideTips(RelationActivity view){
        view.mRelationListView.setVisibility(View.VISIBLE);
        view.tipsTextView.setVisibility(View.INVISIBLE);
    }

    public void onItemClick(int index) {
        if (index < 0 || index >= userRelationsList.size()) {
            return;
        }
        Conversation conv = dbSupportHelper.queryLocalConversationById(userRelationsList.get(index).getConversationId());
        String avatarUrl =userRelationsList.get(index).getTargetAvatar();

        //不为空说明本地有缓存，可以直接发送聊天
        if (conv != null) {
            mView.moveToChatActivity(conv.getTargetUid(), conv.getConversationId(), false, avatarUrl);
            PreferencesUtils.putBoolean(getActivity(), conv.getConversationId(), true);

        } else {
            //本地没有缓存,需要提供状conversationId,和对方用户id ,进入聊天页面。
            String conversationId = userRelationsList.get(index).getConversationId();
            String targetId = userRelationsList.get(index).getTargetUid();
            if (TextUtils.isEmpty(getLocalLastMessage(userRelationsList.get(index).getConversationId())) && userRelationsList.get(index).isTemp()) {
                mView.moveToChatActivity(targetId, conversationId, true, avatarUrl);
            } else {
                mView.moveToChatActivity(targetId, conversationId, false, avatarUrl);
            }
            PreferencesUtils.putBoolean(getActivity(),conversationId,true);
        }
    }
    private void registerReceiver() {
        messageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.RECEIVE_MESSAGE_ACTION);
        mView.registerReceiver(messageReceiver, filter);
    }

    public void unregisterReceiver() {
        if (messageReceiver != null) {
            mView.unregisterReceiver(messageReceiver);
        }
    }

    private class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }

            if (Constant.RECEIVE_MESSAGE_ACTION.equals(action)) {
                 new Handler(){
                     @Override
                     public void handleMessage(Message msg) {
                         loadData();
                     }
                 }.sendEmptyMessageDelayed(0, 500);

              }
            }

    }

    public void getMyRelations() {
        conversationManager.fetchConversationList(ConversationManager.STRATEGY_LOCAL, new QueryCallback<List<Conversation>>() {
            @Override
            public void done(List<Conversation> result, boolean isLocal, Exception e) {
                if (result != null && result.size() > 0) {
                    userRelationsList.clear();
                    userRelationsList.addAll(result);
                    adapter.setDate(userRelationsList);
                    if (userRelationsList.size() == 0){
                        showTips(activity);
                    }else{hideTips(activity);}

//                    showTips(activity);hideTips(activity);
                }else{
                    showTips(activity);
                }

            }
        });
        conversationManager.fetchConversationList(ConversationManager.STRATEGY_HYBRID, new QueryCallback<List<Conversation>>() {
            @Override
            public void done(List<Conversation> result, boolean isLocal, Exception e) {
                if (result != null && result.size() > 0) {
                    userRelationsList.clear();
                    userRelationsList.addAll(result);
                    adapter.setDate(userRelationsList);
                    if (userRelationsList.size() == 0){
                        showTips(activity);
                    }else{hideTips(activity);}

//                    showTips(activity);hideTips(activity);
                }else{
                    showTips(activity);
                }

            }
        });

    }

    private void removeConversationList(){
        adapter.setonRemoveItemClickListener(new RelationAdapter.onRemoveItemClickListener() {
            @Override
            public void onRemoveItemClick(int pos, String conversationId) {
                conversationManager.removeConversation(conversationId);
//                activity.mRelationListView.getChildAt(pos).setVisibility(View.GONE);
                loadData();

            }
        });
    }




    public String getLocalLastMessage(String conversationId) {
        String lastMessage = "";
        Conversation conv;
        if (!TextUtils.isEmpty(conversationId)) {
            conv = dbSupportHelper.queryLocalConversationById(conversationId);
            if (conv != null) {
                ChatMessageContent message = gson.fromJson(conv.getLastMessage(), ChatMessageContent.class);
                if (message != null) {
                    if (ChatMessageContent.TYPE_TEXT.equals(message.getType())) {
                        lastMessage = message.getText();
                    } else if (ChatMessageContent.TYPE_GRAFFITI.equals(message.getType())) {
                        lastMessage = Constant.GRAFFITI_MESSAGE;
                    } else if (ChatMessageContent.TYPE_TRUTH.equals(message.getType())) {
                        lastMessage = message.getQuestion();
                    }else if (ChatMessageContent.TYPE_CHANGE_BG.equals(message.getType())) {
                        lastMessage = Constant.TYPE_CHANGE_BG;
                    }else if (ChatMessageContent.TYPE_GRAFFITI_REVOKE.equals(message.getType())) {
                        lastMessage = Constant.TYPE_GRAFFITI_REVOKE;
                    }
                }

            }
        }
            return lastMessage;
        }

    }