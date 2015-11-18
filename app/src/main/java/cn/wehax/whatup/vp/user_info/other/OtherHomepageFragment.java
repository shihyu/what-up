package cn.wehax.whatup.vp.user_info.other;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import javax.inject.Inject;

import cn.wehax.whatup.R;
import cn.wehax.whatup.config.Constant;
import cn.wehax.whatup.config.IntentKey;
import cn.wehax.whatup.framework.fragment.BaseDataFragment;
import cn.wehax.whatup.support.helper.MoveToHelper;
import cn.wehax.whatup.vp.user_info.user_status.UserStatusListFragment;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;


public class OtherHomepageFragment extends BaseDataFragment {
    public final String TAG = "OtherHomepageFragment";

    @Inject
    OtherHomepagePresenter presenter;

    @InjectView(R.id.user_avatar_image_view)
    ImageView userAvatarIv;

    @InjectView(R.id.user_sex_image_view)
    ImageView userSexIv;

    @InjectView(R.id.user_name_tv)
    TextView usernameTv;

    @InjectView(R.id.user_introduce_tv)
    TextView userIntroduceTv;

    @InjectView(R.id.say_hello_btn)
    Button sayHelloBtn;

    @InjectView(R.id.function_view)
    LinearLayout functionView;

    @InjectView(R.id.report_btn)
    ImageView reportbtn;

    @InjectView(R.id.fragment_container)
    FrameLayout fragmentContainer;

   @InjectExtra(IntentKey.INTENT_KEY_TARGET_UID)
    private String userId;

    @InjectExtra(value = IntentKey.INTENT_KEY_STATUS_ID, optional = true)
    private String statusId;

    private DisplayImageOptions options;
    /**
     * 标记功能按钮是否正在显示
     */
    boolean isFunctionViewShowing = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_other_homepage);

        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @Override
    protected void initPresenter() {
        presenter.init(this, getArguments());
    }

    @Override
    protected void initTopBar() {
        super.initTopBar();

        setTopBarTitle(R.string.personal_homepage);

        showTopBarRightBtn();
        topBarRightImageView.setImageResource(R.drawable.top_bar_function_btn_bg_selector);
        topBarRightImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFunctionViewShowing) {
                    hideFunctionView();
                } else {
                    showFunctionView();
                }
            }
        });
    }

    @Override
    protected void initView() {
        reportbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.moveToDenounceView();
            }
        });

        sayHelloBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoveToHelper.moveToChatView(getActivity(), userId, statusId);
            }
        });

        userAvatarIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.viewImage();
            }
        });

        // add fragment
        UserStatusListFragment fragment = new UserStatusListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(IntentKey.INTENT_KEY_TARGET_UID, userId);
        fragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().
                beginTransaction().
                replace(R.id.fragment_container, fragment).
                commit();
    }

    @Override
    protected void loadData() {
        presenter.requestUserInformation(userId);
    }

    @Override
    protected void onReloadData() {
        loadData();
    }

    public void setMyInformation(String nickname, Integer sex, String avatarUrl, String introduce) {
        if (avatarUrl != null) {
            ImageLoader.getInstance().displayImage(avatarUrl, userAvatarIv, options);
        }

        if (sex != null) {
            if (sex == Constant.SEX_MALE)
                userSexIv.setImageResource(R.drawable.ic_male);
            else
                userSexIv.setImageResource(R.drawable.ic_female);
        }

        if (nickname != null)
            usernameTv.setText(nickname);

        if (introduce != null)
            userIntroduceTv.setText(introduce);
    }

    private void showFunctionView() {
        functionView.setVisibility(View.VISIBLE);
    }

    private void hideFunctionView() {
        functionView.setVisibility(View.GONE);
    }
}
