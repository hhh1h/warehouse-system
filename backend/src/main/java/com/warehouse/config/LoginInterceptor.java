package com.warehouse.config;

import com.warehouse.common.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    private static final List<String> EXCLUDE_PATHS = Arrays.asList(
        "/user/login",
        "/user/register"
    );

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        for (String path : EXCLUDE_PATHS) {
            if (uri.contains(path)) {
                return true;
            }
        }

        String token = request.getHeader("Authorization");

        if (!StringUtils.hasText(token)) {
            // 下载链接可能通过 query parameter 传递 token
            token = request.getParameter("token");
        }

        if (!StringUtils.hasText(token)) {
            sendUnauthorized(response, "请先登录");
            return false;
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            if (!jwtUtil.validateToken(token)) {
                sendUnauthorized(response, "Token无效或已过期");
                return false;
            }

            String username = jwtUtil.getUsernameFromToken(token);
            request.setAttribute("username", username);

            return true;

        } catch (Exception e) {
            sendUnauthorized(response, e.getMessage());
            return false;
        }
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"msg\":\"" + message + "\",\"data\":null}");
    }
}
