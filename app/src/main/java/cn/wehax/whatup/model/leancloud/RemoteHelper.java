package cn.wehax.whatup.model.leancloud;

import android.util.Log;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import java.util.HashMap;
import java.util.Iterator;
import cn.wehax.whatup.model.chat.QueryCallback;

/**
 * Created by sanchibing on 2015/8/7.
 * Email:sanchibing@gmail.com
 */
public class RemoteHelper implements IRemoteHelper {
    @Override
    public void queryAVObject(String objectId, String tableClass, String[] includes, final QueryCallback callback) {
        AVQuery<AVObject> query = new AVQuery<>(tableClass);
        if (includes != null) {
            for (String key : includes) {
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

    @Override
    public void updateAVObject(AVObject avObject, HashMap<String, Object> data) {
        if (data != null && !data.isEmpty()) {
            Iterator iterator = data.entrySet().iterator();
            while (iterator.hasNext()) {
                HashMap.Entry<String, Object> entry = (HashMap.Entry<String, Object>) iterator.next();
                String key = entry.getKey();
                Object obj = entry.getValue();

                avObject.put(key, obj);
                Log.e("chat", "key=" + key + " value=" + obj);
            }
            avObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        Log.e("chat", "更新对象成功");
                    } else {
                        Log.e("chat", "更新对象失败" + e.toString());
                    }
                }
            });
        }

    }

    @Override
    public void queryAVUser(String userId, final QueryCallback callback) {
        AVQuery<AVUser> query = AVUser.getQuery();
        query.getInBackground(userId, new GetCallback<AVUser>() {
            @Override
            public void done(AVUser avUser, AVException e) {
                callback.done(avUser, false, e);
            }
        });
    }
}
