package cn.wehax.whatup.support.image.camera;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

import cn.wehax.whatup.support.image.Config;
import cn.wehax.whatup.support.image.ImageUtils;
import cn.wehax.whatup.support.image.crop.CropActivity;

/**
 * Created by howe on 15/3/11.
 * Email:howejee@gmail.com
 * 在这调用系统相机，并处理返回数据（例如裁减图片）
 */
public class CameraActivity extends Activity {

    File file;
    boolean hasNeedCrop = false;
    int encodingType;
    int returnType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        hasNeedCrop = getIntent().getBooleanExtra(Config.INTENT_VALUE_NEED_CROP,false);
        encodingType = getIntent().getIntExtra(Config.INTENT_VALUE_ENCODINGTYPE,Config.ENCODINGTYPE_JPEG);
        returnType = getIntent().getIntExtra(Config.INTENT_VALUE_RETURNTYPE,Config.RETURNTYPE_DATA);

        String path = ImageUtils.getLocalImagePath(this) + "/" + getImageNameWithTime(encodingType);
        file = new File(path);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        this.startActivityForResult(takePhotoIntent, Config.REQUEST_CODE_CAMERA_TO_SYSTEM_CAMERA);

        super.onCreate(savedInstanceState);
    }



    public String getImageNameWithTime(int encodingType) {
        String suffix = ".jpg";
        if(encodingType == Config.ENCODINGTYPE_PNG){
            suffix = ".png";
        }
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmsss");
        return dateFormat.format(date) + suffix;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            //拍照成功或者裁减成功
            switch (requestCode){
                case Config.REQUEST_CODE_CAMERA_TO_SYSTEM_CAMERA:
                    //拍照返回
                    doCameraReturn();
                    break;

                case Config.REQUEST_CODE_CAMERA_TO_IMG_CROP:
                    //图片裁减返回
                    doCropReturn(data);
                    break;
            }

        }else{
            finish();
        }

    }

    private void doCameraReturn(){
        try{
            //判断是否需要裁减图片
            if(hasNeedCrop){
                //调用系统裁减
                Uri uri = Uri.fromFile(file);
//                Intent intent = new Intent("com.android.camera.action.CROP");// 打开图片裁减工具
//                if (uri != null) {
//                    intent.setDataAndType(uri, "image/*");
//                    intent.putExtra("crop", "true");
//                    intent.putExtra("aspectX", 1);
//                    intent.putExtra("aspectY", 1);
//                    intent.putExtra("outputX", 200);
//                    intent.putExtra("outputY", 200);
//                    intent.putExtra("return-data", true);
//                }
                Log.e("crop","裁减图片开始");
                Intent intent = new Intent(CameraActivity.this,CropActivity.class);
                intent.putExtra(Config.INTENT_VALUE_CROP_WIDTH,getIntent().getIntExtra(Config.INTENT_VALUE_CROP_WIDTH,200));
                intent.putExtra(Config.INTENT_VALUE_CROP_HEIGHT,getIntent().getIntExtra(Config.INTENT_VALUE_CROP_HEIGHT,200));
                intent.putExtra(Config.INTENT_VALUE_CROP_PATH,uri.toString());
                CameraActivity.this.startActivityForResult(intent,
                    Config.REQUEST_CODE_CAMERA_TO_IMG_CROP);
            }else{
                setResult(RESULT_OK, getIntent().setData(Uri.fromFile(file)));
                finish();
            }

        }catch (Exception e){
            setResult(RESULT_OK, getIntent().setData(Uri.fromFile(file)));
            finish();
        }
    }

    private void doCropReturn(Intent data){
        data.putExtra(Config.INTENT_VALUE_ENCODINGTYPE,encodingType);
        data.putExtra(Config.INTENT_VALUE_RETURNTYPE,returnType);
        setResult(RESULT_OK, data);
        this.finish();
    }


}
