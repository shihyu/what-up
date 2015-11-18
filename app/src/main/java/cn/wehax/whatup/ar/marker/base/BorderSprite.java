package cn.wehax.whatup.ar.marker.base;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.FloatBuffer;

import cn.wehax.whatup.ar.programs.TextureShaderProgram;
import cn.wehax.whatup.ar.utils.GlHelper;

/**
 * Created by mayuhan on 15/6/24.
 */
public class BorderSprite {

    /**
     * 绘制图形时，每个顶点xyz三分坐标
     */
    public static int COORDS_PER_VERTEX_3D = 3;

    /**
     * 纹理映射时，每个顶点对应贴图的uv两分坐标
     */
    public static int COORDS_PER_VERTEX_UV = 2;

    /**
     * 每个ViewObj都视为一个空间中的正方行，所以顶点数量为4
     */
    public static int VERTEX_CONT = 4;

    private float[] modelMtx = new float[16];
    private float[] modelViewMtx = new float[16];
    private float[] projectMtx = new float[16];

    private int borderTextureHandle;
    private int imageTextureHandle;
    private int contentTextureHandle;
    private float[] mvpMatrix = new float[16];
    private float[] borderEdge;
    private float[] imageEdge;
    private float[] contentEdge;
    private float[] vertexData;
    private float[] borderCoord;
    private float[] imageCoord;
    private float[] contentCoord;

    private float radius;
    private float width;
    private float height;
    private float[] center;
    private int[] viewPort;
    private Geometry.Sphere touchSphere;


    public BorderSprite() {
        Matrix.setIdentityM(modelMtx, 0);
    }

    /**
     * 传入STRIP规则顶点数据
     *
     * @param vertexData
     */
    public void putVertexData(float[] vertexData) {
        this.vertexData = vertexData;

        //vec =  rt - rb
        final float[] hVec = new float[]{vertexData[6] - vertexData[9], vertexData[7] - vertexData[10], vertexData[8] - vertexData[11]};
        this.height = (float) Math.sqrt(hVec[0] * hVec[0] + hVec[1] * hVec[1] + hVec[2] * hVec[2]);

        //vec = lt - rt
        final float[] wVec = new float[]{vertexData[0] - vertexData[6], vertexData[1] - vertexData[7], vertexData[2] - vertexData[8]};
        this.width = (float) Math.sqrt(wVec[0] * wVec[0] + wVec[1] * wVec[1] + wVec[2] * wVec[2]);

        //取对角线一半
        this.radius = (float) Math.sqrt(width * width + height * height) / 2;

        this.center = new float[]{(vertexData[0] + vertexData[3] + vertexData[6] + vertexData[9]) / 4, //x
                (vertexData[1] + vertexData[4] + vertexData[7] + vertexData[10]) / 4,  //y
                (vertexData[2] + vertexData[5] + vertexData[8] + vertexData[11]) / 4,  //z
        };
        this.touchSphere = new Geometry.Sphere(new Geometry.Point(center[0], center[1], center[2]), this.radius);
    }

    /**
     * 传入想要绘制的纹理、纹理映射数据，需保持纹理映射与顶点同序
     *
     * @param textureCoordData
     */

    public void setBorderTexture(int textureHandle, float[] textureCoordData, float edgeX, float edgeY) {
        this.borderTextureHandle = textureHandle;
        this.borderCoord = textureCoordData;
        this.borderEdge = new float[]{edgeX, edgeY};
    }

    public void setImageTexture(int textureHandle, float[] textureCoordData, float edgeX, float edgeY) {
        this.imageTextureHandle = textureHandle;
        this.imageCoord = textureCoordData;
        this.imageEdge = new float[]{edgeX, edgeY};
    }

    public void setContentTexture(int textureHandle, float[] textureCoordData, float edgeX, float edgeY) {
        this.contentTextureHandle = textureHandle;
        this.contentCoord = textureCoordData;
        this.contentEdge = new float[]{edgeX, edgeY};
    }


    /**
     * 将Spirit移动到(View变换后)世界坐标某一位置
     *
     * @param x
     * @param y
     * @param z
     */
    public void moveTo(float x, float y, float z) {
        Matrix.setIdentityM(modelMtx, 0);
        Matrix.translateM(modelMtx, 0, x, y, z);
    }


