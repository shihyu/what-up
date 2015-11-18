package cn.wehax.whatup.vp.user_info.user_status;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import javax.inject.Inject;

import cn.wehax.whatup.R;
import cn.wehax.whatup.framework.fragment.BaseDataFragment;
import roboguice.inject.InjectView;

/**
 * 用户状态列表
 */
public class UserStatusListFragment extends BaseDataFragment {
    public final String TAG = "UserStatusListFragment";

    @Inject
    UserStatusListPresenter presenter;

    @InjectView(R.id.user_status_list_view)
    PullToRefreshListView ptrListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user_status_list);
    }

    @Override
    protected void initPresenter() {
        presenter.init(this, getArguments());
    }

    @Override
    protected void initView() {
        hideTopBar();
        pageRootView.setBackgroundColor(getResources().getColor(R.color.transparent));

        ptrListView.setEmptyView(listEmptyView);
        ptrListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                presenter.refreshData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                presenter.loadMoreData();
            }
        });
        ptrListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                presenter.viewMultiImage(--position);
            }
        });

        ptrListView.getLoadingLayoutProxy(true, false)
                .setRefreshingLabel("正在刷新");
        ptrListView.getLoadingLayoutProxy(true, false).setPullLabel(
                "下拉刷新");
        ptrListView.getLoadingLayoutProxy(true, false)
                .setReleaseLabel("释放后刷新");

        ptrListView.getLoadingLayoutProxy(false, true)
                .setRefreshingLabel("正在加载");
        ptrListView.getLoadingLayoutProxy(false, true).
                setPullLabel("上拉加载更多");
        ptrListView.getLoadingLayoutProxy(false, true)
                .setReleaseLabel("释放后加载");

    }

    @Override
    protected void loadData() {
        presenter.loadData();
    }

    @Override
    protected void onReloadData() {
        loadData();
    }

}
