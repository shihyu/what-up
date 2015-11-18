package cn.wehax.whatup.support.image.album;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import cn.wehax.whatup.R;


/**
 * Created by sanchibing on 2015/7/6.
 */
public class ImagePreviewActivity extends Activity{
    Button cancel_button;
    Button ok_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_preview);
        ImageView img_preview= (ImageView) findViewById(R.id.iv_img_preview);
       cancel_button = (Button) findViewById(R.id.img_cancel);
        ok_button = (Button) findViewById(R.id.img_ok);
        img_preview.setImageBitmap(ImageLoader.getInstance().loadImageSync(getIntent().getData().toString()));
        init();

    }
    public void  init(){
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePreviewActivity.this.finish();
            }
        });
        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK,ImagePreviewActivity.this.getIntent().setData(getIntent().getData()));
                ImagePreviewActivity.this.finish();
            }
        });

    }
}
