package com.sdu.base.custom.entity.dto;

import lombok.Data;

@Data
public class WebSocketBaseReq {

    private Integer type;

    // 每个请求包具体的数据
    private String data;
}
