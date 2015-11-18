package cn.wehax.whatup.model.location;

import android.app.Application;
import android.util.Log;

import com.avos.avoscloud.AVGeoPoint;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.inject.Provider;

import cn.wehax.whatup.framework.model.WXException;
import cn.wehax.whatup.support.helper.LogHelper;
import cn.wehax.whatup.support.util.PreferencesUtils;

/**
 * 定位管理器
 * </p>
 * 注：使用SharePreference缓存定位结果
 */
@Singleton
public class LocationManager {
    public static final String TAG = "LocationManager";
    private Application application;

    private OnLocationListener onLocationListener;

    @Inject
    LocationManager(Provider<Application> application) {
        this.application = application.get();

        init();
    }

    private LocationClient locationClient;
    private MyLocationListener myLocationListener;
    private boolean isLocating = false;

    /**
     * 初始化Manager
     */
    private void init() {
        locationClient = new LocationClient(application.getApplicationContext());
        myLocationListener = new MyLocationListener();
        locationClient.registerLocationListener(myLocationListener);
        setLocOption();
    }

    /**
     * 设置定位参数
     */
    private void setLocOption() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置定位模式
        option.setCoorType("gcj02");// 国测局经纬度坐标
        option.setScanSpan(999);// 手动请求定位
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        locationClient.setLocOption(option);
    }

    /**
     * 监听定位请求返回
     */
    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (onLocationListener == null)
                return;
            /*
            61 ： GPS定位结果
            62 ： 扫描整合定位依据失败。此时定位结果无效。
            63 ： 网络异常，没有成功向服务器发起请求。此时定位结果无效。
            65 ： 定位缓存的结果。
            66 ： 离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果
            67 ： 离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果
            68 ： 网络连接失败时，查找本地离线定位时对应的返回结果
            161： 表示网络定位结果
            162~167： 服务端定位失败
            502：key参数错误
            505：key不存在或者非法
            601：key服务被开发者自己禁用
            602：key mcode不匹配
            501～700：key验证失败
            */
            if (location.getLocType() == 161) {
                LocationResult result = new LocationResult(
                        new AVGeoPoint(location.getLatitude(), location.getLongitude()),
                        location.getCity());
                cacheLocation(result);
                Log.e(TAG, "requestLocation-success");
                Log.e(TAG, "result=" + result);
                onLocationListener.onSuccess(result, true);
            } else {
                WXException e = new WXException(location.getLocType(), "");
                LogHelper.e(TAG, "requestLocation-success", e);
                onLocationListener.onFail(e);
            }

            onLocationListener = null;
            isLocating = false;
            stop();
        }
    }


    public static final int STRATEGY_NETWORK = 1;
    public static final int STRATEGY_CACHE_ELSE_NETWORK = 2;

    /**
     * 请求定位
     *
     * @param strategy
     * @param lis
     */
    public void requestLocation(int strategy, final OnLocationListener lis) {
        if (lis == null || locationClient == null) {
            return;
        }

        if (strategy == STRATEGY_NETWORK) {
            requestLocationFromNetwork(lis);
        } else if (strategy == STRATEGY_CACHE_ELSE_NETWORK) {
            requestLocationFromCacheElseNetwork(lis);
        }
    }


    /**
     * 请求网络定位
     *
     * @param lis
     */
    private void requestLocationFromNetwork(final OnLocationListener lis) {
        if (isLocating) {
            WXException e = new WXException(ERROR_IS_LOCATION, "");
            onLocationListener.onFail(e);
            return;
        }

        start();

        onLocationListener = lis;
        isLocating = true;
        locationClient.requestLocation();
    }

    /**
     * 请求定位，缓存优先
     *
     * @param lis
     */
    private void requestLocationFromCacheElseNetwork(final OnLocationListener lis) {
        if (hasCached()) {
            lis.onSuccess(getCachedLocation(), false);
        } else {
            requestLocationFromNetwork(lis);
        }
    }

    private final String PREFERENCE_NAME_LOCATION_CACHE = "PREFERENCE_NAME_LOCATION_CACHE";
    private final String KEY_LATITUDE = "KEY_LATITUDE";
    private final String KEY_LONGITUDE = "KEY_LONGITUDE";
    private final String KEY_CITY = "KEY_CITY";
    private final String KEY_TIME = "KEY_TIME"; // 缓存时间

    /**
     * 获取缓存的上一次的定位结果
     *
     * @return
     */
    private LocationResult getCachedLocation() {
        PreferencesUtils.setPreferenceName(PREFERENCE_NAME_LOCATION_CACHE);
        AVGeoPoint point = new AVGeoPoint(
                PreferencesUtils.getFloat(application, KEY_LATITUDE, 0),
                PreferencesUtils.getFloat(application, KEY_LONGITUDE, 0));

        return new LocationResult(point, PreferencesUtils.getString(application, KEY_CITY, ""));
    }

    /**
     * 如果缓存了上次的定位结果，返回true
     *
     * @return
     */
    private boolean hasCached() {
        PreferencesUtils.setPreferenceName(PREFERENCE_NAME_LOCATION_CACHE);
        // TODO 缓存是否考虑过期时间？
        if (PreferencesUtils.contains(application, KEY_LATITUDE)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 缓存定位结果
     *
     * @param result
     */
    private void cacheLocation(LocationResult result) {
        PreferencesUtils.setPreferenceName(PREFERENCE_NAME_LOCATION_CACHE);

        PreferencesUtils.putFloat(application, KEY_LATITUDE, (float) result.getLocation().getLatitude());
        PreferencesUtils.putFloat(application, KEY_LONGITUDE, (float) result.getLocation().getLongitude());
        PreferencesUtils.putString(application, KEY_CITY, result.getCity());
        PreferencesUtils.putLong(application, KEY_TIME, System.currentTimeMillis());
    }


    /**
     * 开启定位服务
     */
    public void start() {
        if (locationClient != null && !locationClient.isStarted()) {
            locationClient.start();
        }
    }

    /**
     * 关闭定位服务
     */
    public void stop() {
        if (locationClient != null && locationClient.isStarted()) {
            locationClient.stop();
        }
    }

    public static interface OnLocationListener {
        void onSuccess(LocationResult result, Boolean fromNetwork);

        void onFail(WXException e);
    }

    /**
     * 定位结果
     */
    public static class LocationResult {
        AVGeoPoint location;
        String city;

        public LocationResult() {
        }

        public LocationResult(AVGeoPoint location, String city) {
            this.location = location;
            this.city = city;
        }

        public AVGeoPoint getLocation() {
            return location;
        }

        public void setLocation(AVGeoPoint location) {
            this.location = location;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        @Override
        public String toString() {
            return "LocationResult{" +
                    "location=" + location +
                    ", city='" + city + '\'' +
                    '}';
        }
    }

    /**
     * 正在查询中
     */
    public static final int ERROR_IS_LOCATION = 29001;

//    public static final HashMap<Integer, String> errorMap = new HashMap<>();
//    static{
//        errorMap.put(ERROR_IS_LOCATION, "正在查询中");
//    }

}
