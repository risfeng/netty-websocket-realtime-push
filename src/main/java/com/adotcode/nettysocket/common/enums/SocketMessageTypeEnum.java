package com.adotcode.nettysocket.common.enums;

/**
 * Socket消息类别枚举
 *
 * @author risfeng
 * @date 2020/9/7
 */
public enum SocketMessageTypeEnum {

    /**
     * 发送给全部在线连接
     */
    SEND_TO_ALL,

    /**
     * 私聊
     */
    PRIVATE_CHAT,
    /**
     * 群聊
     */
    GROUP_CHAT,

    /**
     * 系统通知
     */
    SYSTEM_NOTIFICATION,

    /**
     * 实时在线统计
     */
    REAL_TIME_ONLINE_STATISTICS

}
