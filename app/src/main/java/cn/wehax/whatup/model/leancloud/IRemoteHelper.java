package cn.wehax.whatup.model.leancloud;

import com.avos.avoscloud.AVObject;

import java.util.HashMap;

import cn.wehax.whatup.model.chat.QueryCallback;

/**
 * Created by sanchibing on 2015/8/5.
 * Email:sanchibing@gmail.com
 */
public interface IRemoteHelper {
    /**
     * 查找Leancloud表数据
     * @param objectId 某条数据id
     * @param tableClass 数据表名
     * @param includes   需要关联查询的字段
     * @param callback   回调
     */
    void queryAVObject(String objectId,String tableClass,String[] includes,QueryCallback callback);

    /**
     * 更新Leancloud 数据
     * @param avObject 需要更新的数据类
     * @param data    需要更新的字段及内容
     */
    void updateAVObject(AVObject avObject,HashMap<String,Object> data);

    /**
     * 查找用户
     */
    void queryAVUser(String userId,QueryCallback callback);

}
