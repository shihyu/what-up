package cn.wehax.whatup.model.user;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.FunctionCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.UpdatePasswordCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Provider;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.utils.WechatClientNotExistException;
import cn.wehax.whatup.config.Debug;
import cn.wehax.whatup.framework.model.OnRequestDataListener;
import cn.wehax.whatup.framework.model.OnRequestListener;
import cn.wehax.whatup.framework.model.OnRequestResultListener;
import cn.wehax.whatup.framework.model.WXException;
import cn.wehax.whatup.model.leancloud.LC;
import cn.wehax.whatup.support.helper.LogHelper;

/**
 * 用户管理器
 */
@Singleton
public class UserManager {
    public static final String TAG = "UserManager";
    private Application application;

    @Inject
    UserManager(Provider<Application> application) {
        this.application = application.get();
        init();
    }

    /**
     * 初始化Manager
     */
    private void init() {
        // 使用LeanCloud帐号系统时， 默认登录工作，由LeanCloud自动完成，我们不需要做任何工作。
        // 具体的，AVUser对象在创建时，自动检查是否能够默认登录，如果能，使用默认帐号自动登录。

        if (Debug.isAutoLogoutWhenEnter && getCurrentUser() != null) {
            logout();
        }

        // 检查默认登录是否有效，如果无效退出默认登录
        // 如果用户信息不完整，默认登录无效
        if (getCurrentUser() != null && !isUserInformationIntegrity(getCurrentUser())) {
            logout();
        }
    }

    public AVUser getCurrentUser() {
        return AVUser.getCurrentUser();
    }

    /**
     * 如果用户资料完整，返回true
     */
    public boolean isUserInformationIntegrity(AVUser user) {
        assert (user != null);

        Boolean verified = user.getBoolean(LC.table.User.verified);

        if (verified == null || verified == false)
            return false;
        else
            return true;
    }

