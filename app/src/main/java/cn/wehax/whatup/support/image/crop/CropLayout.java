package cn.wehax.whatup.support.image.crop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;


/**
 * www.denghaojie.cn
 *
 * @author howe
 */
public class CropLayout extends RelativeLayout {

    private int screenWidth;
    private int screenHeight;

    private CropImageView mZoomImageView;
    private CropBorderView mClipImageView;

    private int cropWidth;
    private int cropHeight;
    public CropLayout(Context context) {
        super(context);

        mZoomImageView = new CropImageView(context);
        mClipImageView = new CropBorderView(context);

        android.view.ViewGroup.LayoutParams lp = new LayoutParams(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            android.view.ViewGroup.LayoutParams.MATCH_PARENT);

        /**
         * 这里测试，直接写死了图片，真正使用过程中，可以提取为自定义属性
         */
        //        mZoomImageView.setImageDrawable(getResources().getDrawable(
        //            R.drawable.a));

        this.addView(mZoomImageView, lp);
        this.addView(mClipImageView, lp);

    }

    public CropLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mZoomImageView = new CropImageView(context);
        mClipImageView = new CropBorderView(context);

        android.view.ViewGroup.LayoutParams lp = new LayoutParams(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            android.view.ViewGroup.LayoutParams.MATCH_PARENT);

        /**
         * 这里测试，直接写死了图片，真正使用过程中，可以提取为自定义属性
         */
//        mZoomImageView.setImageDrawable(getResources().getDrawable(
//            R.drawable.a));

        this.addView(mZoomImageView, lp);
        this.addView(mClipImageView, lp);

    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        screenWidth = getWidth();
        screenHeight = getHeight();
        if(screenWidth>0 && screenHeight >0){
            int w = cropWidth;
            int h = cropHeight;
            int x;
            int y;


            if(cropWidth == cropHeight){
                w = h = screenWidth - 2;

            }else if (cropWidth > cropHeight){
                w = screenWidth - 2;
                h = (int) ((float)cropHeight * w / (float)cropWidth);
            }else if(cropWidth < cropHeight){
                w = (int) (screenWidth * 0.9f);
                h = (int) ((float)cropHeight * w / (float)cropWidth);
                if( h > (screenHeight * 0.8f)){
                    h = (int) (screenHeight * 0.8f);
                    w = (int) ((float)cropWidth * h / (float)cropHeight);
                }
            }

            x = (screenWidth - w) / 2;
            y = (screenHeight - h) / 2;

            mClipImageView.setSize(x, y, w, h);
            mZoomImageView.setCropSize(x , y, w , h);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override protected void onDraw(Canvas canvas) {

        Log.e("crop","宽度="+getWidth()+"   高度="+getHeight());
        super.onDraw(canvas);
    }

    @Override public void draw(Canvas canvas) {
        Log.e("crop","draw 宽度="+getWidth()+"   高度="+getHeight());
        super.draw(canvas);
    }


    /**
     * 裁切图片
     *
     * @return
     */
    public Bitmap clip() {
        return mZoomImageView.clip();
    }

    /**
     * 设置裁减区域宽高
     *
     * @param w
     * @param h
     */
    public void setCropSize(int w, int h) {
        this.cropWidth = w;
        this.cropHeight = h;
        invalidate();
    }

    public void setCropBorderWidth(int size){
        mClipImageView.setBorderWidth(size);
    }

    public void setImage(Bitmap bitmap) {
        mZoomImageView.setImageBitmap(bitmap);
        invalidate();
    }

    public void setImage(int imgResId){

        mZoomImageView.setImageDrawable(getResources().getDrawable(
                        imgResId));
    }
}
