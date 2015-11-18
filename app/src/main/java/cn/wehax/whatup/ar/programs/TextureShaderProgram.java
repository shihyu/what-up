package cn.wehax.whatup.ar.programs;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.FloatBuffer;

import cn.wehax.whatup.R;
import cn.wehax.whatup.ar.Config;
import cn.wehax.whatup.ar.ShaderManager;
import cn.wehax.whatup.ar.utils.GlHelper;


/**
 * Created by mayuhan on 15/6/29.
 */
public class TextureShaderProgram {

    private static final String U_MVPMATRIX = "uMVPMatrix";
    private static final String U_TEX_BORDER = "uTexBorder";
    private static final String U_TEX_IMAGE = "uTexImage";
    private static final String U_TEX_CONTENT = "uTexContent";

    private static final String A_POSTION = "aPosition";
    private static final String A_BORDER_COORD = "aBorderCoord";
    private static final String A_IMAGE_COORD = "aImageCoord";
    private static final String A_CONTENT_COORD = "aContentCoord";

    private static final String U_BORDER_EDGE = "uBorderEdge";
    private static final String U_IMAGE_EDGE = "uImageEdge";
    private static final String U_CONTENT_EDGE = "uContentEdge";


    private final int uMVPMatrix;
    private final int aPosition;
    private final int aBorderCoord;
    private final int aImageCoord;
    private final int aContentCoord;

    private final int uTexBorder;
    private final int uTexImage;
    private final int uTexContent;
    private final int uBorderEdgeHandle;
    private final int uImageEdgeHandle;
    private final int uContentEdgeHandle;


    private FloatBuffer vertexBuffer;
    private FloatBuffer borderTextureBuffer;
    private FloatBuffer imageTextureBuffer;
    private FloatBuffer contentTextureBuffer;

    private final int program;

    public TextureShaderProgram(Context context) {

        program = GlHelper.createProgram(ShaderManager.readRawTextFile(context, R.raw.border_spirit_vertex),
                ShaderManager.readRawTextFile(context, R.raw.border_spirit_fragment_test));
        aPosition = GLES20.glGetAttribLocation(program, A_POSTION);
        aBorderCoord = GLES20.glGetAttribLocation(program, A_BORDER_COORD);
        aImageCoord = GLES20.glGetAttribLocation(program, A_IMAGE_COORD);
        aContentCoord = GLES20.glGetAttribLocation(program, A_CONTENT_COORD);

        uBorderEdgeHandle = GLES20.glGetUniformLocation(program, U_BORDER_EDGE);
        uImageEdgeHandle = GLES20.glGetUniformLocation(program, U_IMAGE_EDGE);
        uContentEdgeHandle = GLES20.glGetUniformLocation(program, U_CONTENT_EDGE);

        uMVPMatrix = GLES20.glGetUniformLocation(program, U_MVPMATRIX);
        uTexBorder = GLES20.glGetUniformLocation(program, U_TEX_BORDER);
        uTexImage = GLES20.glGetUniformLocation(program, U_TEX_IMAGE);
        uTexContent = GLES20.glGetUniformLocation(program, U_TEX_CONTENT);

        vertexBuffer = GlHelper.createBuffer(Config.VERTEXS_BUFFER_LENGHT);
        borderTextureBuffer = GlHelper.createBuffer(Config.BORDER_TEXTURE_BUFFER_LENGTH);
        imageTextureBuffer = GlHelper.createBuffer(Config.IMAGE_TEXTURE_BUFFER_LENGTH);
        contentTextureBuffer = GlHelper.createBuffer(Config.CONTENT_TEXTURE_BUFFER_LENGTH);


    }

    public void setBorderTexture(int textureId) {

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

        GLES20.glUniform1i(uTexBorder, 0);

        checkInfo("setBorderTexture");

    }

    public void setImageTexture(int textureId) {

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

        GLES20.glUniform1i(uTexImage, 1);

        checkInfo("setBorderTexture");
    }


    public void setContentTexture(int textureId) {

        GLES20.glActiveTexture(GLES20.GL_TEXTURE2);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

        GLES20.glUniform1i(uTexContent, 2);

        checkInfo("setBorderTexture");
    }


    public int getVertexPositionHandle() {
        return aPosition;
    }

    public int getBorderCoordHandle() {
        return aBorderCoord;
    }

    public int getImageCoordHandle() {
        return aImageCoord;
    }



    public int getContentCoordHandle() {
        return aContentCoord;
    }

    public int getMVPMatrixHandle() {
        return uMVPMatrix;
    }

    public int getBorderEdgeHandle() {
        return uBorderEdgeHandle;
    }

    public int getImageEdgeHandle() {
        return uImageEdgeHandle;
    }

    public int getContentEdgeHandle() {
        return uContentEdgeHandle;
    }

    public void useProgram() {
        GLES20.glUseProgram(program);
        checkInfo("useProgram");
    }

    public FloatBuffer getVertexBuffer() {
        return vertexBuffer;
    }

    public FloatBuffer getBorderTextureBuffer() {
        return borderTextureBuffer;
    }

    public FloatBuffer getImageTextureBuffer() {
        return imageTextureBuffer;
    }

    public FloatBuffer getContentTextureBuffer() {
        return contentTextureBuffer;
    }


    private void checkInfo(String action) {
        GlHelper.checkGlError(action);
    }
}
