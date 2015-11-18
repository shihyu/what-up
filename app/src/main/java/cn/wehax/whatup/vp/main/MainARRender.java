package cn.wehax.whatup.vp.main;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;

import com.avos.avoscloud.AVGeoPoint;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;

import cn.wehax.whatup.ar.Config;
import cn.wehax.whatup.ar.marker.IMarkerOnClickListener;
import cn.wehax.whatup.ar.marker.Marker;
import cn.wehax.whatup.ar.programs.TextureShaderProgram;
import cn.wehax.whatup.ar.vision.ARView;
import cn.wehax.whatup.vp.main.impl.ARMainActivity;

/**
 * Created by mayuhan on 15/8/3.
 */
public class MainARRender implements ARView.ARRender, IMarkerOnClickListener, View.OnTouchListener {
    private List<Marker> markers = new ArrayList<>();

    private ARView arView;
    private MainPresenter mPresenter;

    public MainARRender(ARView arView
            , MainPresenter presenter) {
        this.arView = arView;
        arView.setOnTouchListener(this);
        this.mPresenter = presenter;
    }

    @Override
    public void onDrawEye(float[] project, float[] view, int[] viewPort, TextureShaderProgram program) {

        for (Marker marker : markers) {
            marker.draw(project, view, viewPort, program);
        }
    }

    @Override
    public void onRefreshData(Activity activity, List dataList) {
        ARMainActivity arMainActivity = (ARMainActivity) activity;
        refreshMarkerData(arMainActivity, arMainActivity, (ArrayList<Map>) dataList);
    }

    @Override
    public void onNewFrame(HeadTransform var1) {

    }

    @Override
    public void onDrawEye(Eye var1) {

    }

    @Override
    public void onFinishFrame(Viewport var1) {

    }

    @Override
    public void onSurfaceChanged(int var1, int var2) {

    }

    @Override
    public void onSurfaceCreated(EGLConfig var1) {
        mPresenter.refreshStatus();
    }

    @Override
    public void onRendererShutdown() {

    }

    private void refreshMarkerData(Activity activity, IMainView view, ArrayList<Map> dataList) {
        if (dataList.size() > 0) {
//            final AVGeoPoint nearPoint = (AVGeoPoint) dataList.get(0).get("location");
//            final double near = mPresenter.distanceToMe(nearPoint);
//            final AVGeoPoint farPoint = (AVGeoPoint) dataList.get(dataList.size() - 1).get("location");
//            final double far = mPresenter.distanceToMe(farPoint);
//            final double range = far - near;

            double near = Config.MARKERS_DISTRIBUTE_DISTANCE;
            double far = 0;
            double range = 0d;
            double distance = 0;
            for (Map markerData : dataList) {
                distance = Double.parseDouble(String.valueOf(markerData.get("distance")));
                if (distance < near) {
                    near = distance;
                }

                if (distance > far) {
                    far = distance;
                }
            }

            range = far - near;

            markers.clear();
            for (int i = 0; i < dataList.size(); i++) {
                final Map markerData = dataList.get(i);
                final Marker marker = new Marker();
                final AVGeoPoint point = (AVGeoPoint) markerData.get("location");
                final double distanceToMe = mPresenter.distanceToMe(point);
                if (range == 0) {
                    marker.setAltitude(0);
                    marker.setCoordinateDistance(Config.MARKERS_DISTRIBUTE_DISTANCE);
                } else {
                    marker.setCoordinateDistance((float) (Config.MARKERS_DISTRIBUTE_DISTANCE +
                            (distanceToMe - near) / range * Config.MARKERS_LEVEL_RANGE));
                    marker.setAltitude((float) (Config.NEAR_Y + ((distanceToMe - near) / range * (Config.FAR_Y - Config.NEAR_Y))));
                }
                marker.setAngleY((float) (Math.random() * 360f - 180f));
                marker.setData(markerData);
                marker.setOnClickListener(this);
                marker.bind(activity, arView);
                markers.add(marker);
            }
        }
    }

    @Override
    public void avatarOnClick(Map dataMap) {

        clickMarker(dataMap);
    }

    @Override
    public void statusOnClick(Map dataMap) {

        clickMarker(dataMap);
    }

    private void clickMarker(Map statusDataMap) {
        String statusId = (String) statusDataMap.get("id");
        String targetId = (String) statusDataMap.get("source.id");
        mPresenter.goToChatView(targetId, statusId);
    }


    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        arView.queueEvent(new Runnable() {
            @Override
            public void run() {
                for (Marker marker : markers) {
                    if (marker.onHandleTouchEvent(v, event)) {
                        break;
                    }
                }
            }
        });
        return false;
    }
}
