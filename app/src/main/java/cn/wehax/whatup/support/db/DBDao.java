package cn.wehax.whatup.support.db;

import android.app.Application;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

/**
 * Created by howe on 15/6/3.
 * Email:howejee@gmail.com
 */
public class DBDao<T> {

    Dao<T , Object> dao;

    Class<T> tClass;

    OrmLiteSqliteOpenHelper ormHelper;

    Application application;

    public DBDao(Class<T> clazz,OrmLiteSqliteOpenHelper ormHelper,Application context){
        this.tClass = clazz;
        this.ormHelper = ormHelper;
        this.application = context;
        try{
            dao = ormHelper.getDao(clazz);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public Dao<T,Object> getRawDao(){
        return dao;
    }

    public boolean idExists(Object id){
        try{
            return dao.idExists(id);
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }


}
