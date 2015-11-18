package cn.wehax.whatup.support.db;

import android.app.Application;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

/**
 * Created by howe on 15/6/3.
 * Email:howejee@gmail.com
 */
@Singleton
public class DatabaseManager {

    private final Application application;

    private String curDBName;

    private OpenDatabaseHelper dbHelper;

    private Map<String, DBDao> daoMap;

    @Inject
    DatabaseManager(Provider<Application> provider) {
        this.application = provider.get();
        this.daoMap = new HashMap<>();
    }

    public void switchDatabase(String uid) {
        if (TextUtils.isEmpty(uid)) {
            uid = "default";
        }

        //避免重复切换数据库
        if (uid.equals(curDBName)) {
            return;
        }

        if (dbHelper != null && dbHelper.isOpen()) {
            dbHelper.close();
        }
        curDBName = uid;

//        PreferencesUtils.setPreferenceName(Constant.PREFERENCE_NAME);
//        PreferencesUtils.putString(application,Constant.PREFERENCE_KEY_CURRENT_UID,uid);

        dbHelper = new OpenDatabaseHelper(application, uid);

        dbHelper.getWritableDatabase();
        daoMap.clear();
    }

    public <T> DBDao<T> getDaoByClass(Class<T> clazz) {
        if (dbHelper == null) {
            throw new RuntimeException("DatabaseManager NullPointException:dbHelper is null,please call switchDatabase first");
        }
        if (daoMap != null) {
            if (!daoMap.containsKey(clazz.getName())) {
                DBDao<T> dao = new DBDao<>(clazz, dbHelper, application);
                this.daoMap.put(clazz.getName(), dao);
            }
            return this.daoMap.get(clazz.getName());
        } else {
            return null;
        }
    }
}
