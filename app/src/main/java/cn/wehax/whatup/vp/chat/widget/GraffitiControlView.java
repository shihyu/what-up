package cn.wehax.whatup.vp.chat.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.avos.avoscloud.LogUtil;

import cn.wehax.util.DensityUtil;
import cn.wehax.whatup.R;
import cn.wehax.whatup.support.util.PreferencesUtils;
import cn.wehax.whatup.support.util.ToastUtils;
import roboguice.inject.InjectView;

/**
 * Created by howe on 15/6/17.
 * Email:howejee@gmail.com
 */
public class GraffitiControlView extends PopupWindow implements View.OnClickListener{

    LinearLayout mColorSelectLayout;

    Button mColorSelectBtn;

    Button mRevokeBtn;

    CheckBox graffiti_switch;

    private boolean isGraffitiSwitch ;


    OnGraffitiControlListener listener;

    public GraffitiControlView(Context context,int width,int height) {
        super(context);
        this.setWidth(width);
        this.setHeight(height);
        init(context);
    }

    private void init(Context context){
        View view = View.inflate(context, R.layout.popwnd_graffiti_control,null);
        view.findViewById(R.id.chat_color_red).setOnClickListener(this);
        view.findViewById(R.id.chat_color_blue).setOnClickListener(this);
        view.findViewById(R.id.chat_color_yellow).setOnClickListener(this);
        view.findViewById(R.id.chat_color_white).setOnClickListener(this);
        mRevokeBtn = (Button) view.findViewById(R.id.chat_revoke);
        mRevokeBtn.setOnClickListener(this);

        mColorSelectBtn = (Button) view.findViewById(R.id.chat_color_select_btn);
        mColorSelectBtn.setOnClickListener(this);
        mColorSelectLayout = (LinearLayout) view.findViewById(R.id.chat_color_select_layout);
        mColorSelectLayout.setOnClickListener(this);
        graffiti_switch = (CheckBox) view.findViewById(R.id.graffiti_switch);
        graffiti_switch.setOnClickListener(this);
        //取出保存的涂鸦开关状态，设置保持上次的状态
        isGraffitiSwitch = PreferencesUtils.getBoolean(context, "graffitiSwitch", true);
        graffiti_switch.setChecked(isGraffitiSwitch);
        setControlViewAble(!isGraffitiSwitch);



        this.setFocusable(false);
        this.setTouchable(true);
        this.setOutsideTouchable(false);

        this.setBackgroundDrawable(new ColorDrawable());

        this.setContentView(view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.chat_revoke:

                if(listener != null){
                    listener.onRevoke();
                }
                break;
            case R.id.graffiti_switch:

                if(listener != null){
                    listener.onGraffitiSwitch(graffiti_switch);
                }
                break;

            case R.id.chat_color_select_btn:
                if (mColorSelectLayout.getVisibility() == View.VISIBLE) {
                    mColorSelectLayout.setVisibility(View.INVISIBLE);
                } else {
                    mColorSelectLayout.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.chat_color_red:

                mColorSelectBtn.setBackgroundResource(R.drawable.chat_graffiti_bg_red);
                mColorSelectLayout.setVisibility(View.INVISIBLE);
                selectColor(R.color.chat_graffiti_red);

                break;

            case R.id.chat_color_yellow:
                mColorSelectBtn.setBackgroundResource(R.drawable.chat_graffiti_bg_yellow);
                mColorSelectLayout.setVisibility(View.INVISIBLE);
                selectColor(R.color.chat_graffiti_yellow);
                break;

            case R.id.chat_color_blue:
                mColorSelectBtn.setBackgroundResource(R.drawable.chat_graffiti_bg_blue);
                mColorSelectLayout.setVisibility(View.INVISIBLE);
                selectColor(R.color.chat_graffiti_blue);
                break;

            case R.id.chat_color_white:
                mColorSelectBtn.setBackgroundResource(R.drawable.chat_graffiti_bg_white);
                mColorSelectLayout.setVisibility(View.INVISIBLE);
                selectColor(R.color.chat_graffiti_white);
                break;
        }
    }

    private void selectColor(int colorResId){
        if(listener != null){
            listener.onSelectColor(colorResId);
        }
    }

    public void setOnGraffitiControlListener(OnGraffitiControlListener listener){
        this.listener = listener;
    }

    public interface OnGraffitiControlListener{
        void onRevoke();
        void onGraffitiSwitch(CheckBox graffitiSwitch);
        void onSelectColor(int colorResId);
    }
    public void setControlViewAble(boolean flag){
        mColorSelectBtn.setEnabled(flag);
        mRevokeBtn.setEnabled(flag);
        if(flag){
            mColorSelectBtn.setVisibility(View.VISIBLE);
            mRevokeBtn.setVisibility(View.VISIBLE);
        }else {
            mColorSelectBtn.setVisibility(View.INVISIBLE);
            mRevokeBtn.setVisibility(View.INVISIBLE);
        }


    }
}
