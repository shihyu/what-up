package cn.wehax.whatup.vp.login.complete_user_info;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.inject.Inject;

import cn.wehax.whatup.R;
import cn.wehax.whatup.config.Constant;
import cn.wehax.whatup.framework.fragment.BaseFragment;
import cn.wehax.whatup.support.helper.InputCheckHelper;
import cn.wehax.whatup.support.helper.MoveToHelper;
import cn.wehax.whatup.support.image.Config;
import cn.wehax.whatup.support.image.ImageUtils;
import cn.wehax.whatup.support.image.album.AlbumActivity;
import cn.wehax.whatup.support.image.camera.CameraActivity;
import roboguice.inject.InjectView;

/**
 * 注册第三步：完善个人信息<br>
 * <p/>
 * 说明：<br>
 * （1）以下用户信息是必要信息（1）昵称（2）性别<br>
 * （2）进入本页面时，必须已经登录
 */
public class CompleteUserInfoFragment extends BaseFragment {
    private static final String TAG = "CompleteUserInfoFragment";

    @Inject
    CompleteUserInfoPresenter presenter;

    @InjectView(R.id.select_image_from_native_image_view)
    ImageView selectImageFromNativeIv;

    @InjectView(R.id.select_image_from_camera_image_view)
    ImageView selectImageFromCameraIv;

    @InjectView(R.id.user_avatar_image_view)
    ImageView userAvatarIv;

    @InjectView(R.id.user_nickname_edit_text)
    EditText userNicknameEt;

    /**
     * (1)默认，显示选中性别(2)选择性别时，第一行按钮
     */
    @InjectView(R.id.user_sex_first_relative_layout)
    RelativeLayout userSexFirstRl;

    /**
     * (1)默认，显示选中性别(2)选择性别时，第一行性别
     */
    @InjectView(R.id.user_sex_first_text_view)
    TextView userSexFirstTv;

    @InjectView(R.id.complete_user_info_bottom_btn)
    Button bottomBtn;

    /**
     * 用户性别（ 0男 1女）
     */
    int userSex = Constant.SEX_MALE; // 默认男性

    /**
     * 选择性别时，第二行按钮
     */
    @InjectView(R.id.user_sex_second_relative_layout)
    RelativeLayout userSexSecondRl;

    /**
     * 选择性别时，第二行性别
     */
    @InjectView(R.id.user_sex_second_text_view)
    TextView userSexSecondTv;

    Bitmap avatorBmp = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_complete_user_info);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String path = ImageUtils.getLocalImagePath(getActivity()) + "/" + ImageUtils.getImageName(Config.ENCODINGTYPE_JPEG);
            avatorBmp = ImageUtils.doImageFromPick(getActivity(), data.getData(),
                    path);
            avatorBmp = ImageUtils.toOvalBitmap(avatorBmp);
        }
        if (avatorBmp != null) {
            setUserAvatar(avatorBmp);
        }
    }

    public void setUserAvatar(Bitmap bitmap){
        if(bitmap!=null){
            userAvatarIv.setImageBitmap(bitmap);
        }
    }

    @Override
    protected void initPresenter() {
        presenter.setView(this);
    }

    /**
     * 标记是否正在选择性别
     */
    boolean isSelectingSex = false;
    /**
     * 性别Icon
     */
    Drawable drawableMaleIc, drawableFemaleIc;

    @Override
    protected void initView() {
        setTopBarTitle(R.string.complete_user_info);

        int icSize = getResources().getDimensionPixelSize(R.dimen.sex_ic_size);
        drawableMaleIc = getResources().getDrawable(R.drawable.ic_male);
        drawableMaleIc.setBounds(0, 0, icSize, icSize);
        drawableFemaleIc = getResources().getDrawable(R.drawable.ic_female);
        drawableFemaleIc.setBounds(0, 0, icSize, icSize);

        setSexSelectTextView(userSexFirstTv, userSex);

        userAvatarIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelectImageViewShowing()) {
                    hideSelectImageView();
                } else {
                    showSelectImageView();
                }
            }
        });

        /**
         * 点击第一个性别按钮
         */
        userSexFirstRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelectingSex) {
                    // 如果正在选择性别，用户选择性别未发生变化，直接关闭性别选择框
                    hideSexSelectView();
                    isSelectingSex = false;
                } else {
                    showSexSelectView();
                    isSelectingSex = true;
                }
            }
        });

        /**
         * 点击第二个性别按钮
         */
        userSexSecondRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 用户选择性别发生变化
                userSex = (int) userSexSecondTv.getTag();
                isSelectingSex = false;
                hideSexSelectView();
                setSexSelectTextView(userSexFirstTv, userSex);
            }
        });

        selectImageFromNativeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AlbumActivity.class);
                intent.putExtra(Config.INTENT_VALUE_NEED_CROP, true);
                intent.putExtra(Config.INTENT_VALUE_IS_PREVIEW, false);
                getActivity().startActivityForResult(intent, Config.REQUEST_CODE_CALL_ALBUM);
            }
        });

        selectImageFromCameraIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CameraActivity.class);
                intent.putExtra(Config.INTENT_VALUE_NEED_CROP, false);
                intent.putExtra(Config.INTENT_VALUE_NEED_CROP, true);
                getActivity().startActivityForResult(intent, Config.REQUEST_CODE_CALL_CAMERA);
            }
        });

        bottomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 检查用户昵称
                String nickName = userNicknameEt.getText().toString();
                if (!InputCheckHelper.checkInputNickname(getActivity(), nickName)) {
                    return;
                }

                // 保存用户信息，进入主页
                Log.e(TAG, "updateUserInfo/userSex = " + userSex);
                presenter.saveUserInfo(nickName, userSex, avatorBmp);
            }
        });
    }

    /**
     * 隐藏选择性别按钮
     */
    private void hideSexSelectView() {
        userSexSecondRl.setVisibility(View.INVISIBLE);
    }

    /**
     * 显示选择性别按钮
     */
    private void showSexSelectView() {
        if (userSex == Constant.SEX_MALE) {
            setSexSelectTextView(userSexSecondTv, Constant.SEX_FEMALE);
        } else {
            setSexSelectTextView(userSexSecondTv, Constant.SEX_MALE);
        }

        userSexSecondRl.setVisibility(View.VISIBLE);
    }


    /**
     * 设置性别按钮
     *
     * @param tv
     * @param sex
     */
    private void setSexSelectTextView(TextView tv, int sex) {
        if (sex == Constant.SEX_MALE) {
            tv.setText(R.string.sex_male);
            tv.setTag(Constant.SEX_MALE);
            tv.setCompoundDrawables(drawableMaleIc, null, null, null);
        } else {
            tv.setText(R.string.sex_female);
            tv.setTag(Constant.SEX_FEMALE);
            tv.setCompoundDrawables(drawableFemaleIc, null, null, null);
        }
    }

    /**
     * 如果选择图片控件正在显示，返回true
     */
    private boolean isSelectImageViewShowing() {
        if (selectImageFromNativeIv.getVisibility() == View.VISIBLE) {
            return true;
        } else {
            return false;
        }
    }

    private void showSelectImageView() {
        selectImageFromNativeIv.setVisibility(View.VISIBLE);
        selectImageFromCameraIv.setVisibility(View.VISIBLE);
    }

    private void hideSelectImageView() {
        selectImageFromNativeIv.setVisibility(View.GONE);
        selectImageFromCameraIv.setVisibility(View.GONE);
    }

    public void moveToARMainView(Activity activity) {
        MoveToHelper.moveToARMainView(getActivity());
        getActivity().finish();
    }
}
