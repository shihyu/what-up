package cn.wehax.whatup.model.leancloud;

/**
 * 本类与LeanCloud云端数据表对应
 */
public class LC {

    /**
     * LeanCLoud云数据库表
     */
    public static class table {

        /**
         * 用户表
         * <p/>
         * <p>字段说明：</p>
         * <ul>
         * <li>String tableName 表名 </li>
         * <li>String username 帐号名 AVUser属性（必须） </li>
         * <li>String password 密码 AVUser属性（必须） </li>
         * <li>String mobilePhoneNumber 密码 AVUser属性（必须） </li>
         * <li>String email AVUser属性  </li>
         * <li>String nickname 昵称</li>
         * <li>String city </li>
         * <li>Number sex 性别 0男 1女 </li>
         * <li>String introduce </li>
         * <li>AVFile avatar  头像 </li>
         * <li>Boolean mobilePhoneVerified 标记手机号码是否已经验证，系统默认字段 </li>
         * <li>Boolean verified 标记用户个人资料是否完善，verified=null等价于verified=false </li>
         * <li>String objectId 记录Id </li>
         * <li>GeoPoint location 位置 </li>
         * <li>Boolean isSendedStatus 标记是否发送过状态，默认false </li>
         * </ul>
         * <p/>
         * <p>username说明：手机登录时，将手机号作为username；第三方登录时，将系统返回帐号名作为username</p>
         */
        public final static class User {
            public static final String tableName = "_User";
            public static final String nickname = "nickname";
            public static final String city = "city";
            public static final String sex = "sex";
            public static final String introduce = "introduce";
            public static final String avatar = "avatar";
            public static final String mobilePhoneNumber = "mobilePhoneNumber";
            public static final String verified = "verified";
            public static final String objectId = "objectId";
            public static final String location = "location";
            public static final String isSendedStatus = "isSendedStatus";

            public static final int SEX_MALE = 0;
            public static final int SEX_FEMALE = 1;
        }

        /**
         * 用户状态表
         * <p/>
         * <p>字段说明：</p>
         * <ul>
         * <li>File imageData 状态图 </li>
         * <li>User source 发送人 </li>
         * <li>Date createdAt 发送时间 </li>
         * </ul>
         * <p/>
         * <p>username说明：手机登录时，将手机号作为username；第三方登录时，将系统返回帐号名作为username</p>
         */
        public final static class AllStatus {
            public static final String tableName = "AllStatus";
            public static final String imageData = "imageData";
            public static final String source = "source";
            public static final String createdAt = "createdAt";
        }

        /**
         * 举报表
         * <p/>
         * <p>字段说明：</p>
         * <ul>
         * <li>User from 举报人 </li>
         * <li>User user 被举报人 </li>
         * <li>String reason 原因 </li>
         * </ul>
         */
        public final static class DenounceData {
            public static final String tableName = "DenounceData";
            public static final String from = "from";
            public static final String user = "user";
            public static final String reason = "reason";
        }

        /**
         * Installation表
         * <p/>
         * <p>字段说明：</p>l
         * <ul>
         * <li>String uid 保存当前设备最近一次登录用户的ID </li>
         * </ul>
         */
        public final static class Installation {
            public static final String uid = "uid";
        }
    }

    /**
     * LeanCLoud云代码方法
     */
    public static class method {
        /*
         * 方法参数键值对说明
         */
        // String 查询用户id
        public static final String paramTargetId = "targetId";
        // String 举报原因
        public static final String paramReason = "reason";
        // int 查询返回的数量
        public static final String paramLimit = "limit";
        // long 时间戳，列表分页时使用，返回此时间之前的limit条数
        public static final String paramBeforeTime = "beforeTime";


        /**
         * 发送状态方法
         * <p/>
         * <p>字段说明：</p>
         * <ul>
         * <li>location keyLocation 发送人位置 </li>
         * <li>String keyImageId 状态图片ID </li>
         * <li>String keyText 状态附加文本 </li>
         * <li>String keyCoord 附加文本位置，格式："{"x":0.3,"y":0.4}，附加文本相对屏幕位置</li>
         * </ul>
         */
        public final static class SendNewStatus {
            public static final String functionName = "sendNewStatus";
            public static final String keyLocation = "location";
            public static final String keyImageId = "imageId";
            public static final String keyText = "text";
            public static final String keyCoord = "coord";
        }

