package cn.wehax.whatup.vp.setting.BubbleSetting;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import cn.wehax.whatup.R;

/**
 * Created by sanchibing on 2015/6/30.
 */
public class BubbleAdapter extends BaseAdapter{
    private  Context context;
    public ImageView itemSelectIv;
    int selectedPosition;
    private int[] items = { R.drawable.bubble_hamburg, R.drawable.bubble_cat, R.drawable.bubble_pig, R.drawable.bubble_bulb, R.drawable.bubble_hulk, R.drawable.bubble_tony,
            R.drawable.bubble_steve, R.drawable.bubble_thor, R.drawable.bubble_kitty,R.drawable.bubble_panda,R.drawable.bubble_doraemon};
    BubbleAdapter(Context context, int selectedPosition){
        this.context=context;
        this.selectedPosition=selectedPosition;
    }
    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Integer getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view= View.inflate(context,R.layout.item_bubble,null);
        ImageView itemIv= (ImageView) view.findViewById(R.id.iv_item_bubble);
        itemSelectIv = (ImageView) view.findViewById(R.id.iv_item_bubble_select);
        if(position==selectedPosition){
            itemSelectIv.setVisibility(View.VISIBLE);
        }else{
            itemSelectIv.setVisibility(View.INVISIBLE);
        }
        itemIv.setImageResource(getItem(position));
        return view;
    }
    public void refreshView(int selectedPosition){
        this.selectedPosition=selectedPosition;
        this.notifyDataSetChanged();
    }
}
