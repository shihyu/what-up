package cn.wehax.whatup.vp.user_info.denounce;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import javax.inject.Inject;


import cn.wehax.whatup.R;
import cn.wehax.whatup.config.Constant;
import cn.wehax.whatup.framework.fragment.BaseFragment;
import roboguice.inject.InjectView;

/**
 * 举报页面
 */
public class DenounceFragment extends BaseFragment {
    public final String TAG = "DenounceFragment";

    @Inject
    DenouncePresenter presenter;

    @InjectView(R.id.user_avatar_image_view)
    ImageView userAvatarIv;

    @InjectView(R.id.user_sex_image_view)
    ImageView userSexIv;

    @InjectView(R.id.user_name_tv)
    TextView usernameTv;

    @InjectView(R.id.denounce_reason_radio_group)
    RadioGroup denounceReasonRadioGroup;

    @InjectView(R.id.denounce_btn)
    Button denounceBtn;

    @InjectView(R.id.denounce_reason_1)
    RadioButton denounceReasonRt1;

    @InjectView(R.id.denounce_reason_2)
    RadioButton denounceReasonRt2;

    @InjectView(R.id.denounce_reason_3)
    RadioButton denounceReasonRt3;

    @InjectView(R.id.denounce_reason_4)
    RadioButton denounceReasonRt4;

    @InjectView(R.id.denounce_reason_5)
    RadioButton denounceReasonRt5;

    private String reason;
    private DisplayImageOptions options;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_denounce);

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

        setTopBarTitle(R.string.denounce);
    }

    @Override
    protected void initView() {
        reason = denounceReasonRt1.getText().toString();

        denounceReasonRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.denounce_reason_1:
                        reason = denounceReasonRt1.getText().toString();
                        break;
                    case R.id.denounce_reason_2:
                        reason = denounceReasonRt2.getText().toString();
                        break;
                    case R.id.denounce_reason_3:
                        reason = denounceReasonRt3.getText().toString();
                        break;
                    case R.id.denounce_reason_4:
                        reason = denounceReasonRt4.getText().toString();
                        break;
                    case R.id.denounce_reason_5:
                        reason = denounceReasonRt5.getText().toString();
                        break;
                }

            }
        });

        denounceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.denounce(reason);
            }
        });
    }

    public void setUserInformation(String nickname, Integer sex, String avatarUrl) {
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
    }

}
