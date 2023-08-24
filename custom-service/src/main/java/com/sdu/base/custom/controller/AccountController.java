package com.sdu.base.custom.controller;

import com.sdu.base.common.response.ResponseResult;
import com.sdu.base.custom.entity.dto.RegisterReq;
import com.sdu.base.custom.service.AccountService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/account")
@Api("Account")
public class AccountController {

    @Resource
    private AccountService accountService;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseResult register(@RequestBody RegisterReq req) {
        return accountService.register(req);
    }
}
