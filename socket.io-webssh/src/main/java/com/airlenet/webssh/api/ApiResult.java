package com.airlenet.webssh.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ApiResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 成功标志
     */
    private boolean success = true;

    /**
     * 返回处理消息
     */
    private String message = "操作成功！";

    /**
     * 返回代码
     */
    private Integer code = 0;

    private Long total;

    private Long pages;
    private Long current;
    /**
     * 返回数据对象 data
     */
    private T content;

    public static <T> ApiResult<T> ok(T data) {
        ApiResult<T> r = new ApiResult<T>();
        r.setSuccess(true);
        r.setCode(200);
        r.setContent(data);
        return r;
    }

    public static ApiResult<Object> error(int code, String msg) {
        ApiResult<Object> r = new ApiResult<Object>();
        r.setCode(code);
        r.setMessage(msg);
        r.setSuccess(false);
        return r;
    }

    public static ApiResult<Object> error(String msg) {
        return error(500, msg);
    }

    public static ApiResult<Object> ok(String msg) {
        ApiResult<Object> r = new ApiResult<Object>();
        r.setSuccess(true);
        r.setCode(200);
        r.setMessage(msg);
        return r;
    }

    public static <T extends IPage<I>, I> ApiResult<List<I>> ok(T data) {
        ApiResult<List<I>> r = new ApiResult<List<I>>();
        r.setSuccess(true);
        r.setCode(200);
        r.setContent(data.getRecords());
        r.setTotal(data.getTotal());
        r.setPages(data.getPages());
        r.setCurrent(data.getCurrent());
        return r;
    }

    public ApiResult<T> total() {
        if (content instanceof List)
            this.total = ((List) content).size() * 1L;
        return this;
    }
}
