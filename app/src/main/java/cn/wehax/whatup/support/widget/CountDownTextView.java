package cn.wehax.whatup.support.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import cn.wehax.util.TimeUtils;
import cn.wehax.util.TimeUtils.ITimeFormatStrategy;


/**
 * 倒计时TextView
 */
public class CountDownTextView extends TextView {
    /**
     * 倒计时时间（单位毫秒）
     */
    private long countDownTime;

    private Timer timer;
    private TimerTask countDownTask;

    /**
     * 倒计时的时间间隔，onTick()方法回调的间隔时间（单位毫秒）
     */
    private long period = 1000;

    /**
     * 标记是否正在倒计时中
     */
    private boolean isCountingDown = false;

    /**
     * Hanlder Msg
     */
    private final int MSG_COUNT_DOWN_FINISH = 100;
    private final int MSG_UPDATE_TEXT_VIEW = 200;


    /**
     * 倒计时监听器
     */
    private OnCountDownLisenter onCountDownLisenter;

    /**
     * 时间格式化策略
     */
    private ITimeFormatStrategy timeFormatStrategy;

    /**
     * 标记onTick操作正在执行
     */
    private boolean isTickExecuting = false;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_COUNT_DOWN_FINISH) {
                // 倒计时结束
                cancelCountDown();

                if (onCountDownLisenter != null)
                    onCountDownLisenter.onFinish();

            } else if (msg.what == MSG_UPDATE_TEXT_VIEW) {
                // 更新倒计时时间
                setCountDownText(countDownTime);

                // 利用isTickExecuting标记，保证onTick方法调用同步
                // onTick方法调用完成前，不会再次被调用
                if (!isTickExecuting) {
                    isTickExecuting = true;

                    if (onCountDownLisenter != null) {
                        onCountDownLisenter.onTick(countDownTime);
                    }

                    isTickExecuting = false;
                }
            }
        }
    };

    public CountDownTextView(Context context) {
        this(context, null);
    }

    public CountDownTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        timer = new Timer();
    }

    /**
     * 设置倒计时监听器
     *
     * @param lis
     */
    public void setOnCountDownLisenter(OnCountDownLisenter lis) {
        onCountDownLisenter = lis;
    }

    /**
     * 如果正在倒计时，返回true
     *
     * @return
     */
    public boolean isCountingDown() {
        return isCountingDown;
    }

    /**
     * 开始倒计时
     */
    public void startCountDown(long millisCountdown, long countDownInterval) {
        startCountDown(millisCountdown, countDownInterval, null);
    }

    /**
     * 开始倒计时
     * <p/>
     * 特别注意<br>
     * （select_image_from_native_btn_bg_selector）调用本方法时，如果控件正在倒计时中，自动停止倒计时。<br>
     *
     * @param millis   倒计时多长时间（不小于0），单位毫秒
     * @param countDownInterval 倒计时的时间间隔，onTick()方法回调的间隔时间（单位毫秒）
     * @param strategy 时间格式化策略
     */
    public void startCountDown(long millis, long countDownInterval, ITimeFormatStrategy strategy) {
        countDownTime = millis;
        timeFormatStrategy = strategy;
        period = countDownInterval;

        if (countDownTime < 0)
            return;
        // 更新文本
        handler.sendEmptyMessage(MSG_UPDATE_TEXT_VIEW);

        // 如果正在倒计时，停止倒计时
        if (isCountingDown) {
            cancelCountDown();
        }

        isCountingDown = true;

        countDownTask = new TimerTask() {
            @Override
            public void run() {
                countDownTime -= period;

                if (countDownTime > 0) {
                    handler.sendEmptyMessage(MSG_UPDATE_TEXT_VIEW);
                } else {
                    handler.sendEmptyMessage(MSG_COUNT_DOWN_FINISH);
                    cancel();
                }
            }
        };
        timer.schedule(countDownTask, period, period);
    }

    /**
     * 取消倒计时
     * </p>
     * 特别注意，调用本方法会将控件文本内容置为空字符串！
     */
    public void cancelCountDown() {
        // 取消TimerTask
        if (countDownTask != null)
            countDownTask.cancel();

        // 移除已取消的TimerTask
        timer.purge();

        countDownTask = null;

        isCountingDown = false;

        countDownTime = 0;

        setText("");

        handler.removeCallbacksAndMessages(null);
    }

    /**
     * 设置CountDownTextView显示文本
     *
     * @param millisecond 倒计时时间
     */
    private void setCountDownText(long millisecond) {
        if (timeFormatStrategy != null) {
            setText(timeFormatStrategy.formatTime(millisecond));
        } else {
            setText(simpleFormatCountDownTime(millisecond));
        }
    }

    /**
     * 倒计时监听器
     */
    public interface OnCountDownLisenter {
        /**
         * 倒计时结束
         */
        void onFinish();

        /**
         * 固定间隔被调用
         * <p/>
         * 例如，倒计时3s，倒计时时间间隔1s，onTick()将被调用3次。<br>
         * 第一次调用，millisUntilFinished = 3000<br>
         * 第三次地用，millisUntilFinished = 1000
         *
         * @param millisUntilFinished 倒计时剩余时间
         */
        void onTick(long millisUntilFinished);
    }

    /**
     * 格式化倒计时时间（时间格式：hh:mm:ss）
     *
     * @param milliseconds 倒计时时间（单位毫秒）
     */
    private String simpleFormatCountDownTime(long milliseconds) {
        if (milliseconds < 0) {
            milliseconds = 0;
        }

        long seconds = milliseconds / 1000;
        long hh = seconds / TimeUtils.SECOND_IN_HOUR;
        long mm = seconds % TimeUtils.SECOND_IN_HOUR;
        long ss = mm % TimeUtils.SECOND_IN_MINUTE;
        mm = mm / TimeUtils.SECOND_IN_MINUTE;

        String time;
        if (hh > 0) {
            time = String.format("%02d:%02d:%02d", hh, mm, ss);
        } else if (mm > 0) {
            time = String.format("%02d:%02d", mm, ss);
        } else {
            time = String.format("%02d", ss);
        }

        return time;
    }
}