package com.adotcode.nettysocket.common.result;

import com.adotcode.nettysocket.common.utils.GsonUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Api响应对象
 *
 * @author risfeng
 * @date 2020/9/7
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HttpResult<T> {

    /**
     * 失败状态码
     */
    private static final Integer FAIL_STATUS = -1;

    /**
     * 成功状态码
     */
    private static final Integer SUCCESS_STATUS = HttpStatus.OK.value();

    /**
     * 成功返回消息
     */
    private static final String SUCCESS_MESSAGE = "请求成功。";

    /**
     * 状态
     */
    private Integer status;

    /**
     * 消息
     */
    private String message;

    /**
     * 结果返回
     */
    private T result;

    /**
     * 成功无数据返回
     *
     * @return {@link HttpResult} 结果返回
     */
    public static HttpResult<Object> ok() {
        return new HttpResult<>(SUCCESS_STATUS, SUCCESS_MESSAGE, null);
    }

    /**
     * 成功无数据返回
     *
     * @param message 成功消息
     * @return {@link HttpResult} 结果返回
     */
    public static HttpResult<Object> ok(String message) {
        return new HttpResult<>(SUCCESS_STATUS, message, null);
    }

    /**
     * 成功带数据返回
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return {@link HttpResult<T>} 结果返回
     */
    public static <T> HttpResult<T> ok(T data) {
        return new HttpResult<>(SUCCESS_STATUS, SUCCESS_MESSAGE, data);
    }

    /**
     * 成功带数据返回
     *
     * @param message 提示信息
     * @param data    数据
     * @param <T>     数据类型
     * @return {@link HttpResult<T>} 结果返回
     */
    public static <T> HttpResult<T> ok(String message, T data) {
        return new HttpResult<>(SUCCESS_STATUS, message, data);
    }

    /**
     * 失败返回
     *
     * @param message 失败信息
     * @return {@link HttpResult} 结果返回
     */
    public static HttpResult<Object> fail(String message) {
        return new HttpResult<>(FAIL_STATUS, message, null);
    }


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
