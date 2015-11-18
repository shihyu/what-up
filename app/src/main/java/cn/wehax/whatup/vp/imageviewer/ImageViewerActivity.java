package cn.wehax.whatup.vp.imageviewer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.L;

import java.util.List;

import cn.wehax.whatup.R;
import cn.wehax.whatup.framework.fragment.WXActivity;
import cn.wehax.whatup.support.imageviewtouch.ImageViewTouch;
import roboguice.inject.InjectView;

/**
 * 浏览图片活动页
 * <p>说明：</p>
 * (1)仿微信设计<br>
 * (2)目前仅支持浏览网络图片，待完善
 */
public class ImageViewerActivity extends WXActivity {
    /**
     * 待浏览图片URLs
     */
    public final static String KEY_IMG_URLS = "KEY_IMG_URLS";
    /**
     * 待浏览图片索引
     */
    public final static String KEY_POSITON = "KEY_POSITON";

    @InjectView(R.id.view_pager)
    private ViewPager viewPager;
    /**
     * 当前正在显示的图片索引，从0开始
     */
    private int curPosition;
    /**
     * 待浏览图片URLs
     */
    private String[] imgUrls;

    /**
     * 保存ViewPager每一页使用的View
     */
    private List<FrameLayout> mViewPagerViewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        curPosition = getIntent().getExtras().getInt(KEY_POSITON);
        imgUrls = getIntent().getExtras().getStringArray(KEY_IMG_URLS);


            viewPager.setAdapter(new ImageViewPagerAdapter());
            viewPager.setCurrentItem(curPosition);
            viewPager.setOnPageChangeListener(new OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    curPosition = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }

        public class ImageViewPagerAdapter extends PagerAdapter {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory(false)
                    .cacheOnDisk(false)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();

            @Override
            public int getCount() {
                return imgUrls.length;
            }

            @Override
            public Object instantiateItem(View container, int position) {
                L.e("mIntroImgs[i]:" + imgUrls[position]);

                View root =  LayoutInflater.from(ImageViewerActivity.this).
                    inflate(R.layout.item_image_viewer, null);
            ImageViewTouch imageView = (ImageViewTouch) root
                    .findViewById(R.id.zoom_image_view);
            imageView.setFitToScreen(true);
            // 浏览图片时，单击图片关闭浏览
            imageView.setSingleTapListener(new ImageViewTouch.OnImageViewTouchSingleTapListener() {

                @Override
                public void onSingleTap() {
                    ImageViewerActivity.this.finish();
                }
            });
            ImageLoader.getInstance().displayImage(imgUrls[position], imageView, options);

            ((ViewPager) container).addView(root);
            return root;
        }

        @Override
        public boolean isViewFromObject(View paramView, Object paramObject) {
            return paramView == paramObject;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }
    }

}
