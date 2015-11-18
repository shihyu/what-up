package cn.wehax.whatup.vp.user_info.user_status;

import android.os.Bundle;

import com.avos.avoscloud.AVFile;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.wehax.util.SystemUtil;
import cn.wehax.whatup.config.Constant;
import cn.wehax.whatup.config.IntentKey;
import cn.wehax.whatup.framework.model.OnRequestDataListListener;
import cn.wehax.whatup.framework.model.WXException;
import cn.wehax.whatup.framework.presenter.BasePresenter;
import cn.wehax.whatup.model.leancloud.LC;
import cn.wehax.whatup.model.status.StatusManager;
import cn.wehax.whatup.model.user.UserManager;
import cn.wehax.whatup.support.helper.CommonHelper;

public class UserStatusListPresenter extends BasePresenter<UserStatusListFragment> {
    @Inject
    UserManager userManager;

    @Inject
    StatusManager statusManager;

    List<Map> data = new ArrayList<>();
    UserStatusListAdapter adapter;

    boolean isLastPage = false;

    String userId;

    @Override
    public void init(UserStatusListFragment view, Bundle arguments) {
        super.init(view, arguments);

        userId = getArguments().getString(IntentKey.INTENT_KEY_TARGET_UID);

        adapter = new UserStatusListAdapter(getActivity(), data);
        mView.ptrListView.setAdapter(adapter);
    }

    /**
     * 加载数据
     */
    public void loadData() {
        mView.showLoadingView();

        if (!CommonHelper.checkNetworkAvailability(getActivity())) {
            mView.showReloadView();
            return;
        }

        statusManager.getUserStatusList(userId, Constant.DEFAULT_TIME, Constant.STATUS_LIST_PAGE_SIZE, new OnRequestDataListListener<Map>() {
            @Override
            public void onDataListReturn(List<Map> dataList, int currentPage, int totalPage) {
                if (dataList.size() < Constant.STATUS_LIST_PAGE_SIZE) {
                    isLastPage = true;
                } else {
                    isLastPage = false;
                }

                mView.showContentView();

                data.addAll(dataList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(WXException error) {
                CommonHelper.showErrorMsg(getActivity(), "加载数据失败");
                mView.showReloadView();
            }
        });
    }

    /**
     * 刷新列表
     */
    public void refreshData() {
        if (!CommonHelper.checkNetworkAvailability(getActivity())) {
            mView.ptrListView.onRefreshComplete();
            return;
        }

        statusManager.getUserStatusList(userId, Constant.DEFAULT_TIME, Constant.STATUS_LIST_PAGE_SIZE, new OnRequestDataListListener<Map>() {
            @Override
            public void onDataListReturn(List<Map> dataList, int currentPage, int totalPage) {
                if (dataList.size() < Constant.STATUS_LIST_PAGE_SIZE) {
                    isLastPage = true;
                } else {
                    isLastPage = false;
                }

                if (!dataList.isEmpty()) {
                    data.clear();
                    data.addAll(dataList);
                    adapter.notifyDataSetChanged();
                }

                mView.ptrListView.onRefreshComplete();
            }

            @Override
            public void onError(WXException error) {
                CommonHelper.showErrorMsg(getActivity(), "刷新数据失败");
                mView.ptrListView.onRefreshComplete();
            }
        });
    }

    /**
     * 加载更多数据
     */
    public void loadMoreData() {
        if (!CommonHelper.checkNetworkAvailability(getActivity())) {
            mView.ptrListView.onRefreshComplete();
            return;
        }

        if (isLastPage) {
            CommonHelper.showErrorMsg(getActivity(), "已经是最后一页了");
            mView.ptrListView.onRefreshComplete();
            return;
        }

        Long fromTime = Constant.DEFAULT_TIME;
        if (data.size() > 0) {
            fromTime = (Long)data.get(data.size() - 1).get(LC.method.GetUserStatusList.keyCTime);
        }

        statusManager.getUserStatusList(userId, fromTime, Constant.STATUS_LIST_PAGE_SIZE, new OnRequestDataListListener<Map>() {
            @Override
            public void onDataListReturn(List<Map> dataList, int currentPage, int totalPage) {
                if (dataList.size() < Constant.STATUS_LIST_PAGE_SIZE) {
                    isLastPage = true;
                } else {
                    isLastPage = false;
                }

                if (dataList.isEmpty()) {
                    CommonHelper.showErrorMsg(getActivity(), "已经是最后一页了");
                } else {
                    data.addAll(dataList);
                    adapter.notifyDataSetChanged();
                }

                mView.ptrListView.onRefreshComplete();
            }

            @Override
            public void onError(WXException error) {
                CommonHelper.showErrorMsg(getActivity(), "加载更多数据失败");
                mView.ptrListView.onRefreshComplete();
            }
        });
    }

    /**
     * 浏览多图片
     * @param position 当前浏览图片索引
     */
    public void viewMultiImage(int position) {
        List<String> imgUrls = new ArrayList<>();
        for (Map item : data) {
            AVFile imgFile = (AVFile) item.get(LC.method.GetUserStatusList.keyImageData);
            imgUrls.add(imgFile.getThumbnailUrl(false,
                    SystemUtil.getScreenWidth(getActivity()),
                    SystemUtil.getScreenHeight(getActivity())));
        }
        CommonHelper.viewMultiImage(getActivity(), imgUrls, position);
    }
}
