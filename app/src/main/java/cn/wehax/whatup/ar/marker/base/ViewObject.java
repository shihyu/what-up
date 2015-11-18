package cn.wehax.whatup.ar.marker.base;

import android.app.Activity;
import android.opengl.GLSurfaceView;

import cn.wehax.whatup.ar.programs.TextureShaderProgram;


/**
 * Created by mayuhan on 15/6/30.
 */
public interface ViewObject {

    void draw(float[] projectMtx, float[] viewMtx, int[] viewPort, TextureShaderProgram program);

    void bind(Activity activity, GLSurfaceView glSurfaceView);

    void moveTo(float x, float y, float z);

    void translate(float x, float y, float z);

    void rotate(float a, float x, float y, float z);

}
