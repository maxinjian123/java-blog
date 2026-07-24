package com.example.springboot.interceptor;

import com.example.springboot.common.UserContext;
import com.example.springboot.common.annotation.Auth;
import com.example.springboot.common.exception.BusinessException;
import com.example.springboot.common.exception.ErrorCode;
import com.example.springboot.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Auth auth = handlerMethod.getMethodAnnotation(Auth.class);
        if (auth == null) {
            return true;
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未登录，请先登录");
        }

        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "会话已过期，请重新登录");
        }

        request.setAttribute("userId", user.getId());
        request.setAttribute("username", user.getUsername());
        request.setAttribute("role", user.getRole());

        String requiredRole = auth.role();
        if (!requiredRole.isEmpty() && !requiredRole.equals(user.getRole())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "权限不足，需要" + requiredRole + "角色");
        }

        UserContext.renewSession();

        return true;
    }
}