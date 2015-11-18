package cn.wehax.whatup.vp.login.login;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;

import cn.wehax.whatup.R;
import cn.wehax.whatup.framework.fragment.BaseFragment;
import cn.wehax.whatup.model.user.UserManager;
import cn.wehax.whatup.support.helper.CommonHelper;
import cn.wehax.whatup.support.helper.InputCheckHelper;
import cn.wehax.whatup.support.helper.MoveToHelper;
import roboguice.inject.InjectView;

/**
 * 登录页面
 */
public class LoginFragment extends BaseFragment {
    @InjectView(R.id.phone_number_edit_text)
    EditText mPhoneNumberEditText;

    @InjectView(R.id.password_edit_text)
    EditText mPasswordEditText;

    @InjectView(R.id.login_btn)
    Button mLoginBtn;

    @InjectView(R.id.forget_password_text_view)
    TextView mForgetPasswordBtn;

    @InjectView(R.id.login_qq_btn)
    ImageView mQQLoginBtn;

    @InjectView(R.id.login_weixin_btn)
    ImageView mWeiXinLoginBtn;

    @InjectView(R.id.login_sina_btn)
    ImageView mSinaLoginBtn;

    @Inject
    LoginPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);
    }

    @Override
    protected void initPresenter() {
        presenter.init(this, getArguments());
    }

    @Override
    protected void initView() {
        setTopBarTitle(R.string.login_with_space);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 检查电话号码
                String phoneNumber = mPhoneNumberEditText.getText().toString();
                if (!InputCheckHelper.checkInputPhoneNumber(getActivity(), phoneNumber)) {
                    return;
                }

                // 检查密码
                String password = mPasswordEditText.getText().toString();
                if (!InputCheckHelper.checkInputPassword(getActivity(), password)) {
                    return;
                }

                // 检查网络
                if (!CommonHelper.checkNetworkAvailability(getActivity())) {
                    return;
                }

                presenter.cellLogin(phoneNumber, password);
            }
        });

        mForgetPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoveToHelper.moveToRetrievePasswordView(getActivity());
            }
        });

        mQQLoginBtn.setOnClickListener(onClickThirdPartyLoginListener);
        mWeiXinLoginBtn.setOnClickListener(onClickThirdPartyLoginListener);
        mSinaLoginBtn.setOnClickListener(onClickThirdPartyLoginListener);
    }

    /**
     * 第三方登录按钮点击事件
     */
    View.OnClickListener onClickThirdPartyLoginListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int platform = -1;

            switch (v.getId()) {
                case R.id.login_qq_btn:
                    platform = UserManager.PLATFORM_QQ;
                    break;
                case R.id.login_weixin_btn:
                    platform = UserManager.PLATFORM_WEIXIN;
                    break;
                case R.id.login_sina_btn:
                    platform = UserManager.PLATFORM_SINA;
                    break;
            }

            presenter.thirdPartyLogin(platform);
        }
    };

    public void returnToRelationView() {
        getActivity().setResult(Activity.RESULT_OK);
    }
}
