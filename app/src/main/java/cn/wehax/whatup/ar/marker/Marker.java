package cn.wehax.whatup.ar.marker;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.View;

import com.avos.avoscloud.AVFile;
import com.nostra13.universalimageloader.core.assist.FailReason;

import java.text.DecimalFormat;
import java.util.Map;

import cn.wehax.whatup.R;
import cn.wehax.whatup.ar.TextureDataManager;
import cn.wehax.whatup.ar.marker.base.ViewObject;
import cn.wehax.whatup.ar.programs.TextureShaderProgram;
import cn.wehax.whatup.ar.texture.DecodeStringTextureListener;
import cn.wehax.whatup.ar.texture.LoadingNetTextureListener;
import cn.wehax.whatup.ar.texture.TextureLoader;
import cn.wehax.whatup.config.Constant;
import cn.wehax.whatup.model.leancloud.LC;


/**
 * Created by mayuhan on 15/6/30.
 */
public class Marker implements ViewObject {

    private Avatar mAvatar;
    private Status mStatus;
    private final static float LEVEL_RANGE = 0.5f;
    private Map dataMap;
    private float altitude;
    private float angleY;
    private float coordinateDistance;
    private int mViewport[];
    private IMarkerOnClickListener onClickListener;

    public void bind(Activity activity, GLSurfaceView glSurfaceView) {
        mAvatar = new Avatar();
        mStatus = new Status();
        mAvatar.bind(activity, glSurfaceView);
        mStatus.bind(activity, glSurfaceView);

        if (dataMap != null) {

            final AVFile statusFile = (AVFile) dataMap.get("imageData");
            if(statusFile !=null){
                TextureLoader.load(glSurfaceView,
                        statusFile.getThumbnailUrl(false, 100, 100), new StatusThumbLoadingListener());
            }

            if (dataMap.containsKey(LC.method.GetNearbyStatus.keyTargetAvatar)) {
                final AVFile avatarFile = (AVFile) dataMap.get(LC.method.GetNearbyStatus.keyTargetAvatar);
                if(avatarFile !=null){
                    TextureLoader.load(glSurfaceView,
                            avatarFile.getThumbnailUrl(false, 100, 100), new AvatarLoadingNetTextureListener());
                }

            } else {
                setAvatarTexture(TextureLoader.load(activity, R.drawable.default_avatar)
                        , TextureDataManager.getAvatarData());
            }

            final Integer gender = (Integer) dataMap.get(LC.method.GetNearbyStatus.keyTargetSex);

            if (gender!=null&&gender == Constant.SEX_FEMALE) {
                mAvatar.setBorderTexture(TextureLoader.load(activity, R.drawable.avatar_border_female),
                        TextureDataManager.getAvatarBorderData(), 0f, 0f);
            } else {
                mAvatar.setBorderTexture(TextureLoader.load(activity, R.drawable.avatar_border_male),
                        TextureDataManager.getAvatarBorderData(), 0f, 0f);
            }


            float distance = Float.parseFloat(dataMap.get("distance").toString());

            String distanceText;

            DecimalFormat df = new DecimalFormat("0.0");

            distance = Float.parseFloat(df.format(distance));

            if (distance < 0.1f) {
                distanceText = "<100m";
            } else if (distance >= 1.0f) {
                distanceText = distance + "km";
            } else {
                distanceText = String.valueOf(distance * 1000);
                distanceText = distanceText.substring(0, distanceText.indexOf("."));
                distanceText += "m";
            }

            setDistanceText(activity, glSurfaceView, distanceText);

            String statusText = (String) dataMap.get("text");

            if (statusText != null && statusText.length() > 0) {
                setContentText(activity, glSurfaceView, statusText);
            }

        }

    }

    public void setDistanceText(Activity activity, GLSurfaceView glSurfaceView, String content) {
        TextureLoader.decodeStringToTexture(activity, glSurfaceView, content, "#FFFFFF", new DistanceDecodeListener());
    }

