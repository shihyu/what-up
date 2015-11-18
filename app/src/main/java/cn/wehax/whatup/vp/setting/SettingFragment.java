package cn.wehax.whatup.vp.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.inject.Inject;

import cn.wehax.whatup.R;
import cn.wehax.whatup.framework.fragment.BaseFragment;
import cn.wehax.whatup.vp.login.choose.ChooseLoginOrRegisterActivity;
import cn.wehax.whatup.vp.setting.BubbleSetting.BubbleSetActivity;
import roboguice.inject.InjectView;

/**
 * 设置页面
 */
public class SettingFragment extends BaseFragment {
    private static final String TAG = "SettingFragment";

    @Inject
    SettingPresenter presenter;

    @InjectView(R.id.setting_chat_bubble)
    Button chatBubbleBtn;

    @InjectView(R.id.setting_clear_cache)
    Button clearCacheBtn;

    @InjectView(R.id.setting_logout)
    Button logoutBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_setting);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void initPresenter() {
        presenter.setView(this);
    }


    @Override
    protected void initView() {
        setTopBarTitle(R.string.setting);

        chatBubbleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), BubbleSetActivity.class));
            }
        });

        clearCacheBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.clearCache();
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.logout();
            }
        });
    }

    public void showLogoutConfirmDialog() {
        moveToChooseLoginView();
    }

    /**
     * 跳转到登录页面，并且清空活动栈
     */
    public void moveToChooseLoginView() {
        Intent intent = new Intent(getActivity(), ChooseLoginOrRegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        getActivity().startActivity(intent);
    }
}
