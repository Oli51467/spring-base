package com.sdu.base.common.interceptor;

import cn.hutool.core.util.StrUtil;
import com.sdu.base.common.domain.dto.RequestInfo;
import com.sdu.base.common.utils.JwtUtil;
import com.sdu.base.common.utils.UserContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class TokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("拦截token");
        //获取header的token参数
        String token = request.getHeader("token");
        if (StrUtil.isNotBlank(token)) {
            Long requestId = JwtUtil.getUidOrNull(token);
            log.info("拦截id: {}", requestId);
            RequestInfo requestInfo = UserContextHolder.get();
            if (null == requestInfo) requestInfo = new RequestInfo();
            requestInfo.setUid(requestId);
            UserContextHolder.set(new RequestInfo());
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContextHolder.remove();
    }
}
