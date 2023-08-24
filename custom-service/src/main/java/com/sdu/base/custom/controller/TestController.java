package com.sdu.base.custom.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestController {

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String createOrder() {
        return "hello";
    }
}
