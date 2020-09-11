package com.adotcode.nettysocket.component.account;

import com.adotcode.nettysocket.common.constants.RedisKeys;
import com.adotcode.nettysocket.service.account.dto.LoginInputDTO;
import com.adotcode.nettysocket.service.account.dto.LoginOutputDTO;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.UUID;

/**
 * 账户管理组件
 *
 * @author risfeng
 * @date 2020/9/9
 */
@Component
public class AccountComponent {

    /**
     * 用户登录RedisTemplate
     */
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 用户登录
     *
     * @param input 登录输入
     * @return 登录后的信息
     */
    public LoginOutputDTO login(LoginInputDTO input) {
        LoginOutputDTO loginOutputDTO = LoginOutputDTO.builder()
                .name(MessageFormat.format("用户{0}", RandomStringUtils.random(5)))
                .userId(RandomUtils.nextInt())
                .token(UUID.randomUUID().toString().replace("-", ""))
                .build();
        HashOperations<String, String, LoginOutputDTO> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(RedisKeys.USER_LOGIN_REDIS_HASH_KEY, loginOutputDTO.getToken(), loginOutputDTO);
        return loginOutputDTO;
    }

    /**
     * 注销登录
     *
     * @param token 用户token凭证
     * @return 注销成功与否
     */
    public Boolean logout(String token) {
        Long deleteCount = redisTemplate.opsForHash().delete(RedisKeys.USER_LOGIN_REDIS_HASH_KEY, token);
        return 0 < deleteCount;
    }

    /**
     * 获取登录用户
     *
     * @param token 登录凭证
     * @return 登录用户信息
     */
    public LoginOutputDTO getLoginUser(String token) {
        HashOperations<String, String, LoginOutputDTO> hashOperations = redisTemplate.opsForHash();
        return hashOperations.get(RedisKeys.USER_LOGIN_REDIS_HASH_KEY, token);
    }

    /**
     * 获取登录用户Id
     *
     * @param token 登录凭证
     * @return 登录用户Id
     */
    public Integer getLoginUserId(String token) {
        if (StringUtils.isBlank(token)) {
            return 0;
        }
        LoginOutputDTO loginUser = getLoginUser(token);
        if (ObjectUtils.isEmpty(loginUser)) {
            return 0;
        }
        return loginUser.getUserId();
    }
}