        /**
         * 获取用户信息
         *
         * <p>方法参数键值对说明：</p>
         * <ul>
         * <li>paramTargetId - String 查询用户id </li>
         * </ul>
         *
         * <p>返回数据键值对说明：</p>
         * <ul>
         * <li>String keyId 用户ID </li>
         * <li>String keyNickname 昵称</li>
         * <li>Number keySex 性别 0男 1女 </li>
         * <li>String keyIntroduce </li>
         * <li>AVFile keyAvatar  头像 </li>
         * </ul>
         */
        public final static class GetUserInfo {
            public static final String functionName = "getUserInfo";
            public static final String paramTargetId = "targetId";
            public static final String keyId = "id";
            public static final String keyNickname = "nickname";
            public static final String keyAvatar = "avatar";
            public static final String keySex = "sex";
            public static final String keyIntroduce = "introduce";
        }

        /**
         * 举报用户
         *
         * <p>方法参数键值对说明：</p>
         * <ul>
         * <li>paramTargetId - String 查询用户id </li>
         * <li>paramReason - String 举报原因 </li>
         * </ul>
         */
        public final static class DenounceUser {
            public static final String functionName = "denounceUser";
            public static final String paramTargetId = "targetId";
            public static final String paramReason = "reason";
        }

        /**
         * 获得附近人的最新状态方法
         * <p/>
         * <p>字段说明：</p>
         * <ul>
         * <li>String keyTargetId 附近人ID<li>
         * <li>location keyLocation 附近人位置,格式为{"latitude":1.0,"longitude":2.0} </li>
         * <li>String keyDistance 最大搜索范围，默认为10 </li>
         * <li>String keyImageId 状态图片ID </li>
         * <li>String keyText 状态附加文本 </li>
         * <li>String keyCoord 附加文本位置，格式："{"x":0.3,"y":0.4}，附加文本相对屏幕位置</li>
         * </ul>
         */
        public final static class GetNearbyStatus {
            public static final String functionName = "getNearbyStatus";
            //TODO: 增加传入参数
            public static final String keyId = "id";
            public static final String keyTargetId = "source.id";
            public static final String keyLocation = "location";
            public static final String keyTargetAvatar = "source.avatar";
            public static final String keyTargetSex = "source.sex";
            public static final String keyTargetNickName = "source.nickname";
            public static final String keyDistance = "distance";
            public static final String keyImageData = "imageData";
            public static final String keyText = "text";
            public static final String keyCoord = "coord";
            public static final String keyCTime =  "ctime";
        }

        /**
         * 查看自己状态方法
         * <p/>
         * <p>字段说明：</p>
         * <ul>
         * <li>String keyId 自己的ID<li>
         * <li>location keyLocation 自己的位置 </li>
         * <li>String keyImageId 状态图片ID </li>
         * <li>String keyText 状态附加文本 </li>
         * <li>String keyCoord 附加文本位置，格式："{"x":0.3,"y":0.4}，附加文本相对屏幕位置</li>
         * </ul>
         */
        public final static class GetSelfStatusList {
            public static final String functionName = "getSelfStatusList";
            public static final String keyId = "id";
            public static final String keyTargetId = "targetId";
            public static final String keyLocation = "location";
            public static final String keyImageData = "imageData";
            public static final String keyText = "text";
            public static final String keyCoord = "coord";
            public static final String keyCTime =  "ctime";
        }

        /**
         * 查看其他用户状态方法
         * <p/>
         * <p>字段说明：</p>
         * <ul>
         * <li>String keyId 状态ID<li>
         * <li>String keySouceId 用户ID<li>
         * <li>location keyLocation 用户的位置 </li>
         * <li>String keyImageId 状态图片ID </li>
         * <li>String keyText 状态附加文本 </li>
         * <li>String keyCoord 附加文本位置，格式："{"x":0.3,"y":0.4}，附加文本相对屏幕位置</li>
         * <li>String keyCTime 状态发送时间</li>
         * </ul>
         */
        public final static class GetUserStatusList  {
            public static final String functionName = "getUserStatusList";
            public static final String keyId = "id";
            public static final String keySouceId = "source.id";
            public static final String keyLocation = "location";
            public static final String keyImageData = "imageData";
            public static final String keyText = "text";
            public static final String keyCoord = "coord";
            public static final String keyCTime =  "ctime";
        }

        /**
         * 上线统计方法
         */
        public final static class Online  {
            public static final String functionName = "online";
        }

