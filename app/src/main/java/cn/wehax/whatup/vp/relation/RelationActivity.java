package cn.wehax.whatup.vp.relation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;

import cn.wehax.whatup.R;
import cn.wehax.whatup.config.IntentKey;
import cn.wehax.whatup.config.RequestCode;
import cn.wehax.whatup.framework.fragment.WXActivity;
import cn.wehax.whatup.model.conversation.ConversationManager;
import cn.wehax.whatup.support.helper.MoveToHelper;
import cn.wehax.whatup.vp.chat.ChatActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by howe on 15/6/9.
 * Email:howejee@gmail.com
 */
@ContentView(R.layout.activity_relation)
public class RelationActivity extends WXActivity implements View.OnClickListener,AdapterView.OnItemClickListener{

    @InjectView(R.id.relation_list)
    SwipeListView mRelationListView;

    @InjectView(R.id.relation_tips)
    TextView tipsTextView;

    @InjectView(R.id.relation_back)
    ImageView mBackBtn;

    @InjectView(R.id.relation_setting)
    ImageView mSettingBtn;

    @Inject
    RelationPresenter presenter;

    @Inject
    ConversationManager conversationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter.setView(this);
        initView();
        presenter.loadData();
    }

    private void initView(){
        mBackBtn.setOnClickListener(this);
        mSettingBtn.setOnClickListener(this);
        mRelationListView.setOnItemClickListener(this);
    }

    public void setAdapter( RelationAdapter adapter){
        mRelationListView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.relation_back:
                this.finish();
                break;

            case R.id.relation_setting:
                MoveToHelper.moveToSetting(this);
                break;
        }
    }

    public void moveToChatActivity(String targetUid,String conversationId,boolean systemRec,String avatarUrl){
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(IntentKey.INTENT_KEY_CONVERSATION_ID,conversationId);
        intent.putExtra(IntentKey.INTENT_KEY_TARGET_UID, targetUid);
        intent.putExtra(IntentKey.INTENT_KEY_TARGET_AVATARURL,avatarUrl);
        if(systemRec){
            intent.putExtra(IntentKey.INTENT_KEY_SYSTEM_REC, true);
        }
        startActivityForResult(intent, RequestCode.REQUEST_CODE_RELATION_TO_CHAT);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        presenter.onItemClick(position);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unregisterReceiver();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("chat","Relation onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("chat","Relation onPause");
    }
}
