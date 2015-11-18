package cn.wehax.whatup.vp.chat.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * Created by howe on 15/6/16.
 * Email:howejee@gmail.com
 */
public class ChatBackgroundView extends ImageView implements ViewTreeObserver.OnGlobalLayoutListener {

    public boolean isOnce = true;

    public ChatBackgroundView(Context context) {
        super(context);
        setScaleType(ScaleType.MATRIX);
    }

    public ChatBackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setScaleType(ScaleType.MATRIX);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        if (isOnce) {
            Log.e("chat","onGlobalLayout");
            Drawable d = getDrawable();
            if (d == null) {
                return;
            }

            int width = getWidth();
            int height = getHeight();

            Log.e("chat","background width="+width+"  height="+height);

            int dw = d.getIntrinsicWidth();
            int dh = d.getIntrinsicHeight();

            float scale = Math.max((float) width / (float) dw, (float) height / (float) dh);


            Log.e("chat","after scale bg width="+(dw*scale)+"  height="+(dh*scale));
            Matrix matrix = new Matrix();

            matrix.postTranslate((width - dw) / 2, (height - dh) / 2);
            matrix.postScale(scale, scale, width / 2, height / 2);
            setImageMatrix(matrix);
//            isOnce = false;
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        isOnce = true;
        super.setImageBitmap(bm);
    }
}
