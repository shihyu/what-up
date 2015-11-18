package cn.wehax.whatup.vp.chat;

import android.graphics.Bitmap;
import android.graphics.Point;

import java.util.List;

import cn.wehax.whatup.framework.view.IBaseView;
import cn.wehax.whatup.model.chatView.ChatMessageContent;
import cn.wehax.whatup.model.chatView.GraffitiAction;

/**
 * Created by howe on 15/6/24.
 * Email:howejee@gmail.com
 */
public interface IChatView extends IBaseView {

    /**
     * 设置标题
     */
    public void setTitle(String title,int sex);

    public void hideProgress();

    public void showProgress();

    /**
     * 设置背景
     */
    public void setBackgroundImage(Bitmap bitmap);

    /**
     * 添加一条文本聊天消息
     * @param text 文本内容
     * @param position 显示位置BubbleView.POSITION_LEFT 或 BubbleView.POSITION_RIGHT
     * @param bubbleStyle 样式
     * @param avatarUrl  头像url
     */

    /**
     * 添加文本消息
     * @param messages
     */
      public void addTextMessage(ChatMessageContent message);

    void addTextMessage(List<ChatMessageContent> chatMessageContents);


    /**
     * 撤销对方发的一条涂鸦
     */
    public void revokeRemoteGraffiti();

    /**
     * 清理涂鸦
     */
    public void clearGraffiti();

    /**
     * 刷新涂鸦
     */
    public void refreshGraffiti();

    /**
     * 绘制远程涂鸦
     * @param points 路径
     * @param color 颜色
     */
    public void drawRemoteGraffiti(List<Point> points,int color);

    /**
     * 绘制本地涂鸦
     * @param actions 涂鸦列表
     */
    public void drawLocalGraffiti(List<GraffitiAction> actions);


    /**
     * 屏幕宽度
     * @return
     */
    public int getScreenWidth();

    /**
     * 屏幕高度
     * @return
     */
    public int getScreenHeight();

    /**
     * 获取涂鸦窗体宽度
     * @return
     */
    public int getGraffitiViewWidth();

    /**
     * 获取涂鸦窗体高度
     * @return
     */
    public int getGraffitiViewHeight();





}
