package cn.wehax.whatup.vp.main.preview_and_edit_status;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import javax.inject.Inject;

import cn.wehax.util.StringUtils;
import cn.wehax.util.SystemUtil;
import cn.wehax.whatup.R;
import cn.wehax.whatup.framework.fragment.BaseFragment;
import cn.wehax.whatup.support.helper.ImageLoaderHelper;
import roboguice.inject.InjectView;


public class PreviewAndEditStatusFragment extends BaseFragment {
    public static final String TAG = "PreviewAndEditStatusFragment";

    @InjectView(R.id.status_image_view)
    ImageView statusIv;

    @InjectView(R.id.return_image_view)
    ImageView returnIv;

    @InjectView(R.id.text_image_view)
    ImageView textIv;

    @InjectView(R.id.send_image_view)
    ImageView sendIv;

    @InjectView(R.id.status_text)
    EditText statusText;

    @Inject
    PreviewAndEditStatusPresenter presenter;

    DisplayImageOptions options;
    int statusBarHeight;
    int screenWidth;
    int screenHeight;

    @Inject
    InputMethodManager inputMethodManager;

    Rect statusTextViewPosition; // 保存状态文本框位置，特别的：rect.left = DEFAULT_LEFT表示未移动
    final int DEFAULT_LEFT = -1; // 默认矩形Left值
    final String EXTRA_STATUS_TEXT_POSITION = "EXTRA_STATUS_TEXT_POSITION";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_preview_and_edit_status);

        if (savedInstanceState != null) {
            statusTextViewPosition = savedInstanceState.getParcelable(EXTRA_STATUS_TEXT_POSITION);
        } else {
            statusTextViewPosition = new Rect(DEFAULT_LEFT, 0, 0, 0);
        }

        options = new DisplayImageOptions.Builder()
                .cacheInMemory(false)
                .cacheOnDisk(false)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        statusBarHeight = SystemUtil.getStatusBarHeight(getActivity());

        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels - statusBarHeight;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e(TAG, "onSaveInstanceState");
        outState.putParcelable(EXTRA_STATUS_TEXT_POSITION, statusTextViewPosition);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
    }

    @Override
    protected void initPresenter() {
        presenter.init(this, getArguments());
    }

    @Override
    protected void initTopBar() {
        super.initTopBar();
        hideTopBar();
    }

    private boolean isStatusTextShow() {
        if (statusText.getVisibility() == View.VISIBLE) {
            return true;
        } else {
            return false;
        }
    }

    private void showStatusText() {
        statusText.setVisibility(View.VISIBLE);
        if (statusTextViewPosition.left != DEFAULT_LEFT) {
            statusText.layout(statusTextViewPosition.left,
                    statusTextViewPosition.top,
                    statusTextViewPosition.right,
                    statusTextViewPosition.bottom);
        }
    }

    private void hideStatusText() {
        statusText.setVisibility(View.INVISIBLE);
    }

    /**
     * @return
     */
    private String getStatusTextCoord() {
        int[] location = new int[2];
        statusText.getLocationOnScreen(location);
        String coord = "{\"x\":" + 1.0f * location[0] / screenWidth + ",\"y\":" + 1.0f * location[1] / screenHeight + "}";
        Log.e(TAG, "getStatusTextCoord=" + coord);
        return coord;
    }

    @Override
    protected void initView() {
        setTopBarTitle(R.string.retrieve_password);

        returnIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().setResult(Activity.RESULT_CANCELED);
                getActivity().finish();
            }
        });

        textIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStatusTextShow()) {
                    hideStatusText();
                } else {
                    showStatusText();
                }
            }
        });

        sendIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStatusTextShow()) {
                    String text = statusText.getText().toString();
                    if (!StringUtils.isNullOrEmpty(text)) {
                        presenter.returnStatusData(text, getStatusTextCoord());
                        return;
                    }
                }

                presenter.returnStatusData();
            }
        });
        statusText.setOnTouchListener(new MyTouchListener());
    }

    /**
     * 自定义的Touch事件监听器<br>
     * <p/>
     * <p>具有以下功能：</p>
     * （1）移动控件<br>
     * 移动思路2：A点按下，移动到B，在移动到C，抬起<br>
     * （一）用户手指从A点移动到B点，控件跟着移动。在移动过程中，按下点在控件中的位置肯定保持不变。<br>
     * （二）如果我们知道（1）B点屏幕坐标（2）按下点在控件中的相对位置，不难算出控件位置<br>
     * <p/>
     * （2）监听点击事件<br>
     */
    public class MyTouchListener implements View.OnTouchListener {
        private float downRelativeX; // 按下点在控件中的相对位置
        private float downRelativeY;

        private float downRawX; // 按下点屏幕坐标
        private float downRawY;

        boolean isMoved; // 标记是否移动
        private int MOVE_AND_CLICK_THRESHOLD = 10; // 判断点击与移动时间阀值

        int left, top, right, bottom; // 控件位置

        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
//                    Log.e(TAG, "ACTION_DOWN");
                    downRelativeX = event.getX();
                    downRelativeY = event.getY();

                    downRawX = event.getRawX();
                    downRawY = event.getRawY();
//                    Log.e(TAG, "downRaw=（" + downRawX + "," + downRawY + ")");

                    isMoved = false;
                    break;
                case MotionEvent.ACTION_MOVE:
//                    Log.e(TAG, "ACTION_MOVE");
//                    Log.e(TAG, "event=（" + event.getRawX() + "," + event.getRawY() + ")");

                    // 计算手指当前位置和按下位置的距离
                    float dx = event.getRawX() - downRawX;
                    float dy = event.getRawY() - downRawY;

                    // 如果移动距离大于阀值，则认为用户希望移动控件
                    // 此时，移动控件
                    if (isMoved || dx * dx + dy * dy > MOVE_AND_CLICK_THRESHOLD) {
                        isMoved = true;

                        // 计算控件位置
                        left = (int) (event.getRawX() - downRelativeX);
                        top = (int) (event.getRawY() - statusBarHeight - downRelativeY);
                        right = left + v.getWidth();
                        bottom = top + v.getHeight();

                        // 检查控件位置是否正确，如果不正确，修改之
                        if (left < 0) {
                            left = 0;
                            right = left + v.getWidth();
                        }

                        if (right > screenWidth) {
                            right = screenWidth;
                            left = right - v.getWidth();
                        }

                        if (top < 0) {
                            top = 0;
                            bottom = top + v.getHeight();
                        }

                        if (bottom > screenHeight) {
                            bottom = screenHeight;
                            top = bottom - v.getHeight();
                        }

                        // 设置控件位置
                        v.layout(left, top, right, bottom);
                    }
                    break;
                case MotionEvent.ACTION_UP:
//                    Log.e(TAG, "ACTION_UP");
                    // 当手指抬起时，如果没有移动控件，说明是点击行为
                    if (!isMoved) {
                        onClick();
                    } else {
                        statusTextViewPosition.set(left, top, right, bottom);
                    }
                    break;
            }

            return true;
        }

        private void onClick() {
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 设置状态图片
     *
     * @param statusImagePath
     */
    public void setStatusImage(String statusImagePath) {
        Log.e(TAG, "setStatusImage/statusImagePath=" + statusImagePath);
        Log.e(TAG, "setStatusImage/uri=" + ImageLoaderHelper.buildSDCardImageUri(statusImagePath));
        ImageLoader.getInstance().displayImage(ImageLoaderHelper.buildSDCardImageUri(statusImagePath), statusIv, options);
    }
}
