package cn.wehax.whatup.framework.model;


/**
 * 网络请求监听器
 */
public interface OnRequestDataListener<T> {
    /**
     * 网络请求成功
     * @param data 返回数据对象
     */
    void onSuccess(T data);

    /**
     * 网络请求失败
     */
    void onError(WXException wxe);
}
