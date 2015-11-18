package cn.wehax.whatup.vp.chat.widget;

import android.app.Activity;
import android.content.Context;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import cn.wehax.util.DensityUtil;
import cn.wehax.whatup.R;
import cn.wehax.whatup.model.chatView.ChatMessageContent;
import cn.wehax.whatup.support.util.StringUtils;
import cn.wehax.whatup.vp.chat.ChatUtils;

/**
 * Created by sanchibing on 2015/7/14.
 */
public class ChatAdapter extends BaseAdapter {

    public static final int IMVT_COM_MSG = 1;

    public static final int IMVT_FROM_MSG = 0;

    int screenWidth;

    List<String> list ;

    private TextPaint textPaint = new TextPaint();

    private Context context;
    //真心话答案点击监听事件
    private OnButtonClickListen onButtonClickListen;

    private DisplayImageOptions options;


    private List<ChatMessageContent> dataList = new ArrayList<ChatMessageContent>();

    public ChatAdapter(Activity context, OnButtonClickListen onButtonClickListen) {
        this.context = context;
        options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
        this.onButtonClickListen = onButtonClickListen;
        WindowManager wm = context.getWindowManager();
        screenWidth = wm.getDefaultDisplay().getWidth();
    }

    @Override
    public int getCount() {

        return dataList.size();
    }

    @Override
    public ChatMessageContent getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessageContent message = dataList.get(position);
        boolean isSelf = message.isSelf();
        if (isSelf) {
            return ChatAdapter.IMVT_FROM_MSG;
        }

        return ChatAdapter.IMVT_COM_MSG;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ChatMessageContent message = getItem(position);
        boolean isSelf = message.isSelf();
        int type = getItemViewType(position);
        RightViewHolder rightViewHolder = null;
        LeftViewHolder leftViewHolder = null;
        if (convertView == null) {
            switch (type) {
                case IMVT_FROM_MSG:
                    rightViewHolder = new RightViewHolder();
                    convertView = View.inflate(context, R.layout.item_message_right, null);
                    rightViewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
                    rightViewHolder.bubbleViewLayout = (LinearLayout) convertView.findViewById(R.id.bubble_view_message);
                    rightViewHolder.itemLayout = (LinearLayout) convertView.findViewById(R.id.ll_message_item);
                    rightViewHolder.buttonLayout = (LinearLayout) convertView.findViewById(R.id.answer_button);
                    rightViewHolder.systemRec = (TextView) convertView.findViewById(R.id.tv_systemRec);
                    setTime(rightViewHolder, message, position);
                    setViewData(rightViewHolder, isSelf, message);
                    if (message.isSystemMsg()) {
                        rightViewHolder.systemRec.setVisibility(View.VISIBLE);
                    } else {
                        rightViewHolder.systemRec.setVisibility(View.GONE);
                    }
                    convertView.setTag(rightViewHolder);
                    break;
                case IMVT_COM_MSG:
                    leftViewHolder = new LeftViewHolder();
                    convertView = View.inflate(context, R.layout.item_message_left, null);
                    leftViewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
                    leftViewHolder.bubbleViewLayout = (LinearLayout) convertView.findViewById(R.id.bubble_view_message);
                    leftViewHolder.itemLayout = (LinearLayout) convertView.findViewById(R.id.ll_message_item);
                    leftViewHolder.buttonLayout = (LinearLayout) convertView.findViewById(R.id.answer_button);
                    leftViewHolder.systemRec = (TextView) convertView.findViewById(R.id.tv_systemRec);
                    setTime(leftViewHolder, message, position);
                    setViewData(leftViewHolder, isSelf, message);
                    if (message.isSystemMsg()) {
                        leftViewHolder.systemRec.setVisibility(View.VISIBLE);
                    } else {
                        leftViewHolder.systemRec.setVisibility(View.GONE);
                    }
                    convertView.setTag(leftViewHolder);
                    break;
            }

        } else {
            switch (type) {
                case IMVT_FROM_MSG:
                    rightViewHolder = (RightViewHolder) convertView.getTag();
                    //增加时间设置
                    setTime(rightViewHolder, message, position);
                    //添加消息显示
                    setViewData(rightViewHolder, isSelf, message);
                    if (message.isSystemMsg()) {
                        rightViewHolder.systemRec.setVisibility(View.VISIBLE);
                    } else {
                        rightViewHolder.systemRec.setVisibility(View.GONE);
                    }
                    break;
                case IMVT_COM_MSG:
                    leftViewHolder = (LeftViewHolder) convertView.getTag();
                    setTime(leftViewHolder, message, position);
                    setViewData(leftViewHolder, isSelf, message);
                    if (message.isSystemMsg()) {
                        leftViewHolder.systemRec.setVisibility(View.VISIBLE);
                    } else {
                        leftViewHolder.systemRec.setVisibility(View.GONE);
                    }
                    break;
            }

        }

