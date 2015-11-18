package cn.wehax.whatup.framework.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.wehax.whatup.R;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public abstract class BaseFragment extends RoboFragment {
    int mContentViewResID = -1; // 内容视图布局资源ID

    @InjectView(R.id.top_bar)
    protected LinearLayout mTopBar; // 标题栏

    @InjectView(R.id.top_bar_left_image_view)
    protected ImageView topBarLeftImageView; // 标题栏，左按钮

    @InjectView(R.id.top_bar_title)
    protected TextView topBarTitleTextView; // 标题栏，标题

    @InjectView(R.id.top_bar_right_image_view)
    protected ImageView topBarRightImageView; // 标题栏，右按钮

    /**
     * 设置Fragment的内容视图
     *
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
        rootView = (LinearLayout) inflater.inflate(R.layout.framework_fragment_base, container, false);

        View contentView;
        if (mContentViewResID != -1) {
            contentView = inflater.inflate(mContentViewResID, container, false);
            rootView.addView(contentView);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 注意：initPresenter()方法先执行、initView()在执行、loadData()方法最后
        initPresenter();
        initTopBar();
        initView();
        loadData();
    }


    /**
     * 初始化Presenter对象
     */
    protected void initPresenter(){

    }

    /**
     * 初始化顶部栏
     */
    protected void initTopBar(){
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
    protected void initView(){

    }

    /**
     * 加载数据
     */
    protected void loadData(){

    }

    /**
     * 如果Fragment所属Activity实例有效，返回true
     */
    public boolean isActivityAlive() {
        if (getActivity() == null || getActivity().isFinishing()) {
            return false;
        }
        return true;
    }

    public void showTopBar(){
        mTopBar.setVisibility(View.VISIBLE);
    }

    public void hideTopBar(){
        mTopBar.setVisibility(View.GONE);
    }

    public void setTopBarTitle(String title){
        topBarTitleTextView.setText(title);
    }

    public void setTopBarTitle(int resID){
        topBarTitleTextView.setText(resID);
    }

    public void showTopBarRightBtn(){
        topBarRightImageView.setVisibility(View.VISIBLE);
    }
}
