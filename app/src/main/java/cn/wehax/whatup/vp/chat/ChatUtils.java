package cn.wehax.whatup.vp.chat;

import android.graphics.Color;
import android.graphics.Point;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.wehax.whatup.config.Constant;
import cn.wehax.whatup.model.chatView.ChatMessage;
import cn.wehax.whatup.model.chatView.ChatMessageContent;
import cn.wehax.whatup.model.chatView.GraffitiAction;
import cn.wehax.whatup.model.chatView.GraffitiPath;
import cn.wehax.whatup.model.chatView.GraffitiPoint;


/**
 * Created by howe on 15/6/3.
 * Email:howejee@gmail.com
 * 聊天通讯工具类
 */
public class ChatUtils {


    /**
     * 压缩数据，将点坐标和时间压缩成int（32bit）
     */
    public static int zipData(int x, int y, int time) {
        return (x << 21) + (y << 10) + time;
    }


    /**
     * 解压数据,将int数据还原成点坐标和时间
     */
    public static float[] unzipData(int data) {
        float[] point = new float[3];
        point[2] = data % 0x400; // 时间
        data = data >> 10;
        point[1] = data % 0x800; //y坐标
        point[0] = data >> 11;   //x坐标
        return point;
    }

    /**
     * 远程屏幕点 转换成 图片上点后 与 图片高度的比率
     *
     * @param point              转换的点坐标
     * @param imgRatio           图片的宽高比
     * @param remoteScreenWidth  对方屏幕宽度
     * @param remoteScreenHeight 对方屏幕高度
     * @return
     */
    public static float[] convertRemoteCoordToImageCoordRatio(float[] point, float imgRatio, int remoteScreenWidth, int remoteScreenHeight) {
        int offsetX;
        int offsetY;
        float scale;
        float imageX;
        float imageY;

        //屏幕宽高比，宽高比越大约扁，越小越长
        float screenRatio = (float) remoteScreenWidth / (float) remoteScreenHeight;

        if (screenRatio >= imgRatio) {
            //屏幕比图片扁的时候，为了满屏显示，图片缩放后以屏幕宽为宽
            int realImgWidth = remoteScreenWidth;
            int realImgHeight = (int) (realImgWidth / imgRatio);

            offsetX = 0;
            offsetY = (realImgHeight - remoteScreenHeight) / 2;
            scale = realImgHeight;
            imageX = point[0] + offsetX;
            imageY = point[1] + offsetY;
        } else {
            //屏幕比图片长的时候，图片以屏幕高为高
            int realImgHeight = remoteScreenHeight;
            int realImgWidth = (int) (realImgHeight * imgRatio);

            offsetX = (realImgWidth - remoteScreenWidth) / 2;
            offsetY = 0;
            scale = realImgHeight;
            imageX = point[0] + offsetX;
            imageY = point[1] + offsetY;
        }
        //以高为基准算出点得百分比
        float[] pointRatio = {(float) imageX / scale, (float) imageY / scale};
        return pointRatio;
    }

    /**
     * 通过图片上点与图片高度的比率，换算成本地屏幕上的点
     *
     * @param pointRatio        以图片圆点为坐标系的点相对高度的比率
     * @param imgRatio          图片的宽高比
     * @param localScreenWidth  本地屏幕宽度
     * @param localScreenHeight 本地屏幕高度
     * @return
     */
    public static int[] convertImageCoordToLoclCoord(float[] pointRatio, float imgRatio, int localScreenWidth, int localScreenHeight) {

        int offsetX;
        int offsetY;

        int x;
        int y;

        float scale;

        int imageX;
        int imageY;

        float screenRatio = (float) localScreenWidth / (float) localScreenHeight;

        if (screenRatio >= imgRatio) {
            int realImgWidth = localScreenWidth;
            int realImgHeight = (int) (realImgWidth / imgRatio);

            scale = realImgHeight;

            imageX = (int) (pointRatio[0] * scale);
            imageY = (int) (pointRatio[1] * scale);

            offsetX = 0;
            offsetY = (realImgHeight - localScreenHeight) / 2;

            x = imageX - offsetX;
            y = imageY - offsetY;
        } else {
            int realImgHeight = localScreenHeight;
            int realImgWidth = (int) (realImgHeight * imgRatio);

            scale = realImgHeight;

            imageX = (int) (pointRatio[0] * scale);
            imageY = (int) (pointRatio[1] * scale);

            offsetX = (realImgWidth - localScreenWidth) / 2;
            offsetY = 0;
            x = imageX - offsetX;
            y = imageY - offsetY;
        }

        int[] p = {x, y};
        return p;
    }

    //加载历史消息，只查询20条
    public void addHistoryMessage(List<ChatMessageContent> list, IChatView mView) {
        if (list != null && list.size() > 0) {
           /* if (list.size() > 20) {
                for (int i = list.size() - 20; i < list.size(); i++) {
                    ChatMessageContent content = list.get(i);
                    mView.addTextMessage(content);
                }
            } else {
                *//*for (int i = 0; i < list.size(); i++) {
                    ChatMessageContent content = list.get(i);
                    mView.addTextMessage(content);
                }*//*
            }*/
            mView.addTextMessage(list);
        }
    }