        return convertView;


    }

    static class RightViewHolder extends BaseViewHolder {
        public TextView tvTime;
        public LinearLayout bubbleViewLayout;
        public LinearLayout itemLayout;
        public LinearLayout buttonLayout;
        public TextView systemRec;
    }

    static class LeftViewHolder extends BaseViewHolder {
        public TextView tvTime;
        public LinearLayout bubbleViewLayout;
        public LinearLayout itemLayout;
        public LinearLayout buttonLayout;
        public TextView systemRec;
    }

    static class BaseViewHolder {
    }

    public void addMessage(ChatMessageContent chatMessageContent) {
        dataList.add(chatMessageContent);
        this.notifyDataSetChanged();
    }

    public void addMessage(List<ChatMessageContent> chatMessageContents) {
        dataList.addAll(chatMessageContents);
        this.notifyDataSetChanged();

    }

    public List<ChatMessageContent> getDataList() {
        return dataList;
    }

    public interface OnButtonClickListen {
        void onButtonClick(String msg,boolean flag);

    }

    private int getTextWidth(String text) {
        textPaint.setTextSize(DensityUtil.sp2px(context, 14));
        return (int) textPaint.measureText(text, 0, text.length());
    }

    private void setViewData(final BaseViewHolder viewHolder, boolean isSelf, final ChatMessageContent message) {
        //增加时间设置
        int[] style = BubbleView.BubbleStyle.getStyle(message.getBubbleStyle());
        int borderStyle;
        int tailStyle;
        if (!isSelf) {
            borderStyle = style[0];
            tailStyle = style[1];
        } else {
            borderStyle = style[2];
            tailStyle = style[3];
        }

        //viewHolder.tvTime.setText(time);
        //增加聊天气泡
        BubbleView bubbleView = new BubbleView(context);
        bubbleView.setAvatar(R.drawable.default_avatar);
        bubbleView.setBorder(borderStyle);
        bubbleView.setContentBackground(style[4]);
        bubbleView.setTextColor(context.getResources().getColor(style[5]));
        bubbleView.setTail(tailStyle);
        if (isSelf) {
            bubbleView.setViewPosition(BubbleView.POSITION_RIGHT);
        } else {
            bubbleView.setViewPosition(BubbleView.POSITION_LEFT);
        }

        bubbleView.onViewLayout();
        if (message.getType().equals(ChatMessageContent.TYPE_TEXT)) {
            bubbleView.setText(message.getText());
        } else if (message.getType().equals(ChatMessageContent.TYPE_TRUTH)) {
            bubbleView.setText(message.getQuestion());
        }
        String avatar = message.getAvatar();
        if (!TextUtils.isEmpty(avatar) && avatar.startsWith("http")) {
            ImageLoader.getInstance().displayImage(avatar, bubbleView.getAvatarImageView(), options);
        }
        if (viewHolder instanceof RightViewHolder) {
            RightViewHolder rightViewHolder = (RightViewHolder) viewHolder;
            rightViewHolder.bubbleViewLayout.removeAllViews();
            rightViewHolder.buttonLayout.removeAllViews();
            rightViewHolder.bubbleViewLayout.addView(bubbleView);
        } else if (viewHolder instanceof LeftViewHolder) {
            LeftViewHolder leftViewHolder = (LeftViewHolder) viewHolder;
            leftViewHolder.bubbleViewLayout.removeAllViews();
            leftViewHolder.buttonLayout.removeAllViews();
            leftViewHolder.bubbleViewLayout.addView(bubbleView);
        }


        //添加真心话答案
        if (message.getType().equals(ChatMessageContent.TYPE_TRUTH)) {
            final String answer = message.getAnswers();
            LinearLayout.LayoutParams params = null;
            if (viewHolder instanceof RightViewHolder) {
                RightViewHolder rightViewHolder = (RightViewHolder) viewHolder;
                rightViewHolder.buttonLayout.removeAllViews();
                params = (LinearLayout.LayoutParams) rightViewHolder.buttonLayout.getLayoutParams();
            } else if (viewHolder instanceof LeftViewHolder) {
                LeftViewHolder leftViewHolder = (LeftViewHolder) viewHolder;
                leftViewHolder.buttonLayout.removeAllViews();
                params = (LinearLayout.LayoutParams) leftViewHolder.buttonLayout.getLayoutParams();
            }
            int width = getTextWidth(message.getQuestion()) + DensityUtil.dp2px(context, 34);
            if (width > (screenWidth - DensityUtil.dp2px(context, 132))) {
                params.width = screenWidth - DensityUtil.dp2px(context, 132);
            } else {
                params.width = width;
            }
            if (viewHolder instanceof RightViewHolder) {
                RightViewHolder rightViewHolder = (RightViewHolder) viewHolder;

                rightViewHolder.buttonLayout.setLayoutParams(params);
            } else if (viewHolder instanceof LeftViewHolder) {
                LeftViewHolder leftViewHolder = (LeftViewHolder) viewHolder;

                leftViewHolder.buttonLayout.setLayoutParams(params);
            }
            if (!TextUtils.isEmpty(answer)) {
                final List<String> answers = StringUtils.strToList(answer);
                for (int i = 0; i < answers.size(); i++) {
                    final String ans = answers.get(i);
                    final Button button = new Button(context);
                    button.setText(ans);
                    button.getBackground().setAlpha(210);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                if (!message.isReply()) {
                                    message.setIsReply(true);
                                    onButtonClickListen.onButtonClick(ans, true);
                                } else {
                                    onButtonClickListen.onButtonClick(ans, false);
                                }

                            }
                    });
                    if (viewHolder instanceof RightViewHolder) {
                        RightViewHolder rightViewHolder = (RightViewHolder) viewHolder;
                        rightViewHolder.buttonLayout.addView(button);
                    } else if (viewHolder instanceof LeftViewHolder) {
                        LeftViewHolder leftViewHolder = (LeftViewHolder) viewHolder;
                        leftViewHolder.buttonLayout.addView(button);
                    }


                }
            }
        }
    }

    private void setTime(BaseViewHolder viewHolder, ChatMessageContent message, int position) {
        String time = ChatUtils.getChatTime(message.getTime());
        if (viewHolder instanceof RightViewHolder) {
            RightViewHolder rightViewHolder = (RightViewHolder) viewHolder;
            if (position > 0 && (getItem(position).getTime() - getItem(position - 1).getTime()) < 60 * 1000) {
                rightViewHolder.tvTime.setVisibility(View.GONE);
            } else {
                rightViewHolder.tvTime.setVisibility(View.VISIBLE);
                rightViewHolder.tvTime.setText(time);
            }
        } else if (viewHolder instanceof LeftViewHolder) {
            LeftViewHolder leftViewHolder = (LeftViewHolder) viewHolder;
            if (position > 0 && (getItem(position).getTime() - getItem(position - 1).getTime()) < 60 * 1000) {
                leftViewHolder.tvTime.setVisibility(View.GONE);
            } else {
                leftViewHolder.tvTime.setVisibility(View.VISIBLE);
                leftViewHolder.tvTime.setText(time);
            }
        }

    }

}


