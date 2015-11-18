package cn.wehax.whatup.ar.animation;

import android.animation.ValueAnimator;
import android.opengl.GLSurfaceView;

import java.util.HashMap;

/**
 * Created by mayuhan on 15/7/25.
 */
public class GLAnimator extends ValueAnimator {

    private GLSurfaceView glSurfaceView;
    private HashMap<Object, Object> updateListenerMap;

    public GLAnimator bindToGLSurface(GLSurfaceView glSurfaceView) {
        this.glSurfaceView = glSurfaceView;
        return this;
    }

    @Override
    public void addUpdateListener(AnimatorUpdateListener listener) {
        if (glSurfaceView == null) {
            throw new RuntimeException("GLAnimator need bind to a GLSurfaceView before add update listener");
        }
        if (updateListenerMap == null) {
            updateListenerMap = new HashMap<>();
        }

        UiThreadAnimatorUpdateListener uiThreadAnimatorUpdateListener = new UiThreadAnimatorUpdateListener(this.glSurfaceView, listener);
        updateListenerMap.put(listener, uiThreadAnimatorUpdateListener);
        super.addUpdateListener(uiThreadAnimatorUpdateListener);
    }

    @Override
    public void removeAllListeners() {
        if (updateListenerMap == null) {
            return;
        }
        super.removeAllListeners();
        updateListenerMap.clear();
    }

    @Override
    public void removeUpdateListener(AnimatorUpdateListener listener) {
        if (updateListenerMap == null) {
            return;
        }

        if (updateListenerMap.containsKey(listener)) {
            UiThreadAnimatorUpdateListener uiThreadAnimatorUpdateListener = (UiThreadAnimatorUpdateListener) updateListenerMap.get(listener);
            super.removeUpdateListener(uiThreadAnimatorUpdateListener);
        }
    }

    @Override
    public GLAnimator clone() {

        GLAnimator animator = (GLAnimator) super.clone();
        if (updateListenerMap != null) {
            animator.updateListenerMap.putAll(updateListenerMap);
        }
        return animator;
    }

    @Override
    public void addListener(AnimatorListener listener) {
        super.addListener(listener);
    }

    private class UiThreadAnimatorUpdateListener implements AnimatorUpdateListener {

        private GLSurfaceView glSurfaceView;
        private AnimatorUpdateListener glThreadListener;

        UiThreadAnimatorUpdateListener(GLSurfaceView glSurfaceView, AnimatorUpdateListener glThreadListener) {
            this.glSurfaceView = glSurfaceView;
            this.glThreadListener = glThreadListener;
        }

        @Override
        public void onAnimationUpdate(final ValueAnimator animation) {
            if (glSurfaceView.isShown()) {
                glSurfaceView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        glThreadListener.onAnimationUpdate(animation);
                    }
                });
            }
        }
    }
}
