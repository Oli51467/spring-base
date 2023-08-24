package com.sdu.base.custom.service;

import com.sdu.base.custom.entity.vo.WebSocketAuthorization;
import io.netty.channel.Channel;

public interface WebSocketService {

    void authorize(Channel channel, WebSocketAuthorization authorizationToken);

    void offline(Channel channel);
}
