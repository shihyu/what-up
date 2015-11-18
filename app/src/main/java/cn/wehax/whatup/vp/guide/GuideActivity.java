package cn.wehax.whatup.vp.guide;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.RelativeLayout;

import com.viewpagerindicator.PageIndicator;

import java.util.ArrayList;
import java.util.List;

import cn.wehax.whatup.R;
import cn.wehax.whatup.framework.fragment.WXFragmentActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_guide)
public class GuideActivity extends WXFragmentActivity {
    final String TAG = "GuideActivity";

    int drawable[][] = {{R.drawable.guide_background,R.drawable.guide_01,R.drawable.guide_text_01},
            {R.drawable.guide_background,R.drawable.guide_02,R.drawable.guide_text_02},
            {R.drawable.guide_background,R.drawable.guide_03,R.drawable.guide_text_03},
            {R.drawable.guide_background,R.drawable.guide_04,R.drawable.guide_text_04},
            {R.drawable.guide_background_05,R.drawable.guide_05}};

    @InjectView(R.id.guide_view_pager)
    ViewPager mPager;

    GuideFragmentPagerAdapter mPagerAdapter;
    List<Fragment> mFragments = new ArrayList<>();

    PageIndicator mIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prepareFragments();
        mPagerAdapter = new GuideFragmentPagerAdapter(getSupportFragmentManager(), mFragments);
        mPager.setAdapter(mPagerAdapter);

        mIndicator = (PageIndicator) findViewById(R.id.guide_view_indicator);
        mIndicator.setViewPager(mPager);
        

    }

    void prepareFragments(){

        for (int i = 0;i<drawable.length;i++){
            mFragments.add(ImageFragment.newInstance(drawable[i]));
        }
    }
}