    /**
     * 如果用户已经登录，返回true
     *
     * @return
     */
    public boolean isLogin() {
        if (getCurrentUser() != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 如果需要手动登录，返回true
     *
     * @return
     */
    public boolean isNeedLoginManually() {
        return !isLogin();
    }

    /**
     * 退出登录
     */
    public void logout() {
        AVUser.logOut();
    }

    /**
     * 登录（手机号+密码）
     * <p>注：LeanCloud帐号系统</p>
     *
     * @param phoneNumber
     * @param password
     * @param lis
     */
    public void cellLogin(String phoneNumber, String password, final OnRequestListener lis) {
        if (lis == null)
            return;

        AVUser.loginByMobilePhoneNumberInBackground(phoneNumber, password, new LogInCallback<AVUser>() {
            @Override
            public void done(AVUser avUser, AVException e) {
                if (e == null) {
                    // 登录成功
                    lis.onSuccess();
                } else {
                    // 登录失败
                    LogHelper.e(TAG, "cellLogin-fail", e);
                    lis.onError(new WXException(e));
                }
            }
        });
    }

    /**
     * 注册手机号
     *
     * @param phoneNumber
     */
    public void signUpOrLoginByMobilePhone(String phoneNumber, String smsCode, final OnRequestListener lis) {
        if (lis == null)
            return;

        AVUser.signUpOrLoginByMobilePhoneInBackground(phoneNumber, smsCode, new LogInCallback<AVUser>() {
            public void done(AVUser user, AVException e) {
                if (e == null) {
                    // 注册/登录成功
                    Log.e(TAG, "signUpOrLoginByMobilePhone-success");
                    lis.onSuccess();
                } else {
                    // 注册/登录失败
                    LogHelper.e(TAG, "signUpOrLoginByMobilePhone-fail ", e);
                    lis.onError(new WXException(e));
                }
            }
        });

    }

    /**
     * 请求服务器，给指定手机发送验证码短信
     *
     * @param phoneNumber
     */
    public void requestSMSCode(String phoneNumber, final OnRequestListener lis) {
        if (lis == null)
            return;

        AVOSCloud.requestSMSCodeInBackground(phoneNumber, new RequestMobileCodeCallback() {
            public void done(AVException e) {
                if (e == null) {
                    // 发送成功
                    Log.e(TAG, "requestSMSCode-success");
                    lis.onSuccess();
                } else {
                    // 发送失败
                    LogHelper.e(TAG, "requestSMSCode-fail", e);
                    lis.onError(new WXException(e));
                }
            }
        });
    }

    /**
     * 设置用户名和密码
     *
     * @param user        当前登录用户
     * @param phoneNumber
     * @param password
     */
    public void requestSetUsernameAndPassword(AVUser user, String phoneNumber, String password) {
        if (user == null) {
            return;
        }

        user.setUsername(phoneNumber);
        user.setPassword(password);
        user.put(LC.table.User.verified, false);
        user.saveInBackground();
    }

    /**
     * 请求服务器，修改密码
     * </p>
     * 特别注意：调用本方法时，必须已经登录
     *
     * @param curUser     当前登录用户
     * @param oldPassword
     * @param newPassword
     * @param lis
     */
    public void updatePassword(AVUser curUser, String oldPassword, String newPassword, final OnRequestListener lis) {
        if (curUser == null || lis == null)
            return;

        curUser.updatePasswordInBackground(oldPassword, newPassword, new UpdatePasswordCallback() {

            @Override
            public void done(AVException e) {
                if (e == null) {
                    // 验证成功
                    Log.e(TAG, "updatePassword-success");
                    lis.onSuccess();
                } else {
                    LogHelper.e(TAG, "updatePassword-fail", e);
                    lis.onError(new WXException(e));
                }
            }
        });
    }


    /**
     * 通过手机号码获取指定用户
     * <p/>
     * 特别注意:<br>
     * 如果查询成功，但是未找到满足条件的记录时，
     * lis.onSuccess()方法的data参数将返回null值
     * <p/>
     * 为什么这样设计？<br>
     * 参考关系型数据库。在查询数据时，即使未找到满足条件的记录，但查询操作本身是成功的。<br>
     * 只有在SQL语句出错时，才会弹窗提示异常信息。
     *
     * @param phoneNumber
     * @param lis
     */
    public void requestUserByPhoneNumber(String phoneNumber, final OnRequestDataListener<AVUser> lis) {
        if (lis == null)
            return;

        AVQuery<AVUser> query = AVUser.getQuery();
        query.whereEqualTo(LC.table.User.mobilePhoneNumber, phoneNumber);
        query.findInBackground(new FindCallback<AVUser>() {
            public void done(List<AVUser> users, AVException e) {
                try {
                    if (e != null) { // 查询出错
                        throw new WXException(e);
                    }

                    assert (users != null);

                    // 查询成功，但是未找到满足条件的记录
                    if (users.isEmpty()) {
                        lis.onSuccess(null);
                        return;
                    }

                    // 查询成功，返回用户对象
                    lis.onSuccess(users.get(0));
                } catch (WXException wxe) {
                    LogHelper.e(TAG, "requestUserByPhoneNumber-onError", e);
                    lis.onError(wxe);
                }
            }
        });
    }

    /**
     * 检查指定手机号码是否注册过
     * <p/>
     * <p>说明：如果使用某个手机号码正确的注册过，那么该手机号码一定已验证！</p>
     * <p/>
     * <p>返回值：如果手机号码已经注册，lis.onResult()方法返回true；否则返回false</p>
     *
     * @param phoneNumber
     */
    public void checkPhoneNumberRegistered(String phoneNumber, final OnRequestResultListener lis) {
        if (lis == null)
            return;

        requestUserByPhoneNumber(phoneNumber, new OnRequestDataListener<AVUser>() {
            @Override
            public void onSuccess(AVUser data) {
                if (data == null || !data.isMobilePhoneVerified()) {
                    lis.onResult(false);
                    return;
                }

                lis.onResult(true);
            }

            @Override
            public void onError(WXException wxe) {
                LogHelper.e(TAG, "checkPhoneNumberRegistered/requestUserByPhoneNumber/onError", wxe);
                lis.onError(wxe);
            }
        });

    }

    /**
     * 请求服务器给指定手机号发送带有验证码的短信，该验证码用于重置密码
     *
     * @param phoneNumber
     */
    public void requestPasswordResetSmsCode(String phoneNumber, final OnRequestListener lis) {
        if (lis == null)
            return;

        AVUser.requestPasswordResetBySmsCodeInBackground(phoneNumber, new RequestMobileCodeCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    //发送成功
                    lis.onSuccess();
                } else {
                    //发送失败
                    LogHelper.e(TAG, "requestPhoneVerifyCode/done", e);
                    lis.onError(new WXException(e));
                }
            }
        });
    }

    /**
     * 重置密码，基于短信验证码
     *
     * @param smsCode     验证码
     * @param newPassword 新密码
     * @param lis
     */
    public void resetPasswordBySmsCode(String smsCode, String newPassword, final OnRequestListener lis) {
        if (lis == null)
            return;

        AVUser.resetPasswordBySmsCodeInBackground(smsCode, newPassword, new UpdatePasswordCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    // 密码更改成功！
                    lis.onSuccess();
                } else {
                    LogHelper.e(TAG, "resetPasswordBySmsCode/done", e);
                    lis.onError(new WXException(e));
                }
            }
        });
    }

    /**
     * 完善用户信息
     *
     * @param nickName
     * @param userSex
     */
    public void completeUserInfo(AVUser user, String nickName, Number userSex, AVFile avatar, final OnRequestListener lis) {
        if (user == null)
            return;

        user.put(LC.table.User.nickname, nickName);
        user.put(LC.table.User.sex, userSex);
        user.put(LC.table.User.verified, true);
        if (avatar != null)
            user.put(LC.table.User.avatar, avatar);

        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    // 完善成功！
                    Log.e(TAG, "completeUserInfo-success");
                    lis.onSuccess();
                } else {
                    LogHelper.e(TAG, "completeUserInfo-fail", e);
                    lis.onError(new WXException(e));
                }
            }
        });
    }

    public static final int PLATFORM_WEIXIN = 1;
    public static final int PLATFORM_QQ = 2;
    public static final int PLATFORM_SINA = 3;

    /**
     * 第三方登录
     *
     * @param platform 第三方平台标识
     * @param lis
     */
    public void thirdPartyLogin(int platform, final OnThirdPartyLoginListener lis) {
        Log.e(TAG, "=========thirdPartyLogin=========");
        if (lis == null)
            return;

        final String platformName;
        switch (platform) {
            case PLATFORM_WEIXIN:
                platformName = Wechat.NAME;
                break;
            case PLATFORM_QQ:
                platformName = QQ.NAME;
                break;
            case PLATFORM_SINA:
                platformName = SinaWeibo.NAME;
                break;
            default:
                // 参数platform，传入的值不合法
                return;
        }

        ShareSDK.initSDK(application);
        Platform pf = ShareSDK.getPlatform(application, platformName);
        if (pf.isValid() && !TextUtils.isEmpty(pf.getDb().getUserId())) {
            loginWithAuthData(pf.getDb(), platformName, lis);
            return;
        }

        pf.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> stringObjectHashMap) {
                loginWithAuthData(platform.getDb(), platformName, lis);
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Log.e(TAG, "thirdPartyLogin/onError/" + throwable.getMessage());
                if (throwable instanceof WechatClientNotExistException) {
                    lis.onError(new WXException("微信未安装"));
                    return;
                }

                lis.onError(new WXException("授权失败"));
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Log.e(TAG, "thirdPartyLogin/onCancel");
                lis.onCancel();
            }
        });
        pf.SSOSetting(false);
        pf.showUser(null);
    }

    /**
     * @param db
     * @param platformName
     * @param lis
     */
    private void loginWithAuthData(PlatformDb db, String platformName, final OnThirdPartyLoginListener lis) {
        if (db == null || lis == null)
            return;

        AVUser.AVThirdPartyUserAuth auth = new AVUser.AVThirdPartyUserAuth(
                db.getToken(),
                String.valueOf(db.getExpiresTime()),
                platformName,
                db.getUserId());
        auth.setUserId(db.getUserId());
        auth.setAccessToken(db.getToken());
        auth.setSnsType("bearer");
        Log.e(TAG, "loginWithAuthData-auth=" + auth.toString());
        AVUser.loginWithAuthData(auth, new LogInCallback<AVUser>() {
            @Override
            public void done(AVUser avUser, AVException e) {
                if (e == null) {
                    // 登录成功
                    Log.e(TAG, "loginWithAuthData-success");
                    lis.onSuccess();
                } else {
                    // 登录失败
                    LogHelper.e(TAG, "loginWithAuthData-fail ", e);
                    lis.onError(new WXException("登录失败"));
                }
            }
        });
    }

    /**
     * 请求服务器，根据ID获取指定用户
     *
     * @param targetId
     * @param lis
     */
    public void requestUserById(String targetId, final OnRequestDataListener<Map> lis) {
        if (lis == null)
            return;

        Map<String, Object> params = new HashMap<>();
        params.put(LC.method.GetUserInfo.paramTargetId, targetId);
        AVCloud.callFunctionInBackground(LC.method.GetUserInfo.functionName, params, new FunctionCallback<Map>() {
            @Override
            public void done(Map o, AVException e) {
                try {
                    if (e != null) { // 查询出错
                        throw new WXException(e);
                    }

                    if (o == null) {
                        throw new WXException("o == null");
                    }

                    Log.e(TAG, "requestUserById-success");
                    lis.onSuccess(o);
                } catch (WXException wxe) {
                    LogHelper.e(TAG, "requestUserById-fail", e);
                    lis.onError(wxe);
                }
            }
        });

    }

    /**
     * 举报指定用户
     *
     * @param reason
     * @param userId
     * @param lis
     */
    public void denounce(String reason, String userId, final OnRequestListener lis) {
        if (lis == null)
            return;

        Map<String, Object> params = new HashMap<>();
        params.put(LC.method.DenounceUser.paramTargetId, userId);
        params.put(LC.method.DenounceUser.paramReason, reason);
        AVCloud.callFunctionInBackground(LC.method.DenounceUser.functionName, params, new FunctionCallback<Map>() {
            @Override
            public void done(Map o, AVException e) {
                try {
                    if (e != null) { // 查询出错
                        throw new WXException(e);
                    }

                    if (o == null) {
                        throw new WXException("o == null");
                    }

                    Log.e(TAG, "denounce-success");
                    lis.onSuccess();
                } catch (WXException wxe) {
                    LogHelper.e(TAG, "denounce-fail", e);
                    lis.onError(wxe);
                }
            }
        });
    }

    /**
     * 保存用户信息
     *
     * @param newNickname
     * @param newIntroduce
     * @param lis
     */
    public void updateUserInfo(final String newNickname, final String newIntroduce, final OnRequestListener lis) {
        if (lis == null)
            return;

        AVUser user;
        try {
            user = AVObject.createWithoutData(AVUser.class, getCurrentUser().getObjectId());
        } catch (AVException e) {
            e.printStackTrace();
            lis.onError(new WXException(e));
            return;
        }

        user.put(LC.table.User.nickname, newNickname);
        user.put(LC.table.User.introduce, newIntroduce);

        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                try {
                    if (e != null) { // 查询出错
                        throw new WXException(e);
                    }

                    getCurrentUser().put(LC.table.User.nickname, newNickname);
                    getCurrentUser().put(LC.table.User.introduce, newIntroduce);

                    Log.e(TAG, "updateUserInfo-success");
                    lis.onSuccess();
                } catch (WXException wxe) {
                    LogHelper.e(TAG, "updateUserInfo-fail", e);
                    lis.onError(wxe);
                }
            }
        });
    }

    //更新用户头像，重载方法
    public void updateUserInfo(final AVFile avatar, final OnRequestListener lis) {
        if (lis == null || avatar == null)
            return;
        AVUser user;
        try {
            user = AVUser.getCurrentUser();
        } catch (Exception e) {
            return;
        }
        user.put(LC.table.User.avatar, avatar);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    //保存头像成功
                    lis.onSuccess();
                } else {
                    lis.onError(new WXException(e));
                }
            }
        });
    }

    /**
     * 更新用户位置信息
     *
     * @param location
     * @param city
     */
    public void updateUserLocation(AVGeoPoint location, String city) {
        getCurrentUser().put(LC.table.User.city, city);
        getCurrentUser().put(LC.table.User.location, location);
        getCurrentUser().saveInBackground();
    }


    public interface OnThirdPartyLoginListener {
        void onSuccess();

        void onError(WXException e);

        void onCancel();
    }

    /**
     * 如果发送过状态，返回true；否则返回false
     *
     * @return
     */
    public boolean isSendedStatus() {
        return getCurrentUser().getBoolean(LC.table.User.isSendedStatus);
    }

    /**
     * 记录当前用户发送过状态
     */
    public void saveSendedStatus() {
        getCurrentUser().put(LC.table.User.isSendedStatus, true);
        getCurrentUser().saveInBackground();
    }
}
