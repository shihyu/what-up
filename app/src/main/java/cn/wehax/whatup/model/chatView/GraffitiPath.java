package cn.wehax.whatup.model.chatView;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.Log;

import cn.wehax.whatup.model.chatView.GraffitiAction;

/**
 * Created by howe on 15/5/26.
 * Email:howejee@gmail.com
 */
public class GraffitiPath extends GraffitiAction {

    //当移动距离大于等于该值时才绘制
    private final int TOUCH_TOLERANCE = 4;

    Path path;
    int size;
    float lastX;
    float lastY;

    public GraffitiPath() {
        this.path = new Path();
        size = 1;
    }

    public GraffitiPath(float x, float y,  int color, int size) {
        super(color);
        this.path = new Path();
        this.size = size;
        path.moveTo(x, y);
        this.lastX = x;
        this.lastY = y;
        points.add(new Point((int)x,(int)y));
    }

    @Override
    public void draw(Canvas canvas) {
        Log.e("chat","画一条线：颜色="+color+"  点："+path.toString());
        Paint paint = new Paint();
        paint.setAntiAlias(true); //抗锯齿
        paint.setDither(true);    //防抖动
        paint.setColor(color);
        paint.setStrokeWidth(size); //设置线条粗细
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND); //设置结合处为圆弧
        paint.setStrokeCap(Paint.Cap.ROUND); //笔画始末圆弧
        canvas.drawPath(path,paint);
    }

    @Override
    public void move(float x, float y) {
        float dx = Math.abs(x - lastX);
        float dy = Math.abs(y - lastY);

        if(dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE){
            path.quadTo(lastX,lastY,(lastX + x)/2,(lastY + y)/2);
            lastX = x;
            lastY = y;
            points.add(new Point((int)x,(int)y));
        }


    }



}
