package com.enchanted.interceptor;
import com.enchanted.util.JwtUtils;
import com.enchanted.util.StringUtil;
import io.jsonwebtoken.Claims;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SysInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        System.out.println(path);
        if (handler instanceof HandlerMethod) {
            String token = request.getHeader("Authorization");
            System.out.println("token:" + token);
            if (StringUtil.isEmpty(token)) {
                System.out.println("token为空！");
                throw new RuntimeException("签名验证不存在");
            } else {
                Claims claims = JwtUtils.validateJWT(token).getClaims();
                if (path.startsWith("/admin")) {
                    if (claims == null || !claims.getSubject().equals("admin") || !claims.getId().equals("-1")) {
                        throw new RuntimeException("鉴权失败！");
                    } else {
                        System.out.println("验证成功");
                        return true;
                    }
                } else {
                    if (claims == null) {
                        throw new RuntimeException("鉴权失败！");
                    } else {
                        System.out.println("验证成功");
                        return true;
                    }
                }

            }
        } else {
            return true;
        }
    }


}