    public void translate(float x, float y, float z) {
        Matrix.translateM(modelMtx, 0, x, y, z);
    }

    public void scale(float x, float y, float z) {
        Matrix.scaleM(modelMtx, 0, x, y, z);
    }

    public void rotate(float a, float x, float y, float z) {
        Matrix.rotateM(modelMtx, 0, a, x, y, z);
    }


    /**
     * 绘制
     *
     * @param projectMtx
     * @param viewMtx
     * @param viewPort
     * @param program    Spirit重用管线
     * @param isMirror
     */
    public void draw(float[] projectMtx, float[] viewMtx, int[] viewPort, TextureShaderProgram program) {

        final FloatBuffer vertexBuffer = program.getVertexBuffer();
        final FloatBuffer borderTextureBuffer = program.getBorderTextureBuffer();
        final FloatBuffer imageTextureBuffer = program.getImageTextureBuffer();
        final FloatBuffer contentTextureBuffer = program.getContentTextureBuffer();

        this.projectMtx = projectMtx;
        this.viewPort = viewPort;

        program.setBorderTexture(borderTextureHandle);

        program.setImageTexture(imageTextureHandle);

        program.setContentTexture(contentTextureHandle);

        vertexBuffer.clear();
        vertexBuffer.put(vertexData);
        vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(program.getVertexPositionHandle(), COORDS_PER_VERTEX_3D, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glEnableVertexAttribArray(program.getVertexPositionHandle());


        if (borderCoord != null && borderCoord.length > 0) {
            borderTextureBuffer.clear();
            borderTextureBuffer.put(borderCoord);
            borderTextureBuffer.position(0);
            GLES20.glVertexAttribPointer(program.getBorderCoordHandle(), COORDS_PER_VERTEX_UV, GLES20.GL_FLOAT, false, 0, borderTextureBuffer);
            GLES20.glEnableVertexAttribArray(program.getBorderCoordHandle());
            GLES20.glUniform2fv(program.getBorderEdgeHandle(), 1, this.borderEdge, 0);
        }

        if (imageCoord != null && imageCoord.length > 0) {
            imageTextureBuffer.clear();
            imageTextureBuffer.put(imageCoord);
            imageTextureBuffer.position(0);
            GLES20.glVertexAttribPointer(program.getImageCoordHandle(), COORDS_PER_VERTEX_UV, GLES20.GL_FLOAT, false, 0, imageTextureBuffer);
            GLES20.glEnableVertexAttribArray(program.getImageCoordHandle());
            GLES20.glUniform2fv(program.getImageEdgeHandle(), 1, this.imageEdge, 0);
        }

        if (contentCoord != null && contentCoord.length > 0) {
            contentTextureBuffer.clear();
            contentTextureBuffer.put(contentCoord);
            contentTextureBuffer.position(0);
            GLES20.glVertexAttribPointer(program.getContentCoordHandle(), COORDS_PER_VERTEX_UV, GLES20.GL_FLOAT, false, 0, contentTextureBuffer);
            GLES20.glEnableVertexAttribArray(program.getContentCoordHandle());
            GLES20.glUniform2fv(program.getContentEdgeHandle(), 1, this.contentEdge, 0);
        }


        Matrix.multiplyMM(this.modelViewMtx, 0, viewMtx, 0, modelMtx, 0);

        Matrix.multiplyMM(mvpMatrix, 0, projectMtx, 0, modelViewMtx, 0);

        GLES20.glUniformMatrix4fv(program.getMVPMatrixHandle(), 1, false, mvpMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, VERTEX_CONT);
    }

    public float getRadius() {
        return radius;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float[] getCenter() {
        return center;
    }

    public float[] getProject() {
        return projectMtx;
    }

    public float[] getModelMtx() {
        return modelMtx;
    }

    public void setModelMtx(float[] modelMtx) {
        this.modelMtx = modelMtx;
    }

    public float[] getModelViewMtx() {
        return modelViewMtx;
    }

    public int[] getViewPort() {
        return viewPort;
    }

    public boolean isCollision(float x, float y) {
        return GlHelper.checkCollision(x, y, this);
    }

    public boolean isCollision(Geometry.Ray touchRay) {
        return Geometry.intersects(this.touchSphere, touchRay);
    }
}
