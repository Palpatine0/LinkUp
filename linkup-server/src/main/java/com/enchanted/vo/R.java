package com.enchanted.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.HashMap;
import java.util.Map;

public class R extends HashMap<String, Object> {

    private static final long serialVersionUID = 1L;

    public R() {
        put("code", 0);
    }

    // ok
    public static R ok() {
        return new R();
    }

    public static R ok(String message) {
        R r = new R();
        r.put("message", message);
        return r;
    }

    public static R ok(Map<String, Object> map) {
        R r = new R();
        r.putAll(map);
        return r;
    }


    // error
    public static R error() {
        return error(500, "Unknown error, please contact the administrator");
    }

    public static R error(String message) {
        return error(500, message);
    }

    public static R error(int code, String message) {
        R r = new R();
        r.put("code", code);
        r.put("message", message);
        return r;
    }


    public R put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public static <T> R paginate(Page<T> page) {
        return R.ok()
            .put("list", page.getRecords())
            .put("total", page.getTotal())
            .put("pages", page.getPages())
            .put("current", page.getCurrent());
    }
}
