package cn.wehax.whatup.support.helper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import cn.wehax.whatup.config.IntentKey;
import cn.wehax.whatup.support.util.ActivityUtils;
import cn.wehax.whatup.vp.chat.ChatActivity;
import cn.wehax.whatup.vp.guide.GuideActivity;
import cn.wehax.whatup.vp.login.choose.ChooseLoginOrRegisterActivity;
import cn.wehax.whatup.vp.login.complete_user_info.CompleteUserInfoActivity;
import cn.wehax.whatup.vp.login.forget_password.reset_password.ResetPasswordActivity;
import cn.wehax.whatup.vp.login.forget_password.retrieve_password.RetrievePasswordActivity;
import cn.wehax.whatup.vp.login.login.LoginActivity;
import cn.wehax.whatup.vp.login.register.cell_register.CellRegisterActivity;
import cn.wehax.whatup.vp.login.register.set_password.SetPasswordActivity;
import cn.wehax.whatup.vp.main.impl.ARMainActivity;
import cn.wehax.whatup.vp.main.preview_and_edit_status.PreviewAndEditStatusActivity;
import cn.wehax.whatup.vp.relation.RelationActivity;
import cn.wehax.whatup.vp.setting.SettingActivity;
import cn.wehax.whatup.vp.user_info.denounce.DenounceActivity;
import cn.wehax.whatup.vp.user_info.edit.EditUserInfoActivity;
import cn.wehax.whatup.vp.user_info.other.OtherHomepageActivity;
import cn.wehax.whatup.vp.user_info.personal.PersonalHomepageActivity;
import cn.wehax.whatup.vp.user_info.personal.PersonalHomepagePresenter;


/**
 * 本助手提供所有页面之间的跳转方法
 * <p/>
 * <p>为什么使用本助手？</p>
 * 通过查看本助手即可知道如何跳转到具体某个页面。
 */
public class MoveToHelper {

    public static void moveToCompleteUserInfoView(Activity ctx) {
        ActivityUtils.moveToActivity(ctx, CompleteUserInfoActivity.class);
    }

    public static void moveToRegisterView(Activity ctx) {
        ActivityUtils.moveToActivity(ctx, CellRegisterActivity.class);
    }

    public static void moveToLoginView(Activity ctx) {
        ActivityUtils.moveToActivity(ctx, LoginActivity.class);
        ctx.finish();
    }

    /**
     * 跳转到登陆页，登录成功则返回跳转前页面
     * @param activity
     * @param requestCode
     * @param from
     */
    public static void moveToLoginViewForResult(Activity activity, int requestCode, String from){
        Bundle bundle = new Bundle();
        bundle.putString(IntentKey.INTENT_KEY_FROM, from);

        ActivityUtils.moveToActivityForResult(activity,LoginActivity.class, requestCode, bundle);
    }

    public static void moveToGuideView(Activity ctx) {
        ActivityUtils.moveToActivity(ctx, GuideActivity.class);
    }

    public static void moveToRetrievePasswordView(Activity ctx) {
        ActivityUtils.moveToActivity(ctx, RetrievePasswordActivity.class);
    }

    public static void moveToChooseLoginView(Activity ctx) {
        ActivityUtils.moveToActivity(ctx, ChooseLoginOrRegisterActivity.class);
    }

    public static void moveToARMainView(Activity ctx) {
        ActivityUtils.moveToActivity(ctx, ARMainActivity.class);
    }

    /**
     * @param ctx
     * @param phoneNumber 手机号
     */
    public static void moveToSetPasswordView(Activity ctx, String phoneNumber) {
        Bundle bundle = new Bundle();
        bundle.putString(IntentKey.INTENT_KEY_PHONE_NUMBER, phoneNumber);

        ActivityUtils.moveToActivity(ctx, SetPasswordActivity.class, bundle);
    }

