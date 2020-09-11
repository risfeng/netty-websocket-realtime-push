package com.adotcode.nettysocket.service.account;

import com.adotcode.nettysocket.service.account.dto.LoginInputDTO;
import com.adotcode.nettysocket.service.account.dto.LoginOutputDTO;

/**
 * 账户管理服务接口
 *
 * @author risfeng
 * @date 2020/9/8
 */
public interface AccountService {

    /**
     * 用户登录
     *
     * @param input 登录输入
     * @return 登录后的信息
     */
    LoginOutputDTO login(LoginInputDTO input);

    /**
     * 注销登录
     *
     * @param token 用户token凭证
     * @return 注销成功与否
     */
    Boolean logout(String token);

}
