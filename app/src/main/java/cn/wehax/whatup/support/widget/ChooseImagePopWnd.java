package cn.wehax.whatup.support.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import cn.wehax.whatup.R;

/**
 * Created by howe on 15/6/16.
 * Email:howejee@gmail.com
 */
public class ChooseImagePopWnd extends PopupWindow implements View.OnClickListener{

    OnChooseListener listener;

    public ChooseImagePopWnd(Context context) {
        super(context);
        init(context);
    }


    private void init(Context context){
        View view = View.inflate(context, R.layout.popwnd_choose_image,null);
        view.findViewById(R.id.choose_image_camera).setOnClickListener(this);
        view.findViewById(R.id.choose_image_album).setOnClickListener(this);
        view.findViewById(R.id.choose_image_cancel).setOnClickListener(this);
        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(false);
        this.setBackgroundDrawable(new ColorDrawable());
        this.setContentView(view);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.choose_image_album:
                if(listener != null){
                    listener.openAlbum();
                }
                this.dismiss();
                break;
            case R.id.choose_image_camera:
                if(listener != null){
                    listener.openCamera();
                }
                this.dismiss();
                break;
            case R.id.choose_image_cancel:
                this.dismiss();
                break;
        }
    }

    public void show(View parent){
        if(!this.isShowing()){
            this.showAtLocation(parent, Gravity.BOTTOM,0,0);
        }
    }

    public void setOnChooseListener(OnChooseListener listener){
        this.listener = listener;
    }

    public interface OnChooseListener{
        public void openCamera();
        public void openAlbum();
    }
}
