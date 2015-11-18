package cn.wehax.whatup.ar.vision;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Surface;

import com.google.vrtoolkit.cardboard.ScreenParams;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;

import cn.wehax.whatup.R;
import cn.wehax.whatup.ar.ShaderManager;
import cn.wehax.whatup.ar.utils.GlHelper;
import cn.wehax.whatup.support.helper.FileHelper;

/**
 * Created by mayuhan on 15/6/11.
 */
public class FovBackground {

    private static final int FLOAT_SIZE_BYTES = 4;
    private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
    private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
    private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;
    private static final float[] mVerticesData = {
            // X, Y, Z, U, V
            -1f, -1f, -40f, 0.f, 0.f,   //lb
            1f, -1f, -40f, 1.f, 0.f,    //rb
            -1f, 1f, -40f, 0.f, 1.f,    //lt
            1f, 1f, -40f, 1.f, 1.f,     //rt
    };

    private int currentFacing = Camera.CameraInfo.CAMERA_FACING_BACK;

    private FloatBuffer mTriangleVertices;


    private float[] mMVPMatrix = new float[16];
    private float[] mSTMatrix = new float[16];

    private int mProgram;
    private int mTextureID;
    private int muMVPMatrixHandle;
    private int muSTMatrixHandle;
    private int maPositionHandle;
    private int maTextureHandle;
    private int msTextureHandle;

    private float scaleX;
    private float scaleY;

    private SurfaceTexture mSurface;

    private static int GL_TEXTURE_EXTERNAL_OES = 0x8D65;

    private Camera fovCamera;
    private int width;
    private int height;
    private float[] shaderData;
    private ARView arView;


    /**
     * 构造场景中的模型,在onSurfaceCreated中调用
     *
     * @param arView
     */
    public void attach(ARView arView) {
        this.arView = arView;

        final int vertexShaderHandle = ShaderManager.loadGLShader(arView.getContext(),
                GLES20.GL_VERTEX_SHADER,
                R.raw.fov_background_vertex);

        final int fragmentShaderHandle = ShaderManager.loadGLShader(arView.getContext(),
                GLES20.GL_FRAGMENT_SHADER,
                R.raw.fov_background_fragment);

        mProgram = GlHelper.createProgram(vertexShaderHandle, fragmentShaderHandle);
        if (mProgram == 0) {
            return;
        }

        GLES20.glUseProgram(mProgram);

        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        GlHelper.checkGlError("glGetAttribLocation aPosition");
        if (maPositionHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aPosition");
        }
        maTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
        GlHelper.checkGlError("glGetAttribLocation aTextureCoord");
        if (maTextureHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aTextureCoord");
        }

        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GlHelper.checkGlError("glGetUniformLocation uMVPMatrix");
        if (muMVPMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uMVPMatrix");
        }

        muSTMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uSTMatrix");
        GlHelper.checkGlError("glGetUniformLocation uSTMatrix");
        if (muSTMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uSTMatrix");
        }

        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);

