package com.adotcode.nettysocket.account.dto;

import com.adotcode.nettysocket.common.utils.GsonUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 登录输出DTO
 *
 * @author risfeng
 * @date 2020/9/8
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginOutputDTO implements Serializable {

    private static final long serialVersionUID = -4917867035909419034L;

    /**
     * 用户Id
     */
    private Integer userId;

    /**
     * 用户姓名
     */
    private String name;

    /**
     * token
     */
    private String token;

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
