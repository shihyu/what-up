package cn.wehax.whatup.support.image.crop;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import java.io.File;
import java.lang.Override;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import cn.wehax.whatup.support.image.Config;
import cn.wehax.whatup.support.image.ImageUtils;

/**
 * Created by howe on 15/3/11.
 * Email:howejee@gmail.com
 * 在这调用裁减图片）
 */
public class CropActivity extends Activity {

    private CropLayout mCropLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        int cropWidth = getIntent().getIntExtra(Config.INTENT_VALUE_CROP_WIDTH, 200);
        int cropHeight = getIntent().getIntExtra(Config.INTENT_VALUE_CROP_HEIGHT, 200);
        String path = getIntent().getStringExtra(Config.INTENT_VALUE_CROP_PATH);
        Log.e("crop", "path=" + path);
        if (TextUtils.isEmpty(path)) {
            finish();
        }
        String packageName = this.getPackageName();
        FrameLayout root = (FrameLayout) View.inflate(this, getResources().getIdentifier("activity_crop", "layout", packageName), null);
        setContentView(root);

        mCropLayout = (CropLayout) root.findViewById(getResources().getIdentifier("crop_img_layout", "id", packageName));
        mCropLayout.setBackgroundColor(0xff000000);


        mCropLayout.setCropSize(cropWidth, cropHeight);
        Log.e("crop", "准备获取图片");
        Bitmap bitmap = ImageUtils.getImageByUri(this, Uri.parse(path));
        Log.e("crop", "图片=" + bitmap);
        if (bitmap == null) {
            finish();
        }
        mCropLayout.setImage(bitmap);

        Button cancel = (Button) root.findViewById(getResources().getIdentifier("crop_img_cancel", "id", packageName));
        cancel.setOnClickListener(cancelClick);
        Button crop = (Button) root.findViewById(getResources().getIdentifier("crop_img_ok", "id", packageName));
        crop.setOnClickListener(cropClick);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    View.OnClickListener cancelClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.e("crop", "取消点击了");
            CropActivity.this.finish();
        }
    };

    View.OnClickListener cropClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.e("crop", "裁减点击了");
            Bitmap bitmap = mCropLayout.clip();
            String path = ImageUtils.getLocalImagePath(CropActivity.this) + "/tmp.jpg";

            ImageUtils.saveImage(bitmap, path, 90, 0);
            Log.e("crop", "路径：" + path);
            Intent intent = getIntent();
            intent.setData(Uri.parse("file://" + path));
            setResult(RESULT_OK, intent);
            CropActivity.this.finish();
        }
    };

    public void finish() {
        super.finish();
        Log.e("crop", "被finish掉了");
    }

    protected void onDestroy() {
        super.onDestroy();
        Log.e("crop", "被destroy掉了");
    }
}
