package cn.wehax.whatup.ar.marker;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.opengl.GLSurfaceView;

import cn.wehax.whatup.R;
import cn.wehax.whatup.ar.ModelDataManager;
import cn.wehax.whatup.ar.TextureDataManager;
import cn.wehax.whatup.ar.marker.base.PopupViewObject;
import cn.wehax.whatup.ar.texture.DecodeStringTextureListener;
import cn.wehax.whatup.ar.texture.TextureLoader;


/**
 * Created by mayuhan on 15/6/23.
 */
public class Avatar extends PopupViewObject implements
        DecodeStringTextureListener
        , ValueAnimator.AnimatorUpdateListener {

    private final static String TAG = "Avatar";
    private final static float LEVEL_RANGE = 0.1f;


    @Override
    public void bind(Activity activity, GLSurfaceView glSurfaceView) {

        this.putVertexData(ModelDataManager.getAvatarVertexData());
        this.setBorderTexture(TextureLoader.load(activity, R.drawable.avatar_border_female), TextureDataManager.getAvatarBorderData(), 0f, 0f);
        super.bind(activity, glSurfaceView);
    }

    @Override
    public void onDone(int textureHandle, float width, float height) {
//        this.setImageTexture(textureHandle, TextureDataManager.getOriginalData(), 0f, 0f);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {

    }
}
