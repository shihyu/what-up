package cn.wehax.whatup.model.chat;

/**
 * Created by howe on 15/6/13.
 * Email:howejee@gmail.com
 */
public interface QueryCallback<T> {

    void done(T result,boolean isLocal,Exception e);
}
