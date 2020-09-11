package com.adotcode.nettysocket.account.dto;

import com.adotcode.nettysocket.common.utils.GsonUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 登录输入DTO
 *
 * @author risfeng
 * @date 2020/9/8
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginInputDTO implements Serializable {

    private static final long serialVersionUID = 7318766388610479592L;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户密码（加密后）
     */
    private String password;

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
