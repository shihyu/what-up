package cn.wehax.whatup.framework.model;

/**
 * 网络请求监听器
 */
public interface OnRequestResultListener {

    /**
     * 网络请求成功
     * @param result 请求结果
     */
    void onResult(Boolean result);

    /**
     * 网络请求失败
     */
    void onError(WXException wxe);
}
