package com.adotcode.nettysocket.common.constants;

/**
 * RedisKey常量列表
 *
 * @author risfeng
 * @date 2020/9/8
 */
public class RedisKeys {

    private RedisKeys() {
    }

    /**
     * 用户登录哈希Key
     */
    public static final String USER_LOGIN_REDIS_HASH_KEY = "user:login:hash:key";


    /**
     * 用户在线连接列表哈希Key
     */
    public static final String USER_ONLINE_CHANNEL_REDIS_HASH_KEY = "user:online:channel:hash:key";


    /**
     * socket在线连接握手列表哈希Key
     */
    public static final String SOCKET_SERVER_HAND_SHAKER_MAP_REDIS_HASH_KEY = "user:login:hash:key";
}
