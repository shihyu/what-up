package cn.wehax.whatup.framework.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.wehax.whatup.R;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public abstract class BaseDataFragment extends RoboFragment {
    int mContentViewResID = -1; // 内容视图布局资源ID

    @InjectView(R.id.top_bar)
    LinearLayout topBar; // 标题栏

    @InjectView(R.id.top_bar_left_image_view)
    ImageView topBarLeftImageView; // 标题栏，左按钮

    @InjectView(R.id.top_bar_title)
    TextView topBarTitleTextView; // 标题栏，标题

    @InjectView(R.id.top_bar_right_image_view)
    protected ImageView topBarRightImageView; // 标题栏，右按钮

    @InjectView(R.id.loading_data)
    View loadingView;

    @InjectView(R.id.reload_data)
    View reloadView;

    @InjectView(R.id.reload_data_btn)
    Button reloadBtn;

    @InjectView(R.id.list_empty)
    protected View listEmptyView;

    @InjectView(R.id.page_root)
    protected LinearLayout pageRootView;

    View contentView;

    /**
     * 设置Fragment的内容视图
     * <p/>
     * <p> 注：本方法的使用方式与Activity的setContentView方法类似，在onCreate()方法中调用 </p>
     *
     * @param layoutResId Fragment布局文件的资源ID
     */
    public void setContentView(int layoutResId) {
        mContentViewResID = layoutResId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout rootView;
        rootView = (LinearLayout) inflater.inflate(R.layout.framework_fragment_base_data, container, false);
        FrameLayout contentContainer = (FrameLayout) rootView.findViewById(R.id.content_container);

        if (mContentViewResID != -1) {
            contentView = inflater.inflate(mContentViewResID, container, false);
            contentContainer.addView(contentView);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initWhenActivityCreated();

        // 注意：initPresenter()方法先执行、initView()在执行、loadData()方法最后
        initPresenter();
        initTopBar();
        initView();
        loadData();
    }


    private void initWhenActivityCreated() {
        reloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onReloadData();
            }
        });
    }

    /**
     * 初始化Presenter对象
     */
    protected void initPresenter() {

    }

    /**
     * 初始化顶部栏
     */
    protected void initTopBar() {
        // 点击返回按钮，关闭当前Activity
        topBarLeftImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }


    /**
     * 初始化控件
     */
    protected void initView() {

    }

    /**
     * 加载数据
     */
    protected void loadData() {

    }

    /**
     * 重新加载数据
     */
    protected abstract void onReloadData();


    /**
     * 如果Fragment所属Activity实例有效，返回true
     */
    public boolean isActivityAlive() {
        if (getActivity() == null || getActivity().isFinishing()) {
            return false;
        }
        return true;
    }

    public void showTopBar() {
        topBar.setVisibility(View.VISIBLE);
    }

    public void hideTopBar() {
        topBar.setVisibility(View.GONE);
    }

    public void showTopBarRightBtn() {
        topBarRightImageView.setVisibility(View.VISIBLE);
    }

    public void setTopBarTitle(String title) {
        topBarTitleTextView.setText(title);
    }

    public void setTopBarTitle(int resID) {
        topBarTitleTextView.setText(resID);
    }

    public void showLoadingView() {
        loadingView.setVisibility(View.VISIBLE);
        reloadView.setVisibility(View.GONE);
        listEmptyView.setVisibility(View.GONE);
        if (contentView != null) {
            contentView.setVisibility(View.GONE);
        }
    }

    public void showContentView() {
        loadingView.setVisibility(View.GONE);
        reloadView.setVisibility(View.GONE);
        if (contentView != null) {
            contentView.setVisibility(View.VISIBLE);
        }
    }

    public void showReloadView() {
        loadingView.setVisibility(View.GONE);
        reloadView.setVisibility(View.VISIBLE);
        listEmptyView.setVisibility(View.GONE);
        if (contentView != null) {
            contentView.setVisibility(View.GONE);
        }
    }
}
