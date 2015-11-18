package cn.wehax.whatup.vp.chat.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import cn.wehax.util.DensityUtil;
import cn.wehax.whatup.R;
import cn.wehax.whatup.config.Constant;

/**
 * Created by howe on 15/6/17.
 * Email:howejee@gmail.com
 */
public class ChatTopControlView extends PopupWindow implements View.OnClickListener{

    private OnTopViewListener listener;

    TextView titleText;

    Drawable male;

    Drawable female;

    public ChatTopControlView(Context context, int width, int height) {
        super(context);
        this.setWidth(width);
        this.setHeight(height);
        init(context);
    }

    private void init(Context context){
        View view = View.inflate(context, R.layout.part_chat_top,null);
        view.findViewById(R.id.chat_back).setOnClickListener(this);
        titleText = (TextView) view.findViewById(R.id.chat_title);
        this.setFocusable(false);
        this.setTouchable(true);
        this.setOutsideTouchable(false);

        this.setBackgroundDrawable(new ColorDrawable());

        this.setContentView(view);
        male = context.getResources().getDrawable(R.drawable.ic_male);
        female = context.getResources().getDrawable(R.drawable.ic_female);
        titleText.setCompoundDrawablePadding( DensityUtil.dp2px(context,5));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.chat_back:
                if(listener != null){
                    listener.onBack();
                }
                break;
        }
    }

    public void setTitle(String title){
        titleText.setText(title);
    }
    public void setSex(int sex ){
        if(sex == Constant.SEX_MALE){
            titleText.setCompoundDrawablesWithIntrinsicBounds(null,null,male,null);
        }else {
            titleText.setCompoundDrawablesWithIntrinsicBounds(null,null,female,null);
        }
        this.update();
    }

    public void setOnTopViewListener(OnTopViewListener listener){
        this.listener = listener;
    }

    public interface OnTopViewListener{
        public void onBack();


    }
}
