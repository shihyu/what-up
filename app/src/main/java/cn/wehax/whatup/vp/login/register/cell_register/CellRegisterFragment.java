package cn.wehax.whatup.vp.login.register.cell_register;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import javax.inject.Inject;

import cn.wehax.whatup.R;
import cn.wehax.whatup.framework.fragment.BaseFragment;
import roboguice.inject.InjectView;


public class CellRegisterFragment extends BaseFragment {
    @InjectView(R.id.phone_number_edit_text)
    EditText mPhoneNumberEditText;

    @InjectView(R.id.cell_register_next_btn)
    Button mNextBtn;

    @Inject
    CellRegisterPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_input_phone_number);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        presenter.setView(this);

    }

    @Override
    protected void initPresenter() {
        presenter.setView(this);
    }

    @Override
    protected void initView() {
        setTopBarTitle(R.string.register);

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = mPhoneNumberEditText.getText().toString();
                presenter.clickNext(phoneNumber);
            }
        });
    }


}