    public void setContentText(Activity activity, GLSurfaceView glSurfaceView, String content) {
        TextureLoader.decodeStringToTexture(activity, glSurfaceView, content, "#FFFFFF", new ContentDecodeListener());
    }

    public void moveTo(float x, float y, float z) {
        mAvatar.moveTo(x, y, z);
        mStatus.moveTo(x, y + 1.7f, z);
    }

    @Override
    public void translate(float x, float y, float z) {
        mAvatar.translate(x, y, z);
        mStatus.translate(x, y, z);
    }

    @Override
    public void rotate(float a, float x, float y, float z) {
        mAvatar.rotate(a, x, y, z);
        mStatus.rotate(a, x, y, z);
    }

    @Override
    public void draw(float[] projectMtx, float[] viewMtx, int[] viewPort, TextureShaderProgram program) {

        this.mViewport = viewPort;

        this.moveTo(0, 0, 0);
        this.rotate(angleY + 180, 0f, 1f, 0f);
        this.translate(0, altitude, -coordinateDistance);

        mAvatar.draw(projectMtx, viewMtx, viewPort, program);
        mStatus.draw(projectMtx, viewMtx, viewPort, program);

    }

    public void setAvatarTexture(int avatarHandle, float[] textureCoord) {
        mAvatar.setImageTexture(avatarHandle, textureCoord, 0f, 0f);
    }

    public void setStatusTexture(int statusHandle, float[] textureCoord) {
        mStatus.setImageTexture(statusHandle, textureCoord, 0f, 0f);
    }

    public void setData(Map dataMap) {
        this.dataMap = dataMap;
    }


    public void setAltitude(float altitude) {
        this.altitude = altitude;
    }

    public void setAngleY(float angle) {
        this.angleY = angle;
    }

    public void setCoordinateDistance(float coordinateDistance) {
        this.coordinateDistance = coordinateDistance;
    }

    private class AvatarLoadingNetTextureListener extends LoadingNetTextureListener {

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

        }

        @Override
        public void onLoadingComplete(String imageUri, View view, int textureHandle) {
            Marker.this.setAvatarTexture(textureHandle, TextureDataManager.getAvatarData());
        }
    }

    private class StatusThumbLoadingListener extends LoadingNetTextureListener {

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

        }

        @Override
        public void onLoadingComplete(String imageUri, View view, int textureHandle) {
            Marker.this.setStatusTexture(textureHandle, TextureDataManager.getStatusThumbData());
        }
    }

    private class DistanceDecodeListener implements DecodeStringTextureListener {

        @Override
        public void onDone(int textureHandle, float width, float height) {
            mAvatar.setContentTexture(textureHandle
                    , TextureDataManager.getDistanceData(width, height), 0f, 0f);
        }
    }

    private class ContentDecodeListener implements DecodeStringTextureListener {
        @Override
        public void onDone(int textureHandle, float width, float height) {
            int line = (int) Math.ceil(Math.random() * 2) - 1;
            mStatus.setContentTexture(textureHandle
                    , TextureDataManager.getContentData(width, height, line), 0f, 0f);
        }
    }

    public void setOnClickListener(IMarkerOnClickListener listener) {
        this.onClickListener = listener;
    }

    public boolean onHandleTouchEvent(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (onClickListener != null) {
                    final float x = event.getX();
                    final float y = v.getHeight() - event.getY();  //opengl的Y轴与屏幕Y轴是反的
                    if (mAvatar.isCollision(x, y)) {
                        onClickListener.avatarOnClick(dataMap);
                        return true;
                    } else if (mStatus.isCollision(x, y)) {
                        onClickListener.statusOnClick(dataMap);
                        return true;
                    } else {
                        return false;
                    }
                }
            default:
                return false;

        }
    }


    public Avatar getAvatar() {
        return mAvatar;
    }

    public Status getStatus() {
        return mStatus;
    }


}
