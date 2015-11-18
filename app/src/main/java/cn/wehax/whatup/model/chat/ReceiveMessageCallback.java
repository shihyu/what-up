package cn.wehax.whatup.model.chat;

/**
 * Created by sanchibing on 2015/8/6.
 * Email:sanchibing@gmail.com
 */
public interface ReceiveMessageCallback<T>  {
    void done(T message,Exception e);
}