        mTextureID = textures[0];
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID);
        GlHelper.checkGlError("glBindTexture mTextureID");

        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);

        msTextureHandle = GLES20.glGetUniformLocation(mProgram, "sTexture");
        GlHelper.checkGlError("glGetUniformLocation sTexture");
        if (msTextureHandle == -1) {
            throw new RuntimeException("Could not get attrib location for sTexture");
        }

        mSurface = new SurfaceTexture(mTextureID);

    }

    public void updateSize(ScreenParams screenParams) {

        this.width = this.arView.getWidth();
        this.height = this.arView.getHeight();

        final float widthMeters = screenParams.getWidthMeters() / screenParams.getWidth() * (float) width;
        final float heightMeters = screenParams.getHeightMeters() / screenParams.getHeight() * (float) height;

        scaleX = widthMeters / 0.1f * 19.5f;
        scaleY = heightMeters / 0.1f * 19.5f;


        if (fovCamera != null) {
            setupShaderData();
            fovCamera.stopPreview();
            fovCamera.startPreview();
        }

    }

    public void setOnFrameAvailableListener(SurfaceTexture.OnFrameAvailableListener listener) {
        mSurface.setOnFrameAvailableListener(listener);
    }

    /**
     * @param facing {@link android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT} or
     *               {@link android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK}
     */
    public void setFovCameraFacing(int facing) {
        currentFacing = facing;
        stop();
        start();
    }

    public int getFovCameraFacing() {
        return currentFacing;
    }

    public void start() {
        try {
            if (fovCamera == null) {
                fovCamera = Camera.open(currentFacing);
                fovCamera.setDisplayOrientation(90);
            }
            setPreviewParameters();
            setupShaderData();
            fovCamera.setPreviewTexture(mSurface);
            // 开始预览
            fovCamera.startPreview();
        } catch (Exception ioe) {
            ioe.printStackTrace();
            Log.w("MainActivity", "CAM LAUNCH FAILED");
        }
    }

    public void stop() {
        if (fovCamera != null) {
            fovCamera.stopPreview();
            fovCamera.release();
            fovCamera = null;
        }
    }

    private void setPreviewParameters() {
        if (fovCamera != null) {
            Camera.Parameters camParam = fovCamera.getParameters();
            if (camParam.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                camParam.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }

            final Camera.Size size = getCameraMaxPreviewSize();
            camParam.setPreviewSize(size.width, size.height);
            fovCamera.setParameters(camParam);

            if (width * height == 0) {
                return;
            }

            final float camWidth, camHeight;
            if (size.width > size.height) {
                camWidth = size.height;
                camHeight = size.width;
            } else {
                camWidth = size.width;
                camHeight = size.height;
            }

//
            final float trimOffset;
            final float texAspect = camWidth / camHeight;
            final float viewAspect = (float) width / (float) height;
            shaderData = mVerticesData.clone();
            if (texAspect > viewAspect) {
                trimOffset = Math.abs(height * texAspect - width) / 2f / width;

                //lb
                shaderData[3] += trimOffset;
                //lt
                shaderData[13] = shaderData[3];
                //rb
                shaderData[8] -= trimOffset;
                //rt
                shaderData[18] = shaderData[8];
            } else {
                trimOffset = Math.abs(width / texAspect - height) / 2f / height;
                //lb
                shaderData[4] += trimOffset;

                //rb
                shaderData[9] = shaderData[4];

                //lt
                shaderData[14] -= trimOffset;

                //rt
                shaderData[19] = shaderData[14];
            }
        }

    }

    private Camera.Size getCameraMaxPreviewSize() {
        if (fovCamera != null) {
            final Camera.Parameters camParam = fovCamera.getParameters();
            List<Camera.Size> supportSizes = camParam.getSupportedPreviewSizes();
            Camera.Size size = supportSizes.get(GlHelper.maxPreviewSizeIndex(camParam));
            return size;
        }
        return null;
    }

    private void setupShaderData() {
        if (mTriangleVertices != null) {
            mTriangleVertices.clear();
        } else {
            mTriangleVertices = ByteBuffer.allocateDirect(
                    shaderData.length * FLOAT_SIZE_BYTES)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
        }
        mTriangleVertices.put(shaderData).position(0);
        Matrix.setIdentityM(mSTMatrix, 0);

    }


    public void draw(float[] perspective) {

        synchronized (this) {
            mSurface.updateTexImage();
            mSurface.getTransformMatrix(mSTMatrix);
        }

        if (mTriangleVertices == null) {
            return;
        }

        GLES20.glUseProgram(mProgram);
        GlHelper.checkGlError("glUseProgram");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID);
        GLES20.glUniform1i(msTextureHandle, 0);

        mTriangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,
                TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
        GlHelper.checkGlError("glVertexAttribPointer maPosition");

        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GlHelper.checkGlError("glEnableVertexAttribArray maPositionHandle");

        mTriangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
        GLES20.glVertexAttribPointer(maTextureHandle, 3, GLES20.GL_FLOAT, false,
                TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
        GlHelper.checkGlError("glVertexAttribPointer maTextureHandle");

        GLES20.glEnableVertexAttribArray(maTextureHandle);
        GlHelper.checkGlError("glEnableVertexAttribArray maTextureHandle");

        Matrix.setIdentityM(mMVPMatrix, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, perspective, 0, mMVPMatrix, 0);
        Matrix.scaleM(mMVPMatrix, 0, mMVPMatrix, 0, scaleX, scaleY, 1f);
        GLES20.glUniformMatrix4fv(muSTMatrixHandle, 1, false, mSTMatrix, 0);
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);


        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GlHelper.checkGlError("glDrawArrays");

    }


    /**
     * 拍照
     *
     * @param activity
     * @param callback
     */
    public void takePicture(final Activity activity, final PictureCallback callback) {

        fovCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                takePhoto(activity, camera, callback);
            }
        });


    }

    private void takePhoto(final Activity activity, Camera camera, final PictureCallback callback) {

        camera.takePicture(
                new Camera.ShutterCallback() {
                    public void onShutter() {
                        // 按下快门瞬间会执行此处代码
                    }
                },
                new Camera.PictureCallback() {
                    public void onPictureTaken(byte[] data, Camera c) {
                        // 此处代码可以决定是否需要保存原始照片信息
                    }
                },
                new PictureCallbackImpl(activity, callback));

    }

    private Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        android.graphics.Matrix mtx = new android.graphics.Matrix();
        mtx.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    private int getRotationAngle(Activity activity, int cameraId) {
        Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }


    class PictureCallbackImpl implements Camera.PictureCallback {
        private final Activity activity;
        private final PictureCallback callback;

        public PictureCallbackImpl(Activity activity, PictureCallback callback) {
            this.activity = activity;
            this.callback = callback;
        }

        @Override
        public void onPictureTaken(final byte[] data, final Camera camera) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int angleToRotate = getRotationAngle(activity, currentFacing);
                        if (currentFacing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                            angleToRotate = angleToRotate + 180;
                        }
                        Bitmap realImage = BitmapFactory.decodeByteArray(data, 0, data.length);
                        Bitmap bitmap = rotate(realImage, angleToRotate);
                        final String absPath = FileHelper.savePictureToTempFile(activity, bitmap);

                        if (!realImage.isRecycled())
                            realImage.recycle();
                        if (!bitmap.isRecycled())
                            bitmap.recycle();

                        if (callback != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onPictureTaken(absPath);
                                }
                            });
                        }

                        camera.cancelAutoFocus();
                        camera.stopPreview();
                        camera.startPreview();
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }
    }

}