package cn.wehax.whatup.vp.setting.BubbleSetting;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.inject.Inject;

import cn.wehax.whatup.R;
import cn.wehax.whatup.framework.fragment.BaseFragment;
import roboguice.inject.InjectView;

/**
 * Created by sanchibing on 2015/6/30.
 */
public class BubbleSetFragment extends BaseFragment {
    @Inject
    BubbleSetPresenter presenter;

    @InjectView(R.id.gv_bubble_choose)
    GridView bubbleGv;


    BubbleAdapter mBubbleAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_bubble_setting);
    }

    @Override
    protected void initView() {
        setTopBarTitle(R.string.choose_bubble);
        mBubbleAdapter=new BubbleAdapter(getActivity(),presenter.selectedPosition(getActivity()));
        bubbleGv.setAdapter(mBubbleAdapter);
        bubbleGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                presenter.chooseBubble(getActivity(),position,mBubbleAdapter);

            }
        });
    }
    @Override
    protected void initPresenter() {
        presenter.setView(this);
    }

}
