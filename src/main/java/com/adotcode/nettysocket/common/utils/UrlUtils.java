package com.adotcode.nettysocket.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Url工具类
 *
 * @author risfeng
 * @date 2020/9/6
 */
public class UrlUtils {

    private UrlUtils() {
    }

    /**
     * 解析url中的参数
     *
     * @param url URL地址
     * @return 参数map，可能返回null
     */
    public static Map<String, String> parseParameter(String url) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        String[] urlParts = url.split("\\?");
        //没有参数
        if (urlParts.length == 1) {
            return null;
        }
        //有参数
        String[] params = urlParts[1].split("&");
        Map<String, String> paramMap = new HashMap<>(params.length);
        for (String param : params) {
            String[] keyValue = param.split("=");
            paramMap.put(keyValue[0], keyValue[1]);
        }
        return paramMap;
    }
}
