package cn.wehax.whatup.vp.chat.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.LogUtil;

import cn.wehax.util.DensityUtil;
import cn.wehax.whatup.R;

/**
 * Created by howe on 15/6/18.
 * Email:howejee@gmail.com
 */
public class BubbleView extends RelativeLayout {



    public static class BubbleStyle{
        public static final String BUBBLE_HAMBURG = "bubbleHamburg";
        public static final String BUBBLE_CAT = "bubbleCat";
        public static final String BUBBLE_PIG = "bubblePig";
        public static final String BUBBLE_BULB = "bubbleBulb";
        public static final String BUBBLE_TONY = "bubbleTony";
        public static final String BUBBLE_STEVE = "bubbleSteve";
        public static final String BUBBLE_THOR = "bubbleThor";
        public static final String BUBBLE_KITTY = "bubbleKitty";
        public static final String BUBBLE_HULK = "bubbleHulk";
        public static final String BUBBLE_DORAEMON = "bubbleDoraemon";
        public static final String BUBBLE_PANDA = "bubblePanda";
        public static final int[] hamburg ={
                R.drawable.bubble_hamburg_border_left,
                R.drawable.bubble_hamburg_icon_tail_left,
                R.drawable.bubble_hamburg_border_right,
                R.drawable.bubble_hamburg_icon_tail_right,
                R.drawable.bubble_hamburg_bg,
                R.color.hamburg_text_color
        };

        public static final int[] bulb ={
                R.drawable.bubble_bulb_border_left,
                R.drawable.bubble_bulb_icon_tail_left,
                R.drawable.bubble_bulb_border_right,
                R.drawable.bubble_bulb_icon_tail_right,
                R.drawable.bubble_bulb_bg,
                R.color.bulb_text_color
        };

        public static final int[] cat ={
                R.drawable.bubble_cat_border_left,
                R.drawable.bubble_cat_icon_tail_left,
                R.drawable.bubble_cat_border_right,
                R.drawable.bubble_cat_icon_tail_right,
                R.drawable.bubble_cat_bg,
                R.color.cat_text_color
        };

        public static final int[] doraemon ={
                R.drawable.bubble_doraemon_border_left,
                R.drawable.bubble_doraemon_icon_tail_left,
                R.drawable.bubble_doraemon_border_right,
                R.drawable.bubble_doraemon_icon_tail_right,
                R.drawable.bubble_doraemon_bg,
                R.color.doraemon_text_color
        };

        public static final int[] hulk ={
                R.drawable.bubble_hulk_border_left,
                R.drawable.bubble_hulk_icon_tail_left,
                R.drawable.bubble_hulk_border_right,
                R.drawable.bubble_hulk_icon_tail_right,
                R.drawable.bubble_hulk_bg,
                R.color.hulk_text_color
        };

        public static final int[] kitty ={
                R.drawable.bubble_kitty_border_left,
                R.drawable.bubble_kitty_icon_tail_left,
                R.drawable.bubble_kitty_border_right,
                R.drawable.bubble_kitty_icon_tail_right,
                R.drawable.bubble_kitty_bg,
                R.color.kitty_text_color
        };

        public static final int[] panda ={
                R.drawable.bubble_panda_border_left,
                R.drawable.bubble_panda_icon_tail_left,
                R.drawable.bubble_panda_border_right,
                R.drawable.bubble_panda_icon_tail_right,
                R.drawable.bubble_panda_bg,
                R.color.panda_text_color
        };

        public static final int[] pig ={
                R.drawable.bubble_pig_border_left,
                R.drawable.bubble_pig_icon_tail_left,
                R.drawable.bubble_pig_border_right,
                R.drawable.bubble_pig_icon_tail_right,
                R.drawable.bubble_pig_bg,
                R.color.pig_text_color
        };

        public static final int[] steve ={
                R.drawable.bubble_steve_border_left,
                R.drawable.bubble_steve_icon_tail_left,
                R.drawable.bubble_steve_border_right,
                R.drawable.bubble_steve_icon_tail_right,
                R.drawable.bubble_steve_bg,
                R.color.steve_text_color
        };

