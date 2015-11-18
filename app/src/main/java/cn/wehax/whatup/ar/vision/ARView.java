package cn.wehax.whatup.ar.vision;

import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.util.Log;

import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;

import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;

import cn.wehax.whatup.ar.programs.TextureShaderProgram;

/**
 * Created by mayuhan on 15/7/31.
 */
public class ARView extends CardboardView implements CardboardView.StereoRenderer, SurfaceTexture.OnFrameAvailableListener {
    private static String TAG = "ARView";

    private int[] viewPort;
    private float[] perspective;
    private float[] eyeView;
    private float[] viewMtx;
    private float[] cameraMtx;
    private float[] viewProjectMatrix;
    private float[] invertedViewProject;

    private TextureShaderProgram mProgram;

    public enum Status {
        RUNNING,
        TO_PAUSE,
        TO_RESUME,
        STOP
    }

    private ARConfig config;

    private Status status = Status.STOP;

    private FovBackground fovBg;

    private StereoRenderer renderer;


    public ARView(Context context) {
        super(context);
        init();
    }

    public ARView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setConfig(ARConfig config) {
        this.config = config;
    }

    private void init() {

        cameraMtx = new float[16];
        viewMtx = new float[16];
        viewProjectMatrix = new float[16];
        invertedViewProject = new float[16];

        this.fovBg = new FovBackground();
        this.setVRModeEnabled(false);


    }

    @Override
    public void onPause() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                stop();
            }
        });
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.status = Status.TO_RESUME;
    }

    public void takePicture(Activity activity, PictureCallback callback) {
        fovBg.takePicture(activity, callback);
    }

    public void start() {
        fovBg.start();
        status = Status.RUNNING;
    }

    public void stop() {

        fovBg.stop();
        status = Status.STOP;
    }

    /**
     * @param cameraFacing {@link android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT } or
     *                     {@link  android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK }
     */
    public void setCameraFacing(int cameraFacing) {
        fovBg.setFovCameraFacing(cameraFacing);

    }

    public int getCameraFacing() {
        return fovBg.getFovCameraFacing();
    }

    @Override
    public void onNewFrame(HeadTransform var1) {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        if (fovBg.getFovCameraFacing() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            Matrix.setLookAtM(cameraMtx, 0, 0.0f, 0.0f, -config.cameraZ, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
        } else {
            Matrix.setLookAtM(cameraMtx, 0, 0.0f, 0.0f, config.cameraZ, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
        }

        if (renderer != null) {
            renderer.onNewFrame(var1);
        }

    }

    @Override
    public void onDrawEye(Eye var1) {
        if (config == null) {
            throw new RuntimeException("ARConfig is null");
        }
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        fovBg.draw(var1.getPerspective(config.nearZ, config.farZ));

        if (viewPort == null) {
            viewPort = new int[4];
            var1.getViewport().getAsArray(viewPort, 0);
        }

        perspective = var1.getPerspective(config.nearZ, config.farZ);

        eyeView = var1.getEyeView();

        Matrix.multiplyMM(viewMtx, 0, eyeView, 0, cameraMtx, 0);

        Matrix.multiplyMM(viewProjectMatrix, 0, perspective, 0, viewMtx, 0);

        Matrix.invertM(invertedViewProject, 0, viewProjectMatrix, 0);

        mProgram.useProgram();


        if (renderer != null) {
            renderer.onDrawEye(var1);
            if (renderer instanceof ARRender) {
                ((ARRender) renderer).onDrawEye(perspective, viewMtx, viewPort, mProgram);
            }
        }
    }

    @Override
    public void onFinishFrame(Viewport var1) {
        if (renderer != null) {
            renderer.onFinishFrame(var1);
        }
    }

    @Override
    public void onSurfaceChanged(int var1, int var2) {
        fovBg.updateSize(this.getScreenParams());

        updateFovStatus();

        if (renderer != null) {
            renderer.onSurfaceChanged(var1, var2);
        }
    }

    private void updateFovStatus() {

        Runnable runnableToUpdateFov = new Runnable() {
            @Override
            public void run() {
                fovBg.updateSize(getScreenParams());
                switch (status) {
                    case TO_PAUSE:
                        Log.e(TAG, "updateFovStatus TO_PAUSE");
                        fovBg.stop();
                        ARView.this.status = Status.STOP;
                        break;
                    case TO_RESUME:
                        Log.e(TAG, "updateFovStatus TO_RESUME");
                        fovBg.start();
                        ARView.this.status = Status.RUNNING;
                        break;
                    case RUNNING:
                        fovBg.stop();
                        fovBg.start();
                        break;
                    default:
                        break;
                }
            }
        };

        queueEvent(runnableToUpdateFov);


    }

    @Override
    public void onSurfaceCreated(EGLConfig var1) {

        GLES20.glClearColor(0f, 0f, 0f, 0f);

        //混色使PNG的透明生效
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glDepthMask(true);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_ALWAYS);

        mProgram = new TextureShaderProgram(getContext());
        fovBg.attach(ARView.this);
        fovBg.updateSize(getScreenParams());
        fovBg.setOnFrameAvailableListener(ARView.this);
        fovBg.start();
        ARView.this.status = Status.RUNNING;


        if (renderer != null) {
            renderer.onSurfaceCreated(var1);
        }
    }

    @Override
    public void onRendererShutdown() {
        if (renderer != null) {
            renderer.onRendererShutdown();
        }
    }

    @Override
    public void setRenderer(StereoRenderer renderer) {
        if (renderer != null) {
            super.setRenderer(this);
            this.renderer = renderer;
        }
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        if (this.renderer != null) {
            this.requestRender();
        }

    }

    public void refreshMarker(final Activity activity, final List dataList) {
        if (!this.isShown()) {
            return;
        }
        if (!(renderer instanceof ARRender)) {
            return;
        }

        if (status != Status.RUNNING) {
            return;
        }

        this.queueEvent(new Runnable() {
            @Override
            public void run() {
                ((ARRender) renderer).onRefreshData(activity, dataList);
            }
        });
    }


    public interface ARRender extends StereoRenderer {

        void onDrawEye(float[] project, float[] view, int[] viewPort, TextureShaderProgram program);

        void onRefreshData(Activity activity, List dataList);
    }


    public int[] getViewPort() {
        return viewPort;
    }

    public float[] getPerspective() {
        return perspective;
    }

    public float[] getEyeView() {
        return eyeView;
    }

    public float[] getViewMtx() {
        return viewMtx;
    }

    public float[] getCameraMtx() {
        return cameraMtx;
    }

    public float[] getViewProjectMatrix() {
        return viewProjectMatrix;
    }

    public float[] getInvertedViewProject() {
        return invertedViewProject;
    }
}
