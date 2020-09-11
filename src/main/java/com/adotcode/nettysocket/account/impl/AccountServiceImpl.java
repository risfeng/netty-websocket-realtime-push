package com.adotcode.nettysocket.account.impl;


import com.adotcode.nettysocket.account.AccountService;
import com.adotcode.nettysocket.account.dto.LoginInputDTO;
import com.adotcode.nettysocket.account.dto.LoginOutputDTO;
import com.adotcode.nettysocket.component.account.AccountComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 账户管理服务接口实现
 *
 * @author risfeng
 * @date 2020/9/8
 */
@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

    /**
     * 账户管理组件
     */
    @Resource
    private AccountComponent accountComponent;

    /**
     * 用户登录
     *
     * @param input 登录输入
     * @return 登录后的信息
     */
    @Override
    public LoginOutputDTO login(LoginInputDTO input) {
        return accountComponent.login(input);
    }

    /**
     * 注销登录
     *
     * @param token 用户token凭证
     * @return 注销成功与否
     */
    @Override
    public Boolean logout(String token) {
        return accountComponent.logout(token);
    }
}
