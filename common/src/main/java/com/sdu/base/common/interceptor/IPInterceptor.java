package com.sdu.base.common.interceptor;

import cn.hutool.extra.servlet.ServletUtil;
import com.sdu.base.common.domain.dto.RequestInfo;
import com.sdu.base.common.utils.UserContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class IPInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        RequestInfo requestInfo = UserContextHolder.get();
        if (null == requestInfo) requestInfo = new RequestInfo();
        log.info("拦截ip: {}", ServletUtil.getClientIP(request));
        requestInfo.setIp(ServletUtil.getClientIP(request));
        UserContextHolder.set(requestInfo);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContextHolder.remove();
    }
}
