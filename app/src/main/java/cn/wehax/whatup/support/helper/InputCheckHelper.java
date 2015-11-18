package cn.wehax.whatup.support.helper;

import android.app.Activity;
import android.text.TextUtils;

import cn.wehax.util.StringUtils;
import cn.wehax.whatup.R;
import cn.wehax.whatup.support.util.ToastUtils;

/**
 * 本助手提供检查用户输入的辅助方法
 */
public class InputCheckHelper {

    /**
     * 检查输入的手机号
     *
     * @param context
     * @param phoneNum
     * @return 如果手机号正确返回true；如果手机号错误，弹出Toast显示错误信息，并返回false
     */
    public static boolean checkInputPhoneNumber(Activity context, String phoneNum) {
        phoneNum = phoneNum.trim();

        if (TextUtils.isEmpty(phoneNum)) {//手机号为空
            ToastUtils.showToast(context, R.string.check_phone_number_cannot_empty);
            return false;
        }

        if (!StringUtils.ValidityCheck.isLegalPhoneNumber(phoneNum)) { //手机号有误
            ToastUtils.showToast(context, R.string.check_phone_number_incorrect);
            return false;
        }

        return true;
    }

    /**
     * 验证码长度
     */
    public static final int VERIFY_CODE_LENGTH = 6;

    /**
     * 检查输入的验证码
     *
     * @param context
     * @param verifyCode
     * @return 如果验证码正确返回true；如果手机号错误，弹出Toast显示错误信息，并返回false
     */
    public static boolean checkInputVerifyCode(Activity context , String verifyCode){
        verifyCode = verifyCode.trim();

        if(TextUtils.isEmpty(verifyCode)){//验证码为空
            ToastUtils.showToast(context, R.string.check_verify_code_cannot_empty);
            return false;
        }
        if(verifyCode.length() != VERIFY_CODE_LENGTH){
            ToastUtils.showToast(context, R.string.check_verify_code_incorrect);
            return false;
        }
        // 关于验证码只能是数字的格式要求，
        // 在布局文件中已经做出限制，这里不需要再次检查
        return true;
    }

    private static final int PASSWORD_LENGTH_LOWER_LIMIT = 6; // 密码长度下限

    /**
     * 检查输入的密码
     *
     * @param context
     * @param password
     * @return 如果密码格式正确返回true；如果密码格式错误，弹出Toast显示错误信息，并返回false
     */
    public static boolean checkInputPassword(Activity context, String password) {
        password = password.trim();

        if (TextUtils.isEmpty(password)) {//密码为空
            ToastUtils.showToast(context, R.string.check_password_cannot_empty);
            return false;
        }

        if (password.length() < PASSWORD_LENGTH_LOWER_LIMIT) { //密码太短
            ToastUtils.showToast(context, R.string.check_password_length_lower_limit);
            return false;
        }

        // 关于（1）密码长度不能超过12位（2）密码只能是数字或字母的格式要求，
        // 在布局文件中已经做出限制，这里不需要再次检查

        return true;
    }

    private static final int NICKNAME_LENGTH_LOWER_LIMIT = 4; // 昵称长度下限

    /**
     * 检查用户昵称
     *
     * @param context
     * @param nickname
     * @return 如果格式正确返回true；如果格式错误，弹出Toast显示错误信息，并返回false
     */
    public static boolean checkInputNickname(Activity context, String nickname) {
        nickname = nickname.trim();

        if (TextUtils.isEmpty(nickname)) {//用户昵称为空
            ToastUtils.showToast(context, R.string.check_nickname_cannot_empty);
            return false;
        }

        if (nickname.length() < NICKNAME_LENGTH_LOWER_LIMIT) { //用户昵称太短
            ToastUtils.showToast(context, R.string.check_nickname_length_lower_limit);
            return false;
        }

        // 关于昵称长度不能超过10位
        // 在布局文件中已经做出限制，这里不需要再次检查

        return true;
    }

}
