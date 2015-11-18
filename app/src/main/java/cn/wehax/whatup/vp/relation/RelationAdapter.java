package cn.wehax.whatup.vp.relation;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.LogUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import cn.wehax.whatup.R;
import cn.wehax.whatup.config.Constant;
import cn.wehax.whatup.model.conversation.Conversation;
import cn.wehax.whatup.model.conversation.ConversationManager;
import cn.wehax.whatup.support.helper.MoveToHelper;


/**
 * Created by howe on 15/6/9.
 * Email:howejee@gmail.com
 */
public class RelationAdapter extends BaseAdapter {
    List<Conversation> userRelationsList = new ArrayList<>();
    RelationPresenter presenter;
    DisplayImageOptions options;
    private int mRightWidth = 0;


    /**
     * 单击事件监听器
     */
    private onRemoveItemClickListener removeItemClickListener ;


    public RelationAdapter(RelationPresenter presenter,int rightWidth) {
        this.presenter = presenter;
        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .build();
        mRightWidth = rightWidth;
    }



    @Override
    public int getCount() {
        return userRelationsList.size();
    }

    @Override
    public Conversation getItem(int position) {
        return userRelationsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Conversation relationData =getItem(position);
        String lastMessage = presenter.getLocalLastMessage(relationData.getConversationId());
       final Holder holder;


        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.item_relation, null);
            holder = new Holder(convertView);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        holder.item_left.setLayoutParams(lp1);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(mRightWidth, LinearLayout.LayoutParams.MATCH_PARENT);

//        lp2.gravity = Gravity.CENTER_VERTICAL;
        holder.item_right.setLayoutParams(lp2);



        final int pos = position;
        holder.item_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (removeItemClickListener != null) {
                    removeItemClickListener.onRemoveItemClick(pos, relationData.getConversationId());
                }
            }
        });

        holder.nickname.setText(relationData.getTargetNickname());
        Drawable drawable;

        if (relationData.getTargetSex() == 0) {

            drawable = parent.getContext().getResources().getDrawable(R.drawable.male);
        } else {
            drawable = parent.getContext().getResources().getDrawable(R.drawable.female);
        }
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        holder.nickname.setCompoundDrawables(drawable, null, null, null);
        //设置时间

        String timeStr = timeFormat(relationData.getTime());
        if(TextUtils.isEmpty(lastMessage)&& relationData.isTemp()){

            holder.lastMsg.setText(Constant.SYSTEM_REC);
        } else {
            holder.lastMsg.setText(lastMessage);
        }
         holder.time.setText(timeStr);
        if(relationData.hasUnread()){
            holder.circle.setBackgroundResource(R.drawable.relation_solid_circle);
        }else {
            holder.circle.setBackgroundResource(R.drawable.relation_circle);
        }
        String avatarUrl =relationData.getTargetAvatar();

        if (!TextUtils.isEmpty(avatarUrl)) {
            ImageLoader.getInstance().displayImage(avatarUrl, holder.avatar, options);
        }
        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoveToHelper.moveToOtherHomepage(presenter.getActivity(), relationData.getTargetUid());
            }
        });

        return convertView;
    }

    private String timeFormat(long time) {
        long curTime = System.currentTimeMillis();
        int beforeTime = (int) ((curTime - time));
        long day = beforeTime / (24 * 60 * 60 * 1000);
        long hour = (beforeTime / (60 * 60 * 1000) - day * 24);
        long min = ((beforeTime / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (beforeTime / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        if (day != 0) {
            return Math.abs(day) + "天前";
        } else if (hour != 0) {
            return Math.abs(hour) + "小时前";
        } else if (min != 0) {
            return Math.abs(min) + " 分钟前";
        } else {
            return Math.abs(s) + " 秒前";
        }
    }

    private class Holder {
        TextView nickname;
        TextView lastMsg;
        ImageView avatar;
        TextView time;
        View circle;
        View line;
        ImageView delBtn;
        LinearLayout item_left;
        RelativeLayout item_right;
        LinearLayout userLayout;

        public Holder(View view) {
            item_left = (LinearLayout)view.findViewById(R.id.item_left);
            userLayout = (LinearLayout)view.findViewById(R.id.relation_user_layout);
            item_right = (RelativeLayout)view.findViewById(R.id.item_right);
            nickname = (TextView) view.findViewById(R.id.relation_nickname);
            lastMsg = (TextView) view.findViewById(R.id.relation_last_msg);
            avatar = (ImageView) view.findViewById(R.id.relation_avatar);
            time = (TextView) view.findViewById(R.id.relation_time);
//            line = view.findViewById(R.id.relation_line);
            circle = view.findViewById(R.id.relation_circle);
            delBtn = (ImageView) view.findViewById(R.id.relation_item_del_icon);
        }
    }

    public void setDate(List<Conversation> userRelationsList) {
        this.userRelationsList = userRelationsList;
        LogUtil.log.e("userRelationsList", userRelationsList.size() + "");
        this.notifyDataSetChanged();
    }



    public void setonRemoveItemClickListener(onRemoveItemClickListener listener){
        removeItemClickListener = listener;
    }

    public interface onRemoveItemClickListener {
        void onRemoveItemClick(int pos,String conversationId);
    }
}
