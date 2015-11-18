package cn.wehax.whatup.vp.user_info.edit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.inject.Inject;

import java.io.ByteArrayOutputStream;

import cn.wehax.whatup.framework.fragment.WXSingleFragmentActivity;
import cn.wehax.whatup.support.image.Config;
import cn.wehax.whatup.support.image.ImageUtils;
import cn.wehax.whatup.vp.user_info.personal.PersonalHomepagePresenter;

public class EditUserInfoActivity extends WXSingleFragmentActivity {
    public Bitmap bitmap;
    Intent intent;
    EditUserInfoFragment editUserInfoFragment;

    @Inject
    EditUserInfoPresenter presenter;

    @Override
    protected Fragment onCreateFragment() {
        intent= getIntent();
        if(editUserInfoFragment==null)
            editUserInfoFragment=new EditUserInfoFragment();

        return editUserInfoFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data !=null){
            String path = ImageUtils.getLocalImagePath(this) + "/" + ImageUtils.getImageName(Config.ENCODINGTYPE_JPEG);
            bitmap = ImageUtils.doImageFromPick(this, data.getData(),
                    path);
            bitmap = ImageUtils.toOvalBitmap(bitmap);
        }
       if(bitmap != null){
           editUserInfoFragment.setUserAvatar(bitmap);
           presenter.updateUserAvatar(bitmap);
           ByteArrayOutputStream baos = new ByteArrayOutputStream();
           bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
           byte [] bytes=baos.toByteArray();
           intent.putExtra("avatar", bytes);
           setResult(PersonalHomepagePresenter.UPDATE_DATA, intent);
       }

    }


}
