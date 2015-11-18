package cn.wehax.whatup.vp.guide;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import cn.wehax.whatup.R;
import cn.wehax.whatup.support.util.ScreenUtils;


/**
 * Created by sanchibing on 2015/7/17.
 * Emali:sanchibing@gmail.com
 */
public class GuideUtils {
    private Activity context;

    private WindowManager windowManager;

    private boolean isGridScanning;
    private static GuideUtils instance = null;

    private View guideView;

    /**采用单例设计模式，同时保证这个对象不会存在多个**/
    private GuideUtils() {
    }

    /**采用单例的设计模式，同时用了同步锁**/
    public static GuideUtils getInstance() {
        synchronized (GuideUtils.class) {
            if (null == instance) {
                instance = new GuideUtils();
            }
        }
        return instance;
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    // 设置LayoutParams参数
                    final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                    // 设置显示的类型，TYPE_PHONE指的是来电话的时候会被覆盖，其他时候会在最前端
                    params.type = WindowManager.LayoutParams.TYPE_PHONE;
                    // 设置显示格式
                    params.format = PixelFormat.RGBA_8888;
                    // 设置对齐方式
                    params.gravity = Gravity.LEFT | Gravity.TOP;
                    // 设置宽高
                    params.width = ScreenUtils.getScreenWidth(context);
                    params.height = ScreenUtils.getScreenHeight(context);
                    // 设置动画
                    params.windowAnimations = R.style.view_anim;
                    // 添加到当前的窗口上
                    windowManager.addView(guideView, params);
                    break;
            }
        }
    };

    /**
     * @方法说明:初始化
     * @方法名称:initGuide
     * @返回值:void
     */
    public void initGuide(final Activity context, final View guideView) {
        this.context = context;
        windowManager = context.getWindowManager();

        /**用一个handler延迟显示界面，主要是为了进入界面后，淡入得动画效果，不然的话，引导界面就直接显示出来**/
        this.guideView = guideView;
        handler.sendEmptyMessageDelayed(1, 1000);
        guideView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /** 点击图层之后，将图层移除**/
                switch (view.getId()) {
                    case R.id.gridView_status:
                        windowManager.removeView(guideView);
                        View scanView = View.inflate(context, R.layout.guide_scanning, null);
                        GuideUtils.this.initGuide(context, scanView);
                        break;
                    case R.id.guideView_photograph:
                     /* guideView.findViewById(R.id.take_picture_btn).setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View view) {
                              windowManager.removeView(guideView);
                          }
                      });*/
                        break;
                    case R.id.gridView_scanning:
                        windowManager.removeView(guideView);
                        isGridScanning = true;
                        break;

                }


            }
        });
    }


    public void setContext(Activity context) {
        this.context = context;
    }

    public boolean getIsGridScanning(){
        return isGridScanning;
    }
    public void removeGuideView(View photograph){
        windowManager.removeView(photograph);
        if(photograph.getId() == R.id.gridView_status){
            View scanView = View.inflate(context, R.layout.guide_scanning, null);
            GuideUtils.this.initGuide(context,scanView);
        }
    }



}
