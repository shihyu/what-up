package cn.wehax.whatup.config;

/**
 * Created by Terry on 14/12/30.
 * mail: zhichangterry@gmail.com
 * login_qq_btn_n: 1090035354
 */
public class RequestCode {
    /**
     * request code start from 0x0123
     */
    public static final int RELATION_TO_CHAT = 0x0123;
    public static final int RETURN_TO_FROM_PAGE_IF_LOGIN_SUCCESS = RELATION_TO_CHAT + 1;
    public static final int REQUEST_CODE_RELATION_TO_CHAT = RETURN_TO_FROM_PAGE_IF_LOGIN_SUCCESS + 1;

}