    /**
     * 处理历史消息
     *
     * @param list 历史消息列表
     */
    public void doHistoryMessage(List<ChatMessage> list, IChatView mView,String targetAvatar) {
        //TODO:这个方法有待优化，可以在ChatMessage里加个type，判断完再解析content

        Gson gson = new Gson();
        //有效涂鸦列表
        List<ChatMessageContent> graffitis = new ArrayList<>();

        //文本列表
        List<ChatMessageContent> chatTexts = new ArrayList<>();

        //分发历史消息
        for (ChatMessage bean : list) {

            //解析消息体
            ChatMessageContent msg = gson.fromJson(bean.getContent(), ChatMessageContent.class);
            msg.setSelf(bean.isSelf());

            if (ChatMessageContent.TYPE_GRAFFITI.equals(msg.getType())) {
                graffitis.add(msg);

            } else if (ChatMessageContent.TYPE_TEXT.equals(msg.getType()) || ChatMessageContent.TYPE_TRUTH.equals(msg.getType())) {
                if(!bean.isSelf()){
                    msg.setTime(bean.getTimestamp());
                    msg.setAvatar(targetAvatar);
                }
                chatTexts.add(msg);

            } else if (ChatMessageContent.TYPE_GRAFFITI_REVOKE.equals(msg.getType())) {
                //处理撤销命令
                doHistoryRevokeMessage(graffitis, msg.isSelf());

            } else if (ChatMessageContent.TYPE_CHANGE_BG.equals(msg.getType())) {
                graffitis.clear();
            }
        }

        //处理涂鸦历史记录
        List<GraffitiAction> actions = new ArrayList<>();
        for (ChatMessageContent graffiti : graffitis) {

            List<Point> points = getPointsInMessage(graffiti, mView);

            GraffitiAction gAction = getActionByPoints(points, Color.parseColor(graffiti.getStrokeStyle()));
            if (gAction != null) {
                gAction.setSelf(graffiti.isSelf());
                actions.add(gAction);
            }
        }
        mView.drawLocalGraffiti(actions);

        //处理文本历史记录
        addHistoryMessage(chatTexts, mView);
    }

    private void doHistoryRevokeMessage(List<ChatMessageContent> graffitis, boolean isSelf) {
        if (graffitis.isEmpty()) {
            return;
        }
        int i = graffitis.size() - 1;
        for (; i > -1; i--) {
            ChatMessageContent content = graffitis.get(i);
            if (content.isSelf() == isSelf) {
                graffitis.remove(i);
                return;
            }
        }
    }

    private List<Point> getPointsInMessage(ChatMessageContent message, IChatView mView) {
        List<Point> points = new ArrayList<>();

        for (Integer i : message.getData()) {
            float[] point = ChatUtils.unzipData(i);
            float[] imgPointRatio = ChatUtils.convertRemoteCoordToImageCoordRatio(point, 0.662f, message.getScreenWidth(), message.getScreenHeight());
            int[] localPoint = ChatUtils.convertImageCoordToLoclCoord(imgPointRatio, 0.662f, mView.getScreenWidth(), mView.getScreenHeight());

            points.add(new Point(localPoint[0], localPoint[1]));

        }
        return points;
    }

    private GraffitiAction getActionByPoints(List<Point> points, int color) {
        if (points == null || points.isEmpty()) {
            return null;
        }

        if (points.size() == 1) {

            GraffitiPoint point = new GraffitiPoint(points.get(0).x, points.get(0).y, color, Constant.GRAFFITI_DEFAULT_WIDTH);
            return point;
        } else {
            GraffitiPath path = null;
            for (Point point : points) {
                if (path == null) {
                    path = new GraffitiPath(point.x, point.y, color, Constant.GRAFFITI_DEFAULT_WIDTH);
                } else {
                    path.move(point.x, point.y);
                }
            }
            return path;
        }
    }

    private static String getInDayTime(int hour) {
        if (hour >= 0 && hour < 7) {
            return "凌晨";
        } else if (hour >= 7 && hour < 12) {
            return "上午";
        } else if (hour >= 12 && hour < 13) {
            return "中午";
        } else if (hour >= 13 && hour < 19) {
            return "下午";
        }
        return "晚上";
    }

    public static String getChatTime(long timesamp) {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        Date today = new Date(System.currentTimeMillis());
        Date otherDay = new Date(timesamp);
        int temp = Integer.parseInt(sdf.format(today))
                - Integer.parseInt(sdf.format(otherDay));
        switch (temp) {
            case 0:
                result = getInDayTime(getHour(timesamp)) + getHourAndMin(timesamp);
                break;
            case 1:
                result = "昨天 "+getInDayTime(getHour(timesamp)) + getHourAndMin(timesamp);
                break;

            default:
                result = getTime(timesamp);
                break;
        }
        return result;
    }

    public static String getHourAndMin(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(new Date(time));
    }
    public static int getHour(long time) {
         Date date = new Date(time);
        return date.getHours();

    }

    public static String getTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
        return format.format(new Date(time));
    }

}