package cn.wehax.whatup.vp.chat.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;

import cn.wehax.whatup.support.util.PreferencesUtils;

/**
 * Created by sanchibing on 2015/7/14.
 */
public class ChatMessageList extends ListView {
    private boolean graffitiSwitch = true;

    private int mPosition;

    InputMethodManager imm;

    public ChatMessageList(Context context) {
        super(context);
        graffitiSwitch = getGraffitiSwitch(context);
    }

    public ChatMessageList(Context context, AttributeSet attrs) {
        super(context, attrs);
        graffitiSwitch = getGraffitiSwitch(context);
    }

    public ChatMessageList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        graffitiSwitch = getGraffitiSwitch(context);
    }
    //重写onTouchEvent，判断是否消耗触摸事件。
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(graffitiSwitch){
            imm.hideSoftInputFromWindow(this.getWindowToken(), 0);
            return super.onTouchEvent(ev);
        }
        return false;

    }
    public void setGraffitiSwitch(boolean graffitiSwitch){
        this.graffitiSwitch =graffitiSwitch;
        //invalidate();


    }
    //覆写dispatchTouchEvent，处理当button获取到焦点时的滚动事件。
/*    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(!graffitiSwitch) {
            final int actionMasked = ev.getActionMasked() & MotionEvent.ACTION_MASK;

            if (actionMasked == MotionEvent.ACTION_DOWN) {
                // 记录手指按下时的位置
                mPosition = pointToPosition((int) ev.getX(), (int) ev.getY());
                return super.dispatchTouchEvent(ev);
            }

            if (actionMasked == MotionEvent.ACTION_MOVE) {
                // 最关键的地方，忽略MOVE 事件
                // ListView onTouch获取不到MOVE事件所以不会发生滚动处理
                return true;
            }

            // 手指抬起时
            if (actionMasked == MotionEvent.ACTION_UP
                    || actionMasked == MotionEvent.ACTION_CANCEL) {
                // 手指按下与抬起都在同一个视图内，交给父控件处理，这是一个点击事件
                if (pointToPosition((int) ev.getX(), (int) ev.getY()) == mPosition) {
                    super.dispatchTouchEvent(ev);
                } else {
                    // 如果手指已经移出按下时的Item，说明是滚动行为，清理Item pressed状态
                    setPressed(false);
                    invalidate();
                    return true;
                }
            }

            return super.dispatchTouchEvent(ev);
        }else {
            return super.dispatchTouchEvent(ev);
        }
    }*/
    private boolean getGraffitiSwitch(Context context){
        imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        return PreferencesUtils.getBoolean(context, "graffitiSwitch", true);

    }
}
