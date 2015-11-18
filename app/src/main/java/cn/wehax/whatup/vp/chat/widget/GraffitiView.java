package cn.wehax.whatup.vp.chat.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import cn.wehax.whatup.model.chatView.GraffitiAction;
import cn.wehax.whatup.model.chatView.GraffitiPath;
import cn.wehax.whatup.model.chatView.GraffitiPoint;

/**
 * Created by howe on 15/5/26.
 * Email:howejee@gmail.com
 */
public class GraffitiView extends SurfaceView implements SurfaceHolder.Callback {

    private static final int GRAFFITI_DEFAULT_WIDTH = 5;

    private static final int GRAFFITI_DEFAULT_COLOR = Color.RED;

    private SurfaceHolder mSurfaceHolder = null;

    //当前所选画笔的形状
    private GraffitiAction curAction = null;

    //当前画笔颜色
    private int curColor = Color.BLACK;

    //当前画笔粗线
    private int curSize = 15;

    //记录笔画的列表
    private List<GraffitiAction> mActions = new ArrayList<>();


    private boolean isDrawRemoteRunning = true;


    private DrawRemoteThread drawRemoteThread;

    private OnGraffitiListener graffitiListener;

    public GraffitiView(Context context) {
        super(context);
        init();
    }

    public GraffitiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GraffitiView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {

        this.getHolder().addCallback(this);

        //背景透明
        this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        this.setZOrderOnTop(true);

        this.setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e("chat", "surfaceCreated");
        mSurfaceHolder = holder;

        drawRemoteThread = new DrawRemoteThread();
        drawRemoteThread.start();

        if(graffitiListener != null){
            graffitiListener.onViewCreated();
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e("chat", "surfaceChanged");
        drawAction();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mSurfaceHolder = null;
        isDrawRemoteRunning = false;
        Log.e("chat", "surfaceDestroyed");
    }


    public void refresh() {
        if (mSurfaceHolder != null)
            drawAction();
    }

    public void clear() {
        mActions.clear();
        if (mSurfaceHolder != null)
            drawAction();
    }



    float lastTouchDownX;
    float lastTouchDownY;
    boolean isPoint = true;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_CANCEL) {
            return false;
        }

        float touchX = event.getX();
        float touchY = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                lastTouchDownX = touchX;
                lastTouchDownY = touchY;
                curAction = new GraffitiPath(touchX, touchY, curColor, curSize);
                Log.e("chat", "keyDown:x=" + touchX + "  y=" + touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                if (isPoint && touchX == lastTouchDownX && touchY == lastTouchDownY) {
                    isPoint = true;
                } else {
                    isPoint = false;
                    drawAction(curAction, touchX, touchY);
                }

                break;

            case MotionEvent.ACTION_UP:
                if (isPoint) {
                    curAction = new GraffitiPoint(touchX, touchY, curColor, curSize);
                    drawAction(curAction, touchX, touchY);
                }

                if (curAction != null) {
                    mActions.add(curAction);
                    if (graffitiListener != null) {
                        graffitiListener.onDrawEnd(curAction);
                    }
                }
                Log.e("chat", "up x=" + touchX + "  y=" + touchY);
                isPoint = true;
                curAction = null;
                break;
            default:
                break;
        }

        return super.onTouchEvent(event);
    }

    private void drawAction() {
        drawAction(null, 0, 0);
    }

    private void drawAction(GraffitiAction action, float x, float y) {
        if(mSurfaceHolder == null){
            return;
        }
        Canvas canvas = mSurfaceHolder.lockCanvas();
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        Log.e("chat","涂鸦数量："+mActions.size());
        for (GraffitiAction a : mActions) {
            a.draw(canvas);
        }
        if (action != null) {
            action.move(x, y);
            action.draw(canvas);
        }
        mSurfaceHolder.unlockCanvasAndPost(canvas);
    }


    public void setColor(int color) {
        this.curColor = color;
    }

    public void setSize(int size) {
        this.curSize = size;
    }


    public void drawRemote(List<Point> points, int color) {
        if (drawRemoteThread != null) {
            drawRemoteThread.drawPoints(points, color);
        }
    }

    public void drawLocal(List<GraffitiAction> list) {
        mActions.clear();
        mActions.addAll(list);
        if (mSurfaceHolder != null)
            drawAction();
    }

    /**
     * 撤销对方的涂鸦(收到对方撤销命令后调用)
     */
    public void revokeRemote() {

        removeLastGraffiti(false);
    }

    /**
     * 撤销自己的涂鸦
     */
    public void revokeLocal() {
        boolean success = removeLastGraffiti(true);
        if (success && graffitiListener != null) {
            graffitiListener.onLocalRevoke();
        }
    }

    private boolean removeLastGraffiti(boolean isSelf) {
        if (!mActions.isEmpty()) {
            synchronized (mActions) {
                int i = mActions.size() - 1;
                for (; i > -1; i--) {
                    GraffitiAction action = mActions.get(i);
                    if (action.isSelf() == isSelf) {
                        mActions.remove(i);
                        drawAction();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private class DrawRemoteThread extends Thread {
        Queue<List<Point>> waitingQueue = new LinkedList<>();
        Queue<Integer> colorQueue = new LinkedList<>();

        public DrawRemoteThread() {

        }

        public synchronized void drawPoints(List<Point> points, int color) {
            waitingQueue.offer(points);
            colorQueue.offer(color);
            this.notify();
        }

        @Override
        public void run() {
            while (isDrawRemoteRunning) {
                List<Point> points = waitingQueue.poll();
                if (points != null && !points.isEmpty()) {
                    int color = colorQueue.poll();
                    if (points.size() == 1) {
                        //画点
                        GraffitiPoint gPoint = new GraffitiPoint(points.get(0).x, points.get(0).y, color, GRAFFITI_DEFAULT_WIDTH);
                        gPoint.setSelf(false);
                        drawAction(gPoint, 0, 0);
                        mActions.add(gPoint);

                    } else {
                        //画路径
                        GraffitiPath path = null;
//
                        for (Point p : points) {
                            if (path == null) {
                                path = new GraffitiPath(p.x, p.y, color, GRAFFITI_DEFAULT_WIDTH);
                                path.setSelf(false);
                            } else {
                                drawAction(path, p.x, p.y);
                                try {
                                    Thread.sleep(8);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        if (path != null) {
                            mActions.add(path);
                        }
                    }

                } else {

                    try {
                        synchronized (this) {
                            wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    public void setOnDrawListener(OnGraffitiListener listener) {
        this.graffitiListener = listener;
    }

    public interface OnGraffitiListener {
        public void onViewCreated();

        public void onDrawEnd(GraffitiAction action);

        public void onLocalRevoke();
    }


}
