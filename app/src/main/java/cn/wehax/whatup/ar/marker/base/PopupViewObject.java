package cn.wehax.whatup.ar.marker.base;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.util.Log;

/**
 * Created by mayuhan on 15/7/25.
 */
public class PopupViewObject extends BorderSprite implements ViewObject, ValueAnimator.AnimatorUpdateListener {
    private static String TAG = "PopupViewObject";

    private ValueAnimator popupAnimator;
    private float[] originModelMtx;
    private boolean isAnimated = false;

    @Override
    public void bind(Activity activity, GLSurfaceView glSurfaceView) {

        popupAnimator = ValueAnimator.ofFloat(0, 1);
        popupAnimator.setDuration(200);
        popupAnimator.addUpdateListener(this);
        popupAnimator.setRepeatCount(ValueAnimator.INFINITE);
        popupAnimator.setTarget(glSurfaceView);


    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        Log.e(TAG, "onAnimationUpdate");
//        float[] animUpdateMtx = originModelMtx.clone();
//        Matrix.scaleM(animUpdateMtx, 0, 0.25f, 0.5f, 0.25f);
//        setModelMtx(animUpdateMtx);
    }

    public void startAnim(Activity activity) {
        if (isAnimated) {
            return;
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                popupAnimator.start();
                isAnimated = true;
            }
        });
    }

    public boolean isAnimationStarted() {
        return isAnimated;
    }
}