        /**
         * 获得所有的消息记录方法
         * <p/>
         * <p>字段说明：</p>
         * <ul>
         * <li>String keyId 自己的ID<li>
         * <li>location keyMessage 所有消息记录 </li>
         * </ul> z

         */
        public final static class GetAllChatList  {
            public static final String functionName = "getAllChatList";
            public static final String paramOnlyOther = "onlyOther";
            public static final String paramStartTime = "startTime";
            public static final String keyMessage = "message";
            public static final String keyType = "type";
            public static final String keyFromID = "from.id";
            public static final String keyToId = "to.id";
            public static final String keyConversationId = "conversation.id";
            public static final String keyTimestamp = "timestamp";
        }

        /**
         * 获得与某个用户的消息记录方法
         * <p/>
         * <p>字段说明：</p>
         * <ul>
         * <li>String keyId 某用户的ID<li>
         * <li>location keyContent 某用户消息记录 </li>
         * </ul>
         */
        public final static class GetTargetChatList  {
            public static final String functionName = "getTargetChatList";
            public static final String keyId = "id";
            public static final String keyContent = "content";
            public static final String keyFrom = "from";
            public static final String keyTo = "to";
        }

        /**
         * 获得真心话功能方法
         */
        public final static class GetTruth  {
            public static final String functionName = "getTruth";
            public static final String keyId = "id";
            public static final String keyAnswer = "answer";
            public static final String keyQuestion = "question";
            public static final String keyLevel = "level";
            public static final String paramConversationId = "conversationId";

        }

        /**
         * 开始与其他用户的对话方法
         * <p/>
         * <p>字段说明：</p>
         * <ul>
         * <li>String keyId 会话的ID<li>
         * <li>String keyBackgroundImage 会话使用的图片AV.File <li>
         * <li>String keyIsStatus 当前是否使用状态 <li>
         * <li>String keyStatusTextCoord /状态文字的坐标 <li>
         * <li>String keyStatusText 状态文字内容 <li>
         * </ul>
         */
        public final static class LoadTargetUserConversation  {
            public static final String functionName = "loadTargetUserConversation";
            public static final String paramTargetId = "targetId";
            public static final String paramStatusId = "statusId";
            public static final String keyId = "id";
            public static final String keyBackgroundImage = "backgroundImage";
            public static final String keyIsStatus = "isStatus";
            public static final String keyStatusTextCoord = "statusTextCoord";
            public static final String keyStatusText = "statusText";
        }

        /**
         * 获得用户的对话列表方法
         * <p/>
         * <p>字段说明：</p>
         * <ul>
         * <li>String keyDirection  from或者to,from则由用户发起，to则由对方发起 <li>
         * <li>String keyTargetAvatar 对方的头像 <li>
         * <li>String keyTemp 是否由系统生成的临时推荐对话，true为临时，false为永久 <li>
         * <li>String keyTargetSex 对方用户sex <li>
         * <li>String keyTargetNickName 对方用户Name <li>
         * <li>String keyTargetId 对方用户id <li>
         * </ul>
         */
        public final static class GetMyConversationList  {
            public static final String keyId = "id";
            public static final String functionName = "getMyConversationList";
            public static final String keyDirection = "direction";
            public static final String keyTargetAvatar = "target.avatar";
            public static final String keyUpdatedAt = "updatedAt";
            public static final String keyTemp = "temp";
            public static final String keyTargetSex = "target.sex";
            public static final String keyTargetNickName = "target.nickname";
            public static final String keyTargetId = "target.id";
        }

        /**
         * 获得与某个用户的涂鸦消息记录方法
         * <p/>
         * <p>字段说明：</p>
         * <ul>
         * <li>String paramSelfId  自己Id <li>
         * <li>String paramTargetId  对方Id <li>
         * <li>String keyType  类型 <li>
         * <li>String keyContent 消息内容 <li>
         * <li>String keyFromId 发起者id <li>
         * <li>String keyConversationId 会话id <li>
         * <li>String keyToId 对方id <li>
         * </ul>
         */

        public final static class GetTargetGraffitiList  {
            public static final String functionName = "getTargetGraffitiList";
            public static final String paramSelfId = "request.user.id";
            public static final String paramTargetId = "request.params.targetId";
            public static final String keyType = "type";
            public static final String keyContent = "content";
            public static final String keyFromId = "from.id";
            public static final String keyToId = "to.id";
            public static final String keyConversationId = "conversation.id";
            public static final String keyCreatedAt = "createdAt";
        }




        

      
    }


}
