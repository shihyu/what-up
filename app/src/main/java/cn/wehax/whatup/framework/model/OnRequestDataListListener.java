package cn.wehax.whatup.framework.model;

import java.util.List;

public interface OnRequestDataListListener<T> {
    void onDataListReturn(List<T> dataList, int currentPage, int totalPage);
    void onError(WXException error);
}
