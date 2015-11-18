package cn.wehax.whatup.support.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import cn.wehax.whatup.model.chatView.ChatMessage;
import cn.wehax.whatup.model.status.Status;
import cn.wehax.whatup.model.conversation.Conversation;

/**
 * Created by howe on 15/6/3.
 * Email:howejee@gmail.com
 */
public class OpenDatabaseHelper extends OrmLiteSqliteOpenHelper{
    public final static String DEFAULT_DATABASE_NAME = "default";

    public final static int DATABASE_VERSION = 1;

    private ConnectionSource connectionSource = null;

    public OpenDatabaseHelper(Context context, String userName) {
        super(context, "main_"+userName+".db", null, DATABASE_VERSION);
    }

    public final static Class<?>[] DATABASE_ENTITY_LIST = new Class<?>[]{
            ChatMessage.class,
            Conversation.class,
            Status.class
    };

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try{
            for(Class<?>clazz : DATABASE_ENTITY_LIST){
                TableUtils.createTable(connectionSource,clazz);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            for (Class<?> clazz : DATABASE_ENTITY_LIST) {
                TableUtils.dropTable(connectionSource, clazz, true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ConnectionSource getConnectionSource() {
        if(connectionSource == null){
            connectionSource = super.getConnectionSource();
        }
        return connectionSource;
    }
}
