package cn.wehax.whatup.vp.user_info.personal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import cn.wehax.whatup.framework.fragment.WXSingleFragmentActivity;

/**
 * 个人用户编辑页
 */
public class PersonalHomepageActivity extends WXSingleFragmentActivity {


    @Override
    protected Fragment onCreateFragment() {
        PersonalHomepageFragment fragment = new PersonalHomepageFragment();
        return fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap=null;
        if(data !=null ){
            byte[] bytes = data.getByteArrayExtra("avatar");
            if(bytes.length >0){
               bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }
        }
        if(bitmap != null){
            ((PersonalHomepageFragment) getFragment()).userAvatarIv.setImageBitmap(bitmap);
        }



    }

}
