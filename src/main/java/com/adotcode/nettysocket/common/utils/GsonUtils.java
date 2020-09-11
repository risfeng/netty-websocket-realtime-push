package com.adotcode.nettysocket.common.utils;

import com.google.gson.Gson;

/**
 * Gson工具类
 *
 * @author risfeng
 * @date 2020/9/7
 */
public class GsonUtils {

    /**
     * 实例
     */
    private static volatile Gson instance;

    /**
     * 私有构造
     */
    private GsonUtils() {
    }

    /**
     * 获取实例
     *
     * @return {@link  Gson} 实例
     */
    public static Gson getInstance() {
        if (instance == null) {
            synchronized (GsonUtils.class) {
                if (instance == null) {
                    instance = new Gson();
                }
            }
        }
        return instance;
    }

}
