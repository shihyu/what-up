package cn.wehax.whatup.ar.texture;

import android.view.View;

import com.nostra13.universalimageloader.core.assist.FailReason;

/**
 * Created by mayuhan on 15/7/9.
 */
public abstract class LoadingNetTextureListener {

    public void onLoadingStarted(String imageUri, View view) {
    }

    abstract public void onLoadingFailed(String imageUri, View view, FailReason failReason);

    abstract public void onLoadingComplete(String imageUri, View view, int textureHandle);

    public void onLoadingCancelled(String imageUri, View view) {
    }
}
