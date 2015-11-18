package cn.wehax.whatup.ar.marker;

import android.app.Activity;
import android.opengl.GLSurfaceView;

import cn.wehax.whatup.R;
import cn.wehax.whatup.ar.ModelDataManager;
import cn.wehax.whatup.ar.TextureDataManager;
import cn.wehax.whatup.ar.marker.base.BorderSprite;
import cn.wehax.whatup.ar.marker.base.ViewObject;
import cn.wehax.whatup.ar.texture.TextureLoader;


/**
 * Created by mayuhan on 15/6/23.
 */
public class Status extends BorderSprite implements ViewObject {

    private BorderSprite mBorder;
    private BorderSprite mThumbnail;
    private final static float LEVEL_RANGE = 0.1f;

    public void bind(Activity activity, GLSurfaceView glSurfaceView) {

//        mBorder = new BorderSpirit();
//        mThumbnail = new BorderSpirit();
//
//        mBorder.putTextureData(TextureDataManager.getOriginalData());
//        mBorder.putVertexData(ModelDataManager.getStatusBorderVertexData());
//
//        mThumbnail.putTextureData(TextureDataManager.getOriginalData());
//        mThumbnail.putVertexData(ModelDataManager.getStatusVertexData());

//        this.putTextureData(TextureDataManager.getOriginalData());
        this.putVertexData(ModelDataManager.getStatusVertexData());
        this.setBorderTexture(TextureLoader.load(activity, R.drawable.status_border), TextureDataManager.getStatusBorderData(), 0f, 0f);
    }

//    public void draw(float[] viewProjectMatrix, float[] invertedViewProjectMatrix, TextureShaderProgram program) {
//        mThumbnail.draw(viewProjectMatrix, invertedViewProjectMatrix, program);
//        mBorder.draw(viewProjectMatrix, invertedViewProjectMatrix, program);
//    }
//
//    public void moveTo(float x, float y, float z) {
//        mThumbnail.moveTo(x, y, z);
//        mBorder.moveTo(x, y, z + LEVEL_RANGE);
//    }
//
//    @Override
//    public void translate(float x, float y, float z) {
//        mBorder.translate(x, y, z);
//        mThumbnail.translate(x, y, z);
//    }
//
//    @Override
//    public void rotate(float a, float x, float y, float z) {
//        mBorder.rotate(a, x, y, z);
//        mThumbnail.rotate(a, x, y, z);
//    }
//
//    @Override
//    public void rotateAroundOriginal(float a, float x, float y, float z) {
//        mBorder.rotateAroundOriginal(a, x, y, z);
//        mThumbnail.rotateAroundOriginal(a, x, y, z);
//    }
//
//    public void setStatusThumbnail(int textureHandle) {
//        mThumbnail.setBorderTexture(textureHandle);
//    }
}
