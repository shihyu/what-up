package cn.wehax.whatup.vp.login.choose;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.wehax.whatup.R;
import cn.wehax.whatup.framework.fragment.WXActivity;
import cn.wehax.whatup.support.helper.MoveToHelper;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * 选择登录或者选择注册或者选择忘记密码页面
 */
@ContentView(R.layout.activity_choose_login_or_register)
public class ChooseLoginOrRegisterActivity extends WXActivity {
    @InjectView(R.id.choose_view_register_btn)
    Button mRegisterBtn;

    @InjectView(R.id.choose_view_forget_password_text_view)
    TextView mForgetPasswordTv;

    @InjectView(R.id.choose_view_loign_text_view)
    TextView mLoginTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoveToHelper.moveToRegisterView(ChooseLoginOrRegisterActivity.this);
            }
        });

        mForgetPasswordTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoveToHelper.moveToRetrievePasswordView(ChooseLoginOrRegisterActivity.this);
            }
        });

        mLoginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoveToHelper.moveToLoginView(ChooseLoginOrRegisterActivity.this);
            }
        });
    }

}
