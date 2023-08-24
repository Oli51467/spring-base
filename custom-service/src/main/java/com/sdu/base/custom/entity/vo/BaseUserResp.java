package com.sdu.base.custom.entity.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BaseUserResp {

    private Long id;

    private String username;

    private String token;
}
