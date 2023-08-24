package com.sdu.base.custom.service.impl;

import com.sdu.base.common.response.exception.BusinessException;
import com.sdu.base.common.response.exception.ExceptionEnum;
import com.sdu.base.custom.common.constants.WebSocketConstant;
import com.sdu.base.custom.entity.vo.WebSocketAuthorization;
import com.sdu.base.custom.service.LoginService;
import com.sdu.base.custom.service.WebSocketService;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.ConcurrentHashMap;

@Service("WebSocketService")
@Slf4j
public class WebSocketServiceImpl implements WebSocketService, WebSocketConstant {

    private static final ConcurrentHashMap<Long, Channel> ONLINE_USERS = new ConcurrentHashMap<>(8);
    private static final ConcurrentHashMap<Channel, Long> CHANNEL_MAP = new ConcurrentHashMap<>(8);

    @Resource
    private LoginService loginService;

    @Override
    public void authorize(Channel channel, WebSocketAuthorization authorizationToken) {
        String token = authorizationToken.getToken();
        // 校验token
        boolean ok = loginService.authenticate(token);
        // 用户校验成功给用户登录
        if (ok) {
            Long uid = loginService.getUseridByToken(token);
            CHANNEL_MAP.put(channel, uid);
            ONLINE_USERS.put(uid, channel);
            channel.writeAndFlush(new TextWebSocketFrame(WebSocketConstant.AUTH_SUCCESS));
        } else {
            throw new BusinessException(ExceptionEnum.AUTH_FAILED);
        }
    }

    @Override
    public void offline(Channel channel) {
        Long uid = CHANNEL_MAP.get(channel);
        CHANNEL_MAP.remove(channel);
        ONLINE_USERS.remove(uid);
    }
}
