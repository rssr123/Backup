package com.example.fms.fms.config;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.method.HandlerMethod;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HandlerInterceptorImpl implements HandlerInterceptor {

    private static final ThreadLocal<HandlerMethod> currentHandlerMethod = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod) {
            currentHandlerMethod.set((HandlerMethod) handler);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) {
        currentHandlerMethod.remove();
    }

    public static HandlerMethod getCurrentHandlerMethod() {
        return currentHandlerMethod.get();
    }
}
