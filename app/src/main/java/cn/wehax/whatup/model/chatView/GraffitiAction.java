package cn.wehax.whatup.model.chatView;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by howe on 15/5/26.
 * Email:howejee@gmail.com
 */
public abstract class GraffitiAction {

    protected List<Point> points = new ArrayList<>();

    protected boolean isSelf = true;

    protected int color;

    GraffitiAction(){
        color = Color.BLACK;
    }

    GraffitiAction(int color){
        this.color = color;
    }

    public abstract void draw(Canvas canvas);

    public abstract void move(float mx, float my);

    public boolean isSelf() {
        return isSelf;
    }

    public void setSelf(boolean isSelf) {
        this.isSelf = isSelf;
    }


    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