        public static final int[] thor ={
                R.drawable.bubble_thor_border_left,
                R.drawable.bubble_thor_icon_tail_left,
                R.drawable.bubble_thor_border_right,
                R.drawable.bubble_thor_icon_tail_right,
                R.drawable.bubble_thor_bg,
                R.color.thor_text_color
        };

        public static final int[] tony ={
                R.drawable.bubble_tony_border_left,
                R.drawable.bubble_tony_icon_tail_left,
                R.drawable.bubble_tony_border_right,
                R.drawable.bubble_tony_icon_tail_right,
                R.drawable.bubble_tony_bg,
                R.color.tony_text_color
        };

        public static int[] getStyle(String styleStr){
            if(BUBBLE_BULB.equalsIgnoreCase(styleStr)){
                return bulb;
            }else if(BUBBLE_CAT.equalsIgnoreCase(styleStr)){
                return cat;
            }else if(BUBBLE_DORAEMON.equalsIgnoreCase(styleStr)){
                return doraemon;
            }else if(BUBBLE_HULK.equalsIgnoreCase(styleStr)){
                return hulk;
            }else if(BUBBLE_KITTY.equalsIgnoreCase(styleStr)){
                return kitty;
            }else if(BUBBLE_PANDA.equalsIgnoreCase(styleStr)){
                return panda;
            }else if(BUBBLE_PIG.equalsIgnoreCase(styleStr)){
                return pig;
            }else if(BUBBLE_STEVE.equalsIgnoreCase(styleStr)){
                return steve;
            }else if(BUBBLE_THOR.equalsIgnoreCase(styleStr)){
                return thor;
            }else if(BUBBLE_TONY.equalsIgnoreCase(styleStr)){
                return tony;
            }else{
                return hamburg;
            }
        }
    }

    //dp
    public static final int TEXT_PADDING_VERTICAL = 8;
    public static final int TEXT_PADDING_HORIZONTAL = 13;
    public static final int TEXT_MARGIN_HORIZONTAL = 27;
    public static final int TEXT_MARGIN_VERTICAL = 28;

    public static final int POSITION_LEFT = 1;
    public static final int POSITION_RIGHT = 2;

    private int position = POSITION_LEFT;

    private int contentBgId;

    private int borderId;

    private int defaultAvatarId;

    private int avatarId;

    private int tailId;

    private int textColor;

    private int textSize;

    TextView contentText;

    ImageView avatarImg;

    ImageView borderImg;

    ImageView tailImg;

    FrameLayout avatarLayout;

    Context context;


    public BubbleView(Context context) {
        super(context);
        init(context);
    }

