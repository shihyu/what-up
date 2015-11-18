package cn.wehax.whatup.vp.guide;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import javax.inject.Inject;

import cn.wehax.whatup.R;
import cn.wehax.whatup.support.helper.MoveToHelper;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 * 仅包含一个ImageView的Fragment
 */
public class ImageFragment extends RoboFragment {
    public static final String KEY_IMAGE_RES_ID = "KEY_IMAGE_RES_ID";

    int[] mImageResID;

    ImageView background;
    ImageView backgroundText;
    ImageView backgroundImage;
    Button mEnterBtn;
    @Inject
    LastPagePresenter mPresenter;

    public static ImageFragment newInstance(int[] imageResId){
        Bundle bundle = new Bundle();
        bundle.putIntArray(KEY_IMAGE_RES_ID, imageResId);

        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // mImageResID = getArguments().getInt(KEY_IMAGE_RES_ID);
        mImageResID = getArguments().getIntArray(KEY_IMAGE_RES_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootLayout =View.inflate(getActivity(),R.layout.guide_image,null);
        background = (ImageView)rootLayout.findViewById(R.id.background_image);
        backgroundText = (ImageView)rootLayout.findViewById(R.id.background_text);
        backgroundImage = (ImageView)rootLayout.findViewById(R.id.background_phone_image);
        mEnterBtn = (Button)rootLayout.findViewById(R.id.fragment_last_page_enter_btn);
        return rootLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter.setView(this);
//        mImageView.setImageResource(mImageResID);
        if (mImageResID.length == 3) {
            backgroundText.setVisibility(View.VISIBLE);
            mEnterBtn.setVisibility(View.INVISIBLE);
            background.setImageResource(mImageResID[0]);
            backgroundImage.setImageResource(mImageResID[1]);
            backgroundText.setImageResource(mImageResID[2]);
        }else if (mImageResID.length == 2){
            background.setImageResource(mImageResID[0]);
            backgroundImage.setImageResource(mImageResID[1]);
            mEnterBtn.setVisibility(View.VISIBLE);
            backgroundText.setVisibility(View.INVISIBLE);

        }
        mEnterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.chooseMoveTo();
            }
        });

    }

    public void moveToChooseLogin() {
        MoveToHelper.moveToChooseLoginView(getActivity());
        getActivity().finish();
    }

    public void moveToMainView() {
//        MoveToHelper.moveToMainView(getActivity());
//        getActivity().finish();
    }
}
