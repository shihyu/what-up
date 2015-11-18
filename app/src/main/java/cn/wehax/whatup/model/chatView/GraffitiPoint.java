package cn.wehax.whatup.model.chatView;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import cn.wehax.whatup.model.chatView.GraffitiAction;

/**
 * Created by howe on 15/5/26.
 * Email:howejee@gmail.com
 */
public class GraffitiPoint extends GraffitiAction {
    public float x;
    public float y;
    public float size;

    public GraffitiPoint(float px, float py, int color,int size) {
        super(color);
        this.x = px;
        this.y = py;
        this.size = size;
        points.add(new Point((int)x,(int)y));
    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true); //抗锯齿
        paint.setDither(true);    //防抖动
        paint.setColor(color);
        paint.setStrokeWidth(size); //设置线条粗细
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND); //设置结合处为圆弧
        paint.setStrokeCap(Paint.Cap.ROUND); //笔画始末圆弧
        canvas.drawPoint(x,y,paint);
    }

    @Override
    public void move(float mx, float my) {

    }
}
