package cn.wehax.whatup.support.image.crop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

/**
 * @author howe
 *         http:www.denghaojie.cn
 */
public class CropBorderView extends View {

    /**
     * 绘制的矩形x坐标
     */
    private int x;

    /**
     * 绘制的矩形y坐标
     */
    private int y;

    /**
     * 绘制的矩形的宽度
     */
    private int width;

    /**
     * 绘制的矩形的宽度
     */
    private int height;


    /**
     * 边框的颜色，默认为白色
     */
    private int mBorderColor = Color.parseColor("#FFFFFF");

    /**
     * 边框的宽度 单位dp
     */
    private int mBorderWidth = 1;

    private Paint mPaint;

    public CropBorderView(Context context) {
        this(context, null);
    }

    public CropBorderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropBorderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        Log.e("crop", "宽度=" + getWidth() + "   高度=" + getHeight());
        //绘制遮罩层
        mPaint.setColor(Color.parseColor("#aa333333"));
        mPaint.setStyle(Style.FILL);
        // 绘制左边1
        canvas.drawRect(0, 0, x, getHeight(), mPaint);
        // 绘制右边2
        canvas.drawRect(getWidth() - x, 0, getWidth(), getHeight(), mPaint);
        // 绘制上边3
        canvas.drawRect(x, 0, getWidth() - x,
            y, mPaint);
        // 绘制下边4
        canvas.drawRect(x, getHeight() - y,
            getWidth() - x, getHeight(), mPaint);
        // 绘制外边框
        mPaint.setColor(mBorderColor);
        mPaint.setStrokeWidth(mBorderWidth);
        mPaint.setStyle(Style.STROKE);
        canvas.drawRect(x, y, getWidth()
            - x, getHeight() - y, mPaint);

    }


    public void setBorderWidth(int size){
        this.mBorderWidth = size;
    }

    public void setSize(int x, int y, int w, int h) {
        this.width = w;
        this.height = h;
        this.x = x;
        this.y = y;
    }

}
