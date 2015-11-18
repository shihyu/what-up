package cn.wehax.whatup.framework.model;

import com.avos.avoscloud.AVException;

import java.util.HashMap;

public class WXException extends Exception {
    /**
     * 默认代码值
     * <p>如果WXException对象使用该值，说明对这个对象而言，code值无关紧要</p>
     */
    private final int DEFAULT_CODE = -1;
    private final String DEFAULT_DES = "unknown exception";

    private final int code;
    private final String description;

    public WXException() {
        this.code = DEFAULT_CODE;
        this.description = DEFAULT_DES;
    }

    public WXException(String description) {
        this.code = DEFAULT_CODE;
        this.description = description;
    }

    public WXException(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据已经定义的异常类型创建对象
     * @param exceptionType
     */
    public WXException(int exceptionType) {
        if(excetiopMap.containsKey(exceptionType)){
            this.code = exceptionType;
            this.description = excetiopMap.get(exceptionType);
        }else{
            this.code = DEFAULT_CODE;
            this.description = DEFAULT_DES;
        }
    }

    public WXException(AVException e) {
        this.code = e.getCode();
        this.description = e.getMessage();
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 用户查询，结果为空
     */
    public static final int USER_QUERY_RESULT_IS_EMPTY = 90001;

    @Override
    public String toString() {
        return "[" +
                "code=" + code +
                ", description='" + description + '\'' +
                ']';
    }



    public static final int EXCEPTION_RESULT_IS_EMPTY = 19001;
    public static final int EXCEPTION_CANNOT_FIND_USER = 211;
    public static final int EXCEPTION_USERNAME_PASSWORD_MISMATCH = 210;
    public static final int EXCEPTION_TIMEOUT = 124;

    private static final HashMap<Integer, String> excetiopMap = new HashMap<>();
    static{
        excetiopMap.put(EXCEPTION_RESULT_IS_EMPTY, "查询时，未找到符合条件的记录");
        excetiopMap.put(EXCEPTION_CANNOT_FIND_USER, "用户不存在");
        excetiopMap.put(EXCEPTION_USERNAME_PASSWORD_MISMATCH, "密码或帐号错误");
        excetiopMap.put(EXCEPTION_TIMEOUT, "连接服务器超时");
    }

    /**
     * 获取错误描述
     * @param exceptionCannotFindUser
     * @return
     */
    public static String getExcptionDes(int exceptionCannotFindUser) {
        return excetiopMap.get(exceptionCannotFindUser);
    }
}
