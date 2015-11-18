package cn.wehax.whatup.model.status;

import android.app.Application;
import android.util.Log;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FunctionCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.j256.ormlite.dao.Dao;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Provider;

import cn.wehax.whatup.framework.model.OnRequestDataListListener;
import cn.wehax.whatup.framework.model.OnRequestDataListener;
import cn.wehax.whatup.framework.model.WXException;
import cn.wehax.whatup.model.chat.QueryCallback;
import cn.wehax.whatup.model.leancloud.LC;
import cn.wehax.whatup.support.db.DatabaseManager;
import cn.wehax.whatup.support.helper.LogHelper;

/**
 * 用户状态管理器
 */
@Singleton
public class StatusManager {
    public static final String TAG = "StatusManager";
    private Application application;

    @javax.inject.Inject
    DatabaseManager databaseManager;


    @Inject
    StatusManager(Provider<Application> application) {
        this.application = application.get();
    }

    /**
     * 获取指定用户的状态列表
     *
     * @param userId
     * @param beforeTime
     * @param pageSize
     * @param lis
     */
    public void getUserStatusList(String userId, Long beforeTime, int pageSize, final OnRequestDataListListener<Map> lis) {
        Map<String, Object> params = new HashMap<>();
        params.put(LC.method.paramTargetId, userId);
        params.put(LC.method.paramLimit, pageSize);
        params.put(LC.method.paramBeforeTime, beforeTime);
        Log.e("dss", "sendStatus=" + params.toString());

        AVCloud.callFunctionInBackground(LC.method.GetUserStatusList.functionName, params, new FunctionCallback<List<Map>>() {
            @Override
            public void done(List<Map> data, AVException e) {
                try {
                    if (e != null) {
                        throw new WXException(e);
                    }

                    if (data == null) {
                        throw new WXException("o == null");
                    }

                    Log.e(TAG, "getUserStatusList-success");
                    Log.e(TAG, "查询到" + data.size() + " 条符合条件的数据");
                    lis.onDataListReturn(data, 0, 0);
                } catch (WXException wxe) {
                    LogHelper.e(TAG, "getUserStatusList-fail", e);
                    lis.onError(wxe);
                }
            }
        });
    }

    public void getNearByStatus(AVGeoPoint point, String city, FunctionCallback<ArrayList<Map>> callback) {
        JSONObject jsoLocation = new JSONObject();
        try {
            jsoLocation.put("latitude", point.getLatitude());
            jsoLocation.put("longitude", point.getLongitude());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Map<String, Object> params = new HashMap<>();
        params.put("location", jsoLocation);
        params.put("city", city);
        AVCloud.callFunctionInBackground(LC.method.GetNearbyStatus.functionName, params, callback);
    }

    public void queryStatusById(String statusId, QueryCallback callback) {
        //先从本地数据库查询，没有再从远程获取
        try {
            Dao<Status, Object> dao = databaseManager.getDaoByClass(Status.class).getRawDao();
            if (dao.idExists(statusId)) {
                Status status = dao.queryForId(statusId);
                callback.done(status,true,null);
                Log.e("chat","从本地获取Status成功");
                return;
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.e("chat","从本地获取Status出错"+e.toString());
        }

        queryStatusFromRemote(statusId, callback);
    }

    /**
     * 查询AllStatus表
     * @param statusId
     * @param callback
     */
    private void queryStatusFromRemote(final String statusId, final QueryCallback<Status> callback){
        String[] includes = {"imageData"};
        queryAVObject(statusId, LC.table.AllStatus.tableName, includes, new QueryCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, boolean isLocal, Exception e) {
                if (e != null) {
                    callback.done(null, false, e);
                    Log.e("chat", "从AllStatus获取Status出错" + e.toString());

                } else {
                    Log.e("chat", "从AllStatus获取成功");

                    //保存数据库
                    Status status = saveStatusToDB(avObject);

                    callback.done(status, false, null);

                }
            }
        });

    }


    public Status saveStatusToDB(AVObject avObject) {
        AVFile file = avObject.getAVFile("imageData");
        // statusId, text, location, imageId, imageUrl, city, isWater
        Status status = new Status(
                avObject.getObjectId(),
                avObject.getString("text"),
                avObject.getString("location"),
                file.getObjectId(),
                file.getUrl(),
                avObject.getString("city"),
                avObject.getBoolean("isWater"),
                avObject.getString("coord"));
        try {
            databaseManager.getDaoByClass(Status.class).getRawDao().createOrUpdate(status);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("chat","保存状态出错："+e.toString());
        }

        return status;
    }
    public void queryAVObject(String objectId, String tableClass, String[] includes,final QueryCallback callback) {
        AVQuery<AVObject> query = new AVQuery<>(tableClass);
        if(includes != null){
            for(String key : includes){
                query.include(key);
            }
        }

        query.getInBackground(objectId, new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                callback.done(avObject, false, e);
            }
        });
    }


    private String getStatusImageName() {
        return String.valueOf(System.currentTimeMillis());
    }

    /**
     * 发送状态
     *
     * @param point
     * @param imgPath 状态图片本绝对路径
     * @param text
     * @param coord
     * @param lis
     */
    public void sendStatus( final AVGeoPoint point, String imgPath, final String text, final String coord, final OnRequestDataListener<AVFile> lis) {
        if (lis == null)
            return;

        if ( point == null || imgPath == null) {
            lis.onError(new WXException("参数错误"));
            return;
        }

        final AVFile file;
        try {
            file = AVFile.withAbsoluteLocalPath(getStatusImageName(), imgPath);
        } catch (IOException e) {
            e.printStackTrace();
            lis.onError(new WXException(e.getMessage()));
            return;
        }

        // 首先保存状态图片，之后在保存上传状态
        file.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    sendStatusToService( point, file, text, coord, lis);
                } else {
                    lis.onError(new WXException(e.getMessage()));
                }
            }
        });
    }

    /**
     * 发送状态到LeanCloud云端
     *
     * @param point
     * @param imgFile 状态图片
     * @param text
     * @param coord
     * @param lis
     */
    private void sendStatusToService(AVGeoPoint point, final AVFile imgFile, String text, String coord, final OnRequestDataListener<AVFile> lis) {
        JSONObject jsoLocation = new JSONObject();
        try {
            jsoLocation.put("latitude", point.getLatitude());
            jsoLocation.put("longitude", point.getLongitude());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, Object> params = new HashMap<>();
        params.put(LC.method.SendNewStatus.keyLocation, jsoLocation);
        params.put(LC.method.SendNewStatus.keyImageId, imgFile.getObjectId());
        if (text != null) {
            params.put(LC.method.SendNewStatus.keyText, text);
            params.put(LC.method.SendNewStatus.keyCoord, coord);
        }
        Log.e("dss", "sendStatus=" + params.toString());

        AVCloud.callFunctionInBackground(LC.method.SendNewStatus.functionName, params, new FunctionCallback<Object>() {
            @Override
            public void done(Object o, AVException e) {
                try {
                    if (e != null) {
                        throw new WXException(e);
                    }

                    if (o == null) {
                        throw new WXException("o == null");
                    }

                    Log.e(TAG, "sendStatusToService-success");
                    lis.onSuccess(imgFile);
                } catch (WXException wxe) {
                    LogHelper.e(TAG, "sendStatusToService-fail", e);
                    lis.onError(wxe);
                }
            }
        });
    }
}
