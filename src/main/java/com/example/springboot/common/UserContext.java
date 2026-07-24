package com.example.springboot.common;

import com.example.springboot.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class UserContext {

    private static final String SESSION_USER_KEY = "currentUser";

    private static int sessionTimeout;

    private UserContext() {
    }

    @Value("${spring.session.timeout:2592000}")
    public void setSessionTimeout(int timeout) {
        UserContext.sessionTimeout = timeout;
    }

    public static HttpSession getSession() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        return request.getSession(false);
    }

    public static User getCurrentUser() {
        HttpSession session = getSession();
        if (session == null) {
            return null;
        }
        return (User) session.getAttribute(SESSION_USER_KEY);
    }

    public static String getCurrentUserId() {
        User user = getCurrentUser();
        if (user == null) {
            return null;
        }
        return user.getId();
    }

    public static String getCurrentUsername() {
        User user = getCurrentUser();
        if (user == null) {
            return null;
        }
        return user.getUsername();
    }

    public static String getCurrentUserRole() {
        User user = getCurrentUser();
        if (user == null) {
            return null;
        }
        return user.getRole();
    }

    public static void setCurrentUser(User user) {
        HttpSession session = getSession();
        if (session != null) {
            session.setAttribute(SESSION_USER_KEY, user);
        }
    }

    public static void removeCurrentUser() {
        HttpSession session = getSession();
        if (session != null) {
            session.removeAttribute(SESSION_USER_KEY);
        }
    }

    public static void renewSession() {
        HttpSession session = getSession();
        if (session != null) {
            session.setMaxInactiveInterval(sessionTimeout);
        }
    }
}