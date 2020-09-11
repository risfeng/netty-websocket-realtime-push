package com.adotcode.nettysocket.common.result;

import com.adotcode.nettysocket.common.enums.SocketMessageTypeEnum;
import com.adotcode.nettysocket.common.utils.GsonUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Socket消息对象
 *
 * @author risfeng
 * @date 2020/9/7
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SocketMessage<T> {

    /**
     * 消息类别
     */
    private SocketMessageTypeEnum messageType;

    /**
     * 来源用户Id
     */
    private String fromUserId;

    /**
     * 目标用户Id
     */
    private String toUserId;

    /**
     * 目标群组Id（群聊/发）
     */
    private String toGroupId;

    /**
     * 消息体
     */
    private T body;


    /**
     * 序列化为Json
     *
     * @return json字符串
     */
    @Override
    public String toString() {
        return GsonUtils.getInstance().toJson(this);
    }
}