    /**
     * @param ctx
     * @param phoneNumber 手机号
     */
    public static void moveToResetPasswordView(Activity ctx, String phoneNumber) {
        Bundle bundle = new Bundle();
        bundle.putString(IntentKey.INTENT_KEY_PHONE_NUMBER, phoneNumber);

        ActivityUtils.moveToActivity(ctx, ResetPasswordActivity.class, bundle);
    }

    public static void moveToPersonalHomepage(Activity ctx) {
        ActivityUtils.moveToActivity(ctx, PersonalHomepageActivity.class);
    }

    public static void moveToRelation(Activity ctx) {
        ActivityUtils.moveToActivity(ctx, RelationActivity.class);
    }

    /**
     *
     * @param ctx
     * @param userId 目标用户ID
     * @param statusId 状态ID
     */
    public static void moveToOtherHomepage(Activity ctx, String userId, String statusId) {
        Bundle bundle = new Bundle();
        bundle.putString(IntentKey.INTENT_KEY_TARGET_UID, userId);
        bundle.putString(IntentKey.INTENT_KEY_STATUS_ID, statusId);
        ActivityUtils.moveToActivity(ctx, OtherHomepageActivity.class, bundle);
    }

    public static void moveToOtherHomepage(Activity ctx, String userId) {
        Bundle bundle = new Bundle();
        bundle.putString(IntentKey.INTENT_KEY_TARGET_UID, userId);
        ActivityUtils.moveToActivity(ctx, OtherHomepageActivity.class, bundle);
    }

    public static void moveToSetting(Activity ctx) {
        ActivityUtils.moveToActivity(ctx, SettingActivity.class);
    }

    public static void moveToChatView(Activity ctx, String userId, String statusId) {
        Intent intent = new Intent(ctx, ChatActivity.class);
        intent.putExtra(IntentKey.INTENT_KEY_STATUS_ID, statusId);
//                intent.putExtra(IntentKey.INTENT_KEY_SELF_UID, "556aabb1e4b0349d3342f54e");
        intent.putExtra(IntentKey.INTENT_KEY_TARGET_UID, userId);
        ctx.startActivity(intent);
    }

    public static void moveToChatView(Activity ctx, String userId) {
        Intent intent = new Intent(ctx, ChatActivity.class);
        intent.putExtra(IntentKey.INTENT_KEY_TARGET_UID, userId);
        ctx.startActivity(intent);
    }

    /**
     * 跳转到举报页面
     *
     * @param ctx
     * @param userId    目标用户id
     * @param nickname
     * @param sex
     * @param avatarUrl
     */
    public static void moveToDenounceView(Activity ctx, String userId, String nickname, int sex, String avatarUrl) {
        Bundle bundle = new Bundle();
        bundle.putString(IntentKey.INTENT_KEY_TARGET_UID, userId);
        bundle.putString(IntentKey.INTENT_KEY_NICKNAME, nickname);
        bundle.putString(IntentKey.INTENT_KEY_AVATAR_URL, avatarUrl);
        bundle.putInt(IntentKey.INTENT_KEY_SEX, sex);
        bundle.putString(IntentKey.INTENT_KEY_TARGET_UID, userId);
        ActivityUtils.moveToActivity(ctx, DenounceActivity.class, bundle);
    }

    public static void moveToEditUserInfoView(Activity ctx) {
        ActivityUtils.moveToActivityForResult(ctx, EditUserInfoActivity.class, PersonalHomepagePresenter.UPDATE_DATA);
    }


    /**
     * 跳转到状态预览界面，请求返回状态数据
     *
     * @param ctx
     * @param requestCode
     * @param imgPath     拍照图片绝对路径
     */
    public static void moveToPreviewAndEditStatusViewForResult(Activity ctx, int requestCode, String imgPath) {
        Bundle bundle = new Bundle();
        bundle.putString(IntentKey.INTENT_KEY_STATUS_IMAGE_PATH, imgPath);
        ActivityUtils.moveToActivityForResult(ctx, PreviewAndEditStatusActivity.class, requestCode, bundle);
    }


}
