package com.sdu.base.custom.entity.dto;

import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;

@Data
@Getter
public class RegisterReq {

    @NotEmpty(message = "用户名不能为空")
    private String username;

    @NotEmpty(message = "密码不能为空")
    private String password;
}
