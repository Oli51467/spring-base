package com.sdu.base.gateway.config;

import com.sdu.base.gateway.GatewayApplication;
import com.sdu.base.gateway.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class LoginTokenFilter implements Ordered, GlobalFilter {

    private static final Logger log = LoggerFactory.getLogger(GatewayApplication.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 排除不需要拦截的请求
        if (path.contains("/test")) {
            log.info("不需要登录验证：{}", path);
            return chain.filter(exchange);
        }
        // 获取header的token参数
        String token = exchange.getRequest().getHeaders().getFirst("token");
        if (token == null || token.isEmpty()) {
            log.info("token为空，请求被拦截");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 校验token是否有效，包括token是否被改过，是否过期
        boolean validate = JwtUtil.validate(token);
        if (validate) {
            return chain.filter(exchange);
        } else {
            log.warn("token无效，请求被拦截");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    /**
     * 优先级设置  值越小  优先级越高
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
