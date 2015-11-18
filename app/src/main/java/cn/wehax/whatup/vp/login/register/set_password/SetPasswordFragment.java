package cn.wehax.whatup.vp.login.register.set_password;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import javax.inject.Inject;

import cn.wehax.util.TimeUtils;
import cn.wehax.whatup.R;
import cn.wehax.whatup.config.IntentKey;
import cn.wehax.whatup.framework.fragment.BaseFragment;
import cn.wehax.whatup.support.helper.InputCheckHelper;
import cn.wehax.whatup.support.widget.CountDownTextView;
import roboguice.inject.InjectView;


public class SetPasswordFragment extends BaseFragment {
    public final String TAG = "SetPasswordFragments";

    String phoneNumber;

    @InjectView(R.id.set_password_top_tip_tv)
    TextView topTipTv;

    @InjectView(R.id.set_password_verify_code_et)
    EditText verifyCodeEt;

    @InjectView(R.id.password_edit_text)
    EditText passwordEditText;

    /**
     * 底部按钮有四种状态
     * <ul>
     * <li>确定（可点击）</li>
     * <li>发送中（不可点击）</li>
     * <li>倒计时（不可点击）</li>
     * <li>重新发送（可点击）</li>
     * </ul>
     */
    @InjectView(R.id.set_password_bottom_btn)
    CountDownTextView bottomBtn;

    private final static Integer BTN_STATUS_POSITIVE = 1; // 确定状态
    private final static Integer BTN_STATUS_RESEND = 2; // 重新发送状态
    private final static Integer BTN_STATUS_COUNT_DOWN = 3; // 确定状态
    private final static Integer BTN_STATUS_SENDING = 4; // 发送中状态

    @Inject
    SetPasswordPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_set_password);

        phoneNumber = getArguments().getString(IntentKey.INTENT_KEY_PHONE_NUMBER);
    }

    @Override
    protected void initPresenter() {
        presenter.setView(this);
    }

    @Override
    protected void initView() {
        setTopBarTitle(R.string.register);
        topTipTv.setText(String.format(getString(R.string.remind_user_verify_code_sended), phoneNumber));

        bottomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击重新发送按钮
                if (v.getTag() == BTN_STATUS_RESEND) {
                    clickResentBtn();
                    return;
                }

                // 点击确定按钮
                if (v.getTag() == BTN_STATUS_POSITIVE) {
                    clickPositiveBtn();
                    return;
                }
            }
        });

        bottomBtn.setOnCountDownLisenter(new CountDownTextView.OnCountDownLisenter() {
            @Override
            public void onFinish() {
                // 倒计时结束，可以重新发送
                setBtnResendStatus();
            }

            @Override
            public void onTick(long millisUntilFinished) {

            }
        });

        // 监听验证码输入框内容变化
        // 根据内容变化，底部需要按钮相应变化
        verifyCodeEt.addTextChangedListener(new TextWatcher() {

            /**
             * 在向输入框中输入之后<br>
             * s为输入框中的所有文字
             */
            public void afterTextChanged(Editable s) {
                String temp = s.toString();

                // 当用户输入完整的验证码后，按钮设为确定状态
                if (temp.length() == InputCheckHelper.VERIFY_CODE_LENGTH) {
                    setBtnPositiveStatus();
                    return;
                }

                // 只有当验证码输入完整时，按钮为确定状态
                // 如果此时用户删除验证码内容，按钮需要设为重新发送状态
                if (bottomBtn.getTag() == BTN_STATUS_POSITIVE
                        && temp.length() < InputCheckHelper.VERIFY_CODE_LENGTH) {
                    setBtnResendStatus();
                }
            }

            /**
             * s:之前的文字内容<br>
             * start:添加文字的位置 <br>
             * count:一直是0<br>
             * after:此次添加的文字总数(并不是输入框中的文字的总数)<br>
             */
            public void beforeTextChanged(final CharSequence s, final int start, final int count,
                                          final int after) {
            }

            /**
             * @param s 文本框中输入的所有文字
             * @param start 添加文字的位置
             * @param before 一直是0
             * @param count 一直是0
             */
            public void onTextChanged(final CharSequence s, final int start, final int before,
                                      final int count) {
            }
        });
    }

    private void clickResentBtn() {
        presenter.requestPhoneVerifyCode(phoneNumber);
    }

    private void clickPositiveBtn() {
        String verifyCode = verifyCodeEt.getText().toString();
        String password = passwordEditText.getText().toString();
        presenter.signUpOrLoginAndSetPassword(phoneNumber, verifyCode, password);
    }

    @Override
    protected void loadData() {
        presenter.requestPhoneVerifyCode(phoneNumber);
    }

    /**
     * 设置底部按钮为确定状态
     */
    public void setBtnPositiveStatus() {
        if (bottomBtn.isCountingDown())
            bottomBtn.cancelCountDown();
        bottomBtn.setText(R.string.positive);
        bottomBtn.setTag(BTN_STATUS_POSITIVE);
        bottomBtn.setEnabled(true);
    }

    /**
     * 设置底部按钮为重新发送状态
     */
    public void setBtnResendStatus() {
        if (bottomBtn.isCountingDown())
            bottomBtn.cancelCountDown();
        bottomBtn.setText(R.string.resend);
        bottomBtn.setTag(BTN_STATUS_RESEND);
        bottomBtn.setEnabled(true);
    }

    /**
     * 设置底部按钮为发送中状态
     */
    public void setBtnSendingStatus() {
        if (bottomBtn.isCountingDown())
            bottomBtn.cancelCountDown();
        bottomBtn.setText(R.string.sending);
        bottomBtn.setTag(BTN_STATUS_SENDING);
        bottomBtn.setEnabled(false);
    }

    private final int COUNT_DOWN_TIME = 60 * 1000; // 倒计时时间
    private final int COUNT_DOWN_INTERNAL = 1000; // 倒计时时间间隔

    /**
     * 时间格式化策略
     */
    TimeUtils.ITimeFormatStrategy timeFormatStrategy = new TimeUtils.ITimeFormatStrategy() {
        @Override
        public String formatTime(long millisecond) {
            return String.format(getActivity().getString(R.string.resend_after), millisecond / TimeUtils.MILLIS_IN_SECOND);
        }
    };

    /**
     * 设置底部按钮为倒计时状态
     */
    public void setBtnCountDownStatus() {
        bottomBtn.setTag(BTN_STATUS_COUNT_DOWN);
        bottomBtn.setEnabled(false);
        bottomBtn.startCountDown(COUNT_DOWN_TIME, COUNT_DOWN_INTERNAL, timeFormatStrategy);
    }
}
