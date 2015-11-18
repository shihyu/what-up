package cn.wehax.whatup.vp.user_info.edit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.inject.Inject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import cn.wehax.whatup.R;
import cn.wehax.whatup.framework.fragment.BaseFragment;
import cn.wehax.whatup.support.image.Config;
import cn.wehax.whatup.support.image.album.AlbumActivity;
import cn.wehax.whatup.support.image.camera.CameraActivity;
import roboguice.inject.InjectView;

/**
 * 编辑用户信息
 */
public class EditUserInfoFragment extends BaseFragment {
    private static final String TAG = "EditUserInfoFragment";

    @Inject
    EditUserInfoPresenter presenter;

    @InjectView(R.id.select_image_from_native_image_view)
    ImageView selectImageFromNativeIv;

    @InjectView(R.id.select_image_from_camera_image_view)
    ImageView selectImageFromCameraIv;

    @InjectView(R.id.user_avatar_image_view)
    ImageView userAvatarIv;

    @InjectView(R.id.user_nickname_edit_text)
    EditText userNicknameEt;

    @InjectView(R.id.user_introduce_edit_text)
    EditText userIntroduceEt;

    @InjectView(R.id.user_edit_sava_btn)
    Button savaBtn;

    private DisplayImageOptions options;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_edit_user_info);

        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void initPresenter() {
        presenter.init(this);
    }


    @Override
    protected void initView() {
        setTopBarTitle(R.string.edit_user_info);

        selectImageFromNativeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AlbumActivity.class);
                intent.putExtra(Config.INTENT_VALUE_NEED_CROP, true);
                intent.putExtra(Config.INTENT_VALUE_IS_PREVIEW,false);
                getActivity().startActivityForResult(intent, Config.REQUEST_CODE_CALL_ALBUM);

            }
        });

        selectImageFromCameraIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CameraActivity.class);
                intent.putExtra(Config.INTENT_VALUE_NEED_CROP, false);
                intent.putExtra(Config.INTENT_VALUE_NEED_CROP,true);
                getActivity().startActivityForResult(intent, Config.REQUEST_CODE_CALL_CAMERA);
            }
        });

        savaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.saveUserInfo(userNicknameEt.getText().toString(), userIntroduceEt.getText().toString());
            }
        });
    }

    public void setUserInfo(String nickname, String introduce, String avatarUrl) {
        if (avatarUrl != null) {
            ImageLoader.getInstance().displayImage(avatarUrl, userAvatarIv, options);
        }

        if (nickname != null)
            userNicknameEt.setText(nickname);

        if (introduce != null)
            userIntroduceEt.setText(introduce);
    }

    public void setUserAvatar(Bitmap bitmap){
            if(bitmap!=null){
                userAvatarIv.setImageBitmap(bitmap);
            }
     }

}
