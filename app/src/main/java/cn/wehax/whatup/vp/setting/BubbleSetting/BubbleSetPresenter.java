package cn.wehax.whatup.vp.setting.BubbleSetting;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;
import cn.wehax.whatup.config.PreferenceKey;
import cn.wehax.whatup.framework.presenter.BasePresenter;
import cn.wehax.whatup.support.util.PreferencesUtils;
import cn.wehax.whatup.vp.chat.widget.BubbleView;

/**
 * Created by sanchibing on 2015/6/30.
 */
public class BubbleSetPresenter extends BasePresenter {
    private Map<Integer, String> map = new HashMap<>();
    public BubbleSetPresenter(){
        if(map.size()>0){
            map.clear();
        }
        for (int i = 0; i < bubbleNames.length; i++) {
            map.put(i, bubbleNames[i]);
        }
    }
    private String[] bubbleNames = {BubbleView.BubbleStyle.BUBBLE_HAMBURG, BubbleView.BubbleStyle.BUBBLE_CAT, BubbleView.BubbleStyle.BUBBLE_PIG,
            BubbleView.BubbleStyle.BUBBLE_BULB, BubbleView.BubbleStyle.BUBBLE_HULK, BubbleView.BubbleStyle.BUBBLE_TONY, BubbleView.BubbleStyle.BUBBLE_STEVE,
            BubbleView.BubbleStyle.BUBBLE_THOR, BubbleView.BubbleStyle.BUBBLE_KITTY, BubbleView.BubbleStyle.BUBBLE_PANDA, BubbleView.BubbleStyle.BUBBLE_DORAEMON};

    public void chooseBubble(Context context, int position, BubbleAdapter adapter) {

        PreferencesUtils.putString(context, PreferenceKey.PREFERENCE_KEY_BUBBLE_STYLE, map.get(position));
        if (adapter != null) {
            adapter.refreshView(position);
        }


    }
    public int selectedPosition (Context context) {
        String selectedBubble = PreferencesUtils.getString(context, PreferenceKey.PREFERENCE_KEY_BUBBLE_STYLE, BubbleView.BubbleStyle.BUBBLE_HAMBURG);
        for (int i = 0; i < bubbleNames.length; i++) {
            if (bubbleNames[i].equals(selectedBubble)) {
                return i;
            }
        }
        return 0;
    }
}
