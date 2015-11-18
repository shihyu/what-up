package cn.wehax.whatup.config;


import java.util.Date;

/**
 * 常量定义
 */
public class Constant {
    public static final String PROJECT_NAME = "whatup";
    /**
     * 你在干嘛-开发版
     */
    public static final String LC_APP_ID = "yn9r14rcep15rit6gbcgymynd299eu83gv1wv0yak3e86lah";
    public static final String LC_APP_KEY = "xvxr92iq83ic2thzjl2u6ztrhfejyyjxabsslyqej6nl0vki";

    /**
     * 你在干嘛-正式服
     */
    public static final String LEANCLOUD_APPID = "01i1l2w9vdspol2qhzqvdgkv9zahwu8ovuw29rlokqb2xn8p";
    public static final String LEANCLOUD_APPKEY = "r2s1zsuo28cqq1m0glws1c2j7t26hgw9wpvzw8xwyyg7fxhd";



    public static final int GRAFFITI_DEFAULT_WIDTH = 5;

    //消息收到
    public static final String RECEIVE_MESSAGE_ACTION = "RECEIVE_MESSAGE_ACTION";
    //消息发送成功
    public static final String RECEIVE_MESSAGE_RECEIPT = "RECEIVE_MESSAGE_RECEIPT";

    /**
     * 性别
     */
    public static final int SEX_MALE = 0;
    public static final int SEX_FEMALE = 1;

    /**
     * 按照时间分页的默认时间
     */
    public static final Long DEFAULT_TIME = null;
    public static final int STATUS_LIST_PAGE_SIZE = 5;

    /**
     * 类用使用的字符串
     */
    public static final String  TRUTH_ACTION = "真心话功能";
    public static final String  TRUTH_DES = "每次点击会自动发出真心话问题，请双方轮流回答";
    public static final String  TRUTH_ANSWER = "已回答过";
    public static final String  TRUTH_ANSWER_DES = "确认要重新回答吗？";
    public static final String  GRAFFITI_ON = "涂鸦模式已开启，对方将实时看到你画画的轨迹。画点什么调戏TA呢?";
    public static final String  GRAFFITI_OFF = "涂鸦模式已关闭，欢迎下次继续";
    public static final String  SYSTEM_REC = "[发布了一张新照片]";
    public static final String  GRAFFITI_MESSAGE = "[涂鸦消息]";
    public static final String  TYPE_CHANGE_BG = "[更换背景]";
    public static final String  TYPE_GRAFFITI_REVOKE = "[撤销涂鸦]";



}
