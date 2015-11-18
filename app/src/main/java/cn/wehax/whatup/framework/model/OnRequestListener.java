package cn.wehax.whatup.framework.model;

/**
 * 网络请求监听器
 *
 * <p>
 * 注：OnRequestListener只监听网络请求成功与否的状态，不返回任何数据
 * </p>
 */
public interface OnRequestListener {
    /**
     * 网络请求成功
     */
    void onSuccess();

    /**
     * 网络请求失败
     */
    void onError(WXException e);
}
