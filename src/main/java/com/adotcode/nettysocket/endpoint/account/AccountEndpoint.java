package com.adotcode.nettysocket.endpoint.account;

import com.adotcode.nettysocket.account.AccountService;
import com.adotcode.nettysocket.account.dto.LoginInputDTO;
import com.adotcode.nettysocket.account.dto.LoginOutputDTO;
import com.adotcode.nettysocket.common.result.HttpResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 账户管理Api
 *
 * @author risfeng
 * @date 2020/9/9
 */
@RestController
@ResponseBody
@RequestMapping("/api/v1/account")
public class AccountEndpoint {

    /**
     * 账户管理服务
     */
    @Resource
    private AccountService accountService;


    /**
     * 用户登录接口
     *
     * @param input 登录输入参数
     * @return 登录成功返回
     */
    @PostMapping("/login")
    public HttpResult<LoginOutputDTO> login(@RequestBody LoginInputDTO input) {
        return HttpResult.ok(accountService.login(input));
    }

    /**
     * 用户注销接口
     *
     * @param token token
     * @return 是否注销成功
     */
    @GetMapping("/logout")
    public HttpResult<Boolean> logout(@RequestParam("token") String token) {
        return HttpResult.ok(accountService.logout(token));
    }
}
