package cn.wehax.whatup.vp.user_info.personal;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import javax.inject.Inject;

import cn.wehax.whatup.R;
import cn.wehax.whatup.config.Constant;
import cn.wehax.whatup.config.IntentKey;
import cn.wehax.whatup.framework.fragment.BaseFragment;
import cn.wehax.whatup.support.helper.MoveToHelper;
import cn.wehax.whatup.vp.user_info.user_status.UserStatusListFragment;
import roboguice.inject.InjectView;


public class PersonalHomepageFragment extends BaseFragment {
    public final String TAG = "PersonalHomepageFragment";

    @Inject
    PersonalHomepagePresenter presenter;

    @InjectView(R.id.user_avatar_image_view)
    de.hdodenhof.circleimageview.CircleImageView userAvatarIv;

    @InjectView(R.id.user_sex_image_view)
    ImageView userSexIv;

    @InjectView(R.id.user_name_tv)
    TextView usernameTv;

    @InjectView(R.id.user_introduce_tv)
    TextView userIntroduceTv;

    @InjectView(R.id.edit_user_info_iv)
    ImageView editUserInfoIv;

    private DisplayImageOptions options;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_personal_homepage);

        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @Override
    protected void initPresenter() {
        presenter.init(this);
    }

    @Override
    protected void initTopBar() {
        super.initTopBar();

        setTopBarTitle(R.string.personal_homepage);

        showTopBarRightBtn();
        topBarRightImageView.setImageResource(R.drawable.top_bar_setting_btn_bg);
        topBarRightImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoveToHelper.moveToSetting(getActivity());
            }
        });
    }

    @Override
    protected void initView() {
        // add fragment
        UserStatusListFragment fragment = new UserStatusListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(IntentKey.INTENT_KEY_TARGET_UID, presenter.getMineUserId());
        fragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().
                beginTransaction().
                add(R.id.fragment_container, fragment).
                commit();

        editUserInfoIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoveToHelper.moveToEditUserInfoView(getActivity());
            }
        });

        userAvatarIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.viewImage();
            }
        });

        presenter.checkAvatarClickable();
    }

    public void setUserInfo(String nickname, Integer sex, String avatarUrl, String introduce) {
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

    public void setAvatarClickable(boolean enable) {
        userAvatarIv.setEnabled(enable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}