    public BubbleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.bubbleView);
        contentBgId = a.getResourceId(R.styleable.bubbleView_contentBackground,R.drawable.bubble_tony_bg);
        borderId = a.getResourceId(R.styleable.bubbleView_avatarBorder,R.drawable.bubble_tony_border_left);
        defaultAvatarId = a.getResourceId(R.styleable.bubbleView_avatar,R.drawable.default_avatar);
        avatarId = defaultAvatarId;
        tailId = a.getResourceId(R.styleable.bubbleView_tail,R.drawable.bubble_tony_icon_tail_left);
        textColor = a.getColor(R.styleable.bubbleView_textColor, 0xff000000);
        textSize = a.getDimensionPixelSize(R.styleable.bubbleView_textSize,DensityUtil.sp2px(context,14));
        init(context);
    }

    private void init(Context context){
        this.context = context;
        //初始化文本框
        contentText = new TextView(context);
        contentText.setId(R.id.bubble_content);
        contentText.setMinWidth(DensityUtil.dp2px(context,100));
        int paddingH = DensityUtil.dp2px(context,TEXT_PADDING_HORIZONTAL);
        int paddingV = DensityUtil.dp2px(context, TEXT_PADDING_VERTICAL);
        contentText.setPadding(paddingH,paddingV,paddingH,paddingV);


        //初始化头像边框
        borderImg = new ImageView(context);


        avatarImg = new ImageView(context);


        tailImg = new ImageView(context);

        avatarLayout = new FrameLayout(context);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void onViewLayout(){
        if(position == POSITION_LEFT) {
            RelativeLayout.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.leftMargin = DensityUtil.dp2px(context, TEXT_MARGIN_HORIZONTAL);
            lp.topMargin = DensityUtil.dp2px(context, TEXT_MARGIN_VERTICAL);
            this.addView(contentText, lp);


            //添加头像
            int avatarSize = DensityUtil.dp2px(context, 36);
            FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(avatarSize, avatarSize);
            flp.gravity = Gravity.CENTER;
            flp.topMargin = DensityUtil.dp2px(context, 1);

            avatarLayout.addView(avatarImg, flp);

            //添加头像边框
            int borderSize = DensityUtil.dp2px(context, 54);
            flp = new FrameLayout.LayoutParams(borderSize, borderSize);
            avatarLayout.addView(borderImg, flp);

            lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.addRule(ALIGN_PARENT_LEFT);
            this.addView(avatarLayout, lp);

            //添加尾部
            int tailWidth = DensityUtil.dp2px(context, 36);
            int tailHeight = DensityUtil.dp2px(context, 51);
            lp = new LayoutParams(tailWidth, tailHeight);
            lp.addRule(RIGHT_OF, R.id.bubble_content);
            lp.leftMargin = -DensityUtil.dp2px(context, 18);
            lp.topMargin = DensityUtil.dp2px(context, 8);
            this.addView(tailImg, lp);

        }else{
            RelativeLayout.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.rightMargin = DensityUtil.dp2px(context, TEXT_MARGIN_HORIZONTAL);
            lp.topMargin = DensityUtil.dp2px(context, TEXT_MARGIN_VERTICAL);
            lp.addRule(ALIGN_PARENT_RIGHT);
            this.addView(contentText, lp);


            //添加头像
            int avatarSize = DensityUtil.dp2px(context, 36);
            FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(avatarSize, avatarSize);
            flp.gravity = Gravity.CENTER;
            flp.topMargin = DensityUtil.dp2px(context, 1);
            avatarLayout.addView(avatarImg, flp);

            //添加头像边框
            int borderSize = DensityUtil.dp2px(context, 54);
            flp = new FrameLayout.LayoutParams(borderSize, borderSize);
            avatarLayout.addView(borderImg, flp);

            lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.addRule(ALIGN_PARENT_RIGHT);
            this.addView(avatarLayout, lp);

            //添加尾部
            int tailWidth = DensityUtil.dp2px(context, 36);
            int tailHeight = DensityUtil.dp2px(context, 51);
            lp = new LayoutParams(tailWidth, tailHeight);
            lp.addRule(LEFT_OF, R.id.bubble_content);
            lp.rightMargin = -DensityUtil.dp2px(context, 18);
            lp.topMargin = DensityUtil.dp2px(context, 8);
            this.addView(tailImg, lp);
       /*     Button button = new Button(context);
            RelativeLayout.LayoutParams buttonLoyout = lp;
            buttonLoyout.addRule(ABOVE,R.id.bubble_content);
            buttonLoyout.addRule(ALIGN_PARENT_RIGHT);
            button.setText("真心话答案");
            this.addView(button,buttonLoyout);*/

        }
    }

    public void setAvatar(int resId){
        this.avatarId = resId;
        avatarImg.setImageResource(resId);
    }

    public void setContentBackground(int resId){
        this.contentBgId = resId;
        contentText.setBackgroundResource(resId);
    }

    public void setBorder(int resId){
        this.borderId = resId;
        borderImg.setImageResource(resId);
    }

    public void setTail(int resId){
        tailId = resId;
        tailImg.setImageResource(resId);
    }

    public void setTextColor(int color){
        contentText.setTextColor(color);
    }

    public void setViewPosition(int position){
        this.position = position;
    }

    public void setText(String msg){
        contentText.setText(msg);
    }

    public float getTextSize(){
        return contentText.getTextSize();
    }

    public float getTextWidth(){
        return contentText.getWidth();
    }

    public void setTextSize(int size){
        contentText.setTextSize(size);
    }

    public void setMaxEms(int ems){
        contentText.setMaxEms(ems);
    }


    public int getLineHeight(){
       return contentText.getLineHeight();
    }

    public TextView getContentText(){
        return contentText;
    }

    public ImageView getAvatarImageView(){
        return avatarImg;
    }

}
